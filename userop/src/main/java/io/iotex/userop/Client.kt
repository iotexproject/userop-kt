package io.iotex.userop

import io.iotex.userop.api.IClient
import io.iotex.userop.api.IUserOperationBuilder
import io.iotex.userop.api.SendUserOperationResponse
import io.iotex.userop.contract.EntryPoint
import io.iotex.userop.provider.BundlerJsonRpcProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.Response
import org.web3j.protocol.core.methods.response.Log
import org.web3j.protocol.http.HttpService
import java.math.BigInteger

class Client(
    val rpcUrl: String,
    val entryPoint: String,
    val overrideBundlerRpc: String,
) : IClient {

    private val provider = BundlerJsonRpcProvider(rpcUrl, overrideBundlerRpc)
    private val web3j = Web3j.build(HttpService(rpcUrl))
    private val entryPointContract = EntryPoint.load(entryPoint, web3j)
    private var chainId = BigInteger.ONE

    private val waitTimeoutMs = 30000L
    private val waitIntervalMs = 5000L

    override fun buildUserOperation(builder: IUserOperationBuilder): UserOperation {
        return builder.buildOp(entryPoint, chainId)
    }

    override suspend fun sendUserOperation(builder: IUserOperationBuilder, userOp: UserOperation?): SendUserOperationResponse {
        val op = userOp ?: this.buildUserOperation(builder)
        val response = provider.send(
            "eth_sendUserOperation",
            listOf(op, entryPoint),
            Response<String>()::class.java
        )
        builder.resetOp()

        return SendUserOperationResponse(response.error?.message, response.result) {
            withContext(Dispatchers.IO) {
                val end = System.currentTimeMillis() + waitTimeoutMs
                while (System.currentTimeMillis() < end) {
                    val blockNumber = web3j.ethBlockNumber().send().blockNumber ?: BigInteger.ZERO
                    val startNumber = if (blockNumber - BigInteger("100") > BigInteger.ZERO) {
                        blockNumber - BigInteger("100")
                    } else {
                        BigInteger.ZERO
                    }
                    val ethLog = entryPointContract.getUserOperationEvent(
                        DefaultBlockParameter.valueOf(startNumber),
                        DefaultBlockParameterName.LATEST
                    )
                    if (ethLog?.logs?.isNotEmpty() == true) {
                        return@withContext ethLog.logs?.lastOrNull() as? Log
                    }
                    delay(waitIntervalMs)
                }
                null
            }
        }
    }

    companion object {

        fun init(rpcUrl: String, entryPoint: String, bundlerRpc: String): Client {
            val instance = Client(rpcUrl, entryPoint, bundlerRpc)
            instance.chainId = instance.web3j.ethChainId().send()?.chainId ?: BigInteger.ONE
            return instance
        }

    }
}