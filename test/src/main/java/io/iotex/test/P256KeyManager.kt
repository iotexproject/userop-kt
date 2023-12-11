package io.iotex.test

import org.bouncycastle.asn1.DERBitString
import org.bouncycastle.asn1.DERSequence
import org.web3j.abi.TypeEncoder
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.Signature
import java.security.cert.CertificateException

internal const val ANDROID_KEY_STORE = "AndroidKeyStore"
internal const val ABSTRACT_ACCOUNT_ALIAS = "abstract_account_alias"
internal const val ALGORITHM_SIGN = "SHA256withECDSA"
internal const val ALGORITHM_EC = "secp256r1"

object P256KeyManager {

    @Throws(CertificateException::class)
    fun signData(data: ByteArray): String {
        val ks = getKeyStore()

        val entry = ks.getEntry(ABSTRACT_ACCOUNT_ALIAS, null)
        if (entry !is KeyStore.PrivateKeyEntry) {
            throw CertificateException("Not an instance of a PrivateKeyEntry")
        }

        val signedData = Signature.getInstance(ALGORITHM_SIGN).run {
            initSign(entry.privateKey)
            update(data)
            sign()
        }

        return generateSignature(Numeric.toHexString(signedData))
    }

    @Throws(CertificateException::class)
    private fun generateSignature(msg: String): String {
        val sigStr = Numeric.cleanHexPrefix(msg)
        if (sigStr.length < 8) throw CertificateException("Signature message is too short")
        val len = sigStr.substring(6, 8).toBigInteger(16)
            .times(BigInteger.valueOf(2)).toInt()
        val middle = sigStr.substring(8)
        val arg01 = middle.substring(0, len).toBigInteger(16)
        val arg02 = middle.substring(len + 4).toBigInteger(16)
        val arg01Encode = TypeEncoder.encodePacked(Uint256(arg01))
        val arg02Encode = TypeEncoder.encodePacked(Uint256(arg02))
        return Numeric.prependHexPrefix(arg01Encode + arg02Encode)
    }


    private fun getKeyStore() = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
        load(null)
        if (!containsAlias(ABSTRACT_ACCOUNT_ALIAS)) {
            /*
            // If your operating environment is Android, please use the following code segment:

            val kpg = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_EC, ANDROID_KEY_STORE
            )

            val parameterSpec = KeyGenParameterSpec.Builder(
                ABSTRACT_ACCOUNT_ALIAS,
                KeyProperties.PURPOSE_SIGN
            ).run {
                setAlgorithmParameterSpec(ECGenParameterSpec(ALGORITHM_EC))
                setDigests(KeyProperties.DIGEST_SHA256)
                build()
            }

            kpg.initialize(parameterSpec)
            */
            val kpg = KeyPairGenerator.getInstance(ANDROID_KEY_STORE)
            kpg.generateKeyPair()
        }
    }

    @Throws(CertificateException::class)
    fun getPubKey(): String? {
        val ks = getKeyStore()
        val pubKeyEncoded = ks.getCertificate(ABSTRACT_ACCOUNT_ALIAS).publicKey?.encoded
            ?: return null
        val subjectPublicKey = DERSequence.getInstance(pubKeyEncoded).getObjectAt(1) as DERBitString
        return Numeric.prependHexPrefix(Numeric.toHexString(subjectPublicKey.bytes).substring(4))
    }

}