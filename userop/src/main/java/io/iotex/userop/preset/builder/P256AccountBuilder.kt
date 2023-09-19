package io.iotex.userop.preset.builder

import io.iotex.userop.PresetBuilderOpts
import io.iotex.userop.api.ISigner
import io.iotex.userop.UserOperationBuilder
import io.iotex.userop.constants.ACCOUNT_ADDRESS
import io.iotex.userop.constants.ENTRY_POINT
import io.iotex.userop.contract.EntryPoint
import io.iotex.userop.contract.P256Account
import io.iotex.userop.contract.P256AccountFactory
import io.iotex.userop.preset.middleware.GasEstimateMiddleware
import io.iotex.userop.preset.middleware.GasPriceMiddleware
import io.iotex.userop.preset.middleware.ResolveAccountMiddleware
import io.iotex.userop.preset.middleware.SignatureMiddleware
import io.iotex.userop.preset.middleware.VerifyingPaymaster
import io.iotex.userop.provider.BundlerJsonRpcProvider
import io.iotex.userop.utils.cleanHexPrefix
import io.iotex.userop.utils.toHexByteArray
import io.iotex.userop.utils.toHexString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Hash
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import java.math.BigInteger

class P256AccountBuilder(val rpcUrl: String, val signer: ISigner, val opts: PresetBuilderOpts?) : UserOperationBuilder() {

    private val provider = BundlerJsonRpcProvider(rpcUrl, opts?.bundlerRpc)
    private val web3j = Web3j.build(HttpService(rpcUrl))
    private val accountFactory = P256AccountFactory.load(opts?.factory ?: ACCOUNT_ADDRESS, web3j)
    private val entryPoint = EntryPoint.load(opts?.entryPoint ?: ENTRY_POINT, web3j)
    private var proxy: P256Account? = null

    fun execute(to: String, value: BigInteger, data: String): String {
        val function = Function(
            "execute",
            listOf(
                Address(to),
                Uint256(value),
                DynamicBytes(data.toHexByteArray())
            ),
            listOf()
        )
        this.callData = FunctionEncoder.encode(function)
        return this.callData
    }

    fun executeBatch(to: String, value: BigInteger, data: String): String {
        val function = Function(
            "executeBatch",
            listOf(
                Address(to),
                Uint256(value),
                DynamicBytes(data.toHexByteArray())
            ),
            listOf()
        )
        this.callData = FunctionEncoder.encode(function)
        return this.callData
    }

    companion object {

        suspend fun init(sender: String?, rpcUrl: String, signer: ISigner, opts: PresetBuilderOpts?): P256AccountBuilder {
            return withContext(Dispatchers.IO) {
                val instance = P256AccountBuilder(rpcUrl, signer, opts)
                if (sender.isNullOrBlank()) {
                    instance.initCode = initCode(signer, opts, instance.accountFactory)
                    instance.entryPoint.getSenderAddress(instance.initCode.toHexByteArray())
                    val address = instance.accountFactory.getAddress(signer.publicKey, opts?.salt ?: BigInteger.ZERO).send()
                    instance.proxy = P256Account.load(address, instance.web3j)
                    instance.sender = address
                } else {
                    instance.proxy = P256Account.load(sender, instance.web3j)
                    instance.sender = sender
                }
                instance.apply {
                    initCode = initCode(signer, opts, instance.accountFactory)
                    signature = signer.sign(Hash.sha3("0xdead".toByteArray())).toHexString()
                    useMiddleware(
                        ResolveAccountMiddleware(instance.entryPoint, instance.initCode),
                        GasPriceMiddleware(instance.provider),
                        opts?.paymasterMiddleware ?: GasEstimateMiddleware(instance.provider),
                        SignatureMiddleware(instance.signer)
                    )
                }
            }
        }

        suspend fun init(rpcUrl: String, signer: ISigner, opts: PresetBuilderOpts): P256AccountBuilder {
            return init(null, rpcUrl, signer, opts)
        }

        private fun initCode(signer: ISigner, opts: PresetBuilderOpts?, accountFactory: P256AccountFactory): String {
            val function = Function(
                "createAccount",
                listOf<Type<*>>(
                    DynamicBytes(signer.publicKey),
                    Uint256(opts?.salt ?: BigInteger.ZERO)
                ),
                listOf(object : TypeReference<Address>() {})
            )
            val data = FunctionEncoder.encode(function)
            return "${accountFactory.contractAddress}${data.cleanHexPrefix()}"
        }
    }

}