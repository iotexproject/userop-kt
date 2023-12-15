package io.iotex.test.compat

import io.iotex.test.BUNDLER_RPC
import io.iotex.test.ENTRY_POINT
import io.iotex.test.P256Signer
import io.iotex.test.RPC_URL
import io.iotex.userop.Client
import io.iotex.userop.PresetBuilderOpts
import io.iotex.userop.api.IClient
import io.iotex.userop.api.SendUserOperationResponse
import io.iotex.userop.preset.builder.P256AccountBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.math.BigInteger

object UseropManager {


    private lateinit var accountBuilder: P256AccountBuilder
    private lateinit var client: IClient

    fun initClient(): Deferred<IClient> {
        return GlobalScope.async(Dispatchers.IO) {
            client = Client.init(rpcUrl = RPC_URL, entryPoint = ENTRY_POINT, bundlerRpc = BUNDLER_RPC)
            return@async client
        }
    }

    fun initP256AccountBuilder(): Deferred<P256AccountBuilder> {
        return GlobalScope.async(Dispatchers.IO) {
            accountBuilder = P256AccountBuilder.init(
                null,
                "https://babel-api.mainnet.iotex.io",
                P256Signer(),
                PresetBuilderOpts(
                    "0xc3527348De07d591c9d567ce1998eFA2031B8675",
                    "0x1d502383056C8cc00C7f70AC2B9E774cC0E1cC3D",
                    BigInteger("100"),
                    "https://bundler.mainnet.w3bstream.com"
                )
            )
            return@async accountBuilder
        }
    }

    fun sendCurrency(): Deferred<String> {
         return GlobalScope.async(Dispatchers.IO) {
            val to = "Receipt address"
            val value = BigInteger.ONE
            val data = "0x"
            val callData = accountBuilder.execute(to, value, data)
            println("callData: $callData")
            val response = client.sendUserOperation(accountBuilder)
            val userOpHash = response.userOpHash
            val txHash = response.wait()
            println("userOpHash: $userOpHash")
            println("txHash: $txHash")
             return@async txHash?.transactionHash ?: ""
        }
    }

}