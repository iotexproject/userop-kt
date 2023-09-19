package io.iotex.userop.provider

import org.web3j.protocol.core.Response

class BundlerJsonRpcProvider(rpcUrl: String, bundlerRpc: String?) : JsonRpcProvider(rpcUrl) {
    private val bundlerProvider: JsonRpcProvider
    private val bundlerMethods = listOf(
        "eth_sendUserOperation",
        "eth_estimateUserOperationGas",
        "eth_getUserOperationByHash",
        "eth_getUserOperationReceipt",
        "eth_supportedEntryPoints",
    )

    init {
        bundlerProvider = if (!bundlerRpc.isNullOrBlank()) {
            JsonRpcProvider(bundlerRpc)
        } else {
            JsonRpcProvider(rpcUrl)
        }
    }

    override fun <T : Response<*>> send(
        method: String,
        params: List<Any>,
        returnType: Class<T>
    ): T {
        if (bundlerMethods.contains(method)) {
            return bundlerProvider.send(method, params, returnType)
        }
        return super.send(method, params, returnType)
    }

}