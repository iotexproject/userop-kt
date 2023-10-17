package io.iotex.test

import io.iotex.userop.api.ISigner
import org.web3j.utils.Numeric

class P256Signer: ISigner {
    override val address: String
        get() = "0x00000000000000000000000000000000"

    override val publicKey: ByteArray
        get() = ByteArray(64)

    override fun sign(data: ByteArray): ByteArray {
        return Numeric.hexStringToByteArray(P256KeyManager.signData(data))
    }
}