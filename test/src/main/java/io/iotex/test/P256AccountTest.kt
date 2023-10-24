package io.iotex.test

import io.iotex.userop.Client
import io.iotex.userop.PresetBuilderOpts
import io.iotex.userop.api.IClient
import io.iotex.userop.preset.builder.P256AccountBuilder
import io.iotex.userop.preset.middleware.PaymasterMiddleware
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import java.math.BigInteger

const val RPC_URL = "https://babel-api.testnet.iotex.io"
const val ENTRY_POINT = "0xc3527348De07d591c9d567ce1998eFA2031B8675"
const val ACCOUNT_FACTORY = "0x8D6f2C1839c9bd633A1972Ac50B24D8EA188a372"
const val BUNDLER_RPC = "https://bundler.testnet.w3bstream.com"
const val PAYMASTER_RPC = "https://paymaster.testnet.w3bstream.com/rpc/API_KEY"

fun main() {

    MainScope().launch {

        // Create account
        val builder = P256AccountBuilder.init(null, RPC_URL, P256Signer(), PresetBuilderOpts(
            entryPoint = ENTRY_POINT,
            factory = ACCOUNT_FACTORY,
            salt = BigInteger.ZERO,
            bundlerRpc = BUNDLER_RPC,
            // use paymaster
            paymasterMiddleware = PaymasterMiddleware(PAYMASTER_RPC)
        ))
        val address = builder.sender
        println("Account address: $address")

        val client = Client.init(rpcUrl = RPC_URL, entryPoint = ENTRY_POINT, bundlerRpc = BUNDLER_RPC)

        // Send currency
        sendCurrency(builder, client)

        // Send ERC20
        sendERC20(builder, client)
    }

}

suspend fun sendCurrency(builder: P256AccountBuilder, client: IClient) {
    val to = "Receipt address"
    val value = BigInteger.ONE
    val data = "0x"
    val callData = builder.execute(to, value, data)
    println("callData: $callData")
    val response = client.sendUserOperation(builder)
    val userOpHash = response.userOpHash
    val txHash = response.wait()
    println("userOpHash: $userOpHash")
    println("txHash: $txHash")
}

suspend fun sendERC20(builder: P256AccountBuilder, client: IClient) {
    val erc20Contract = "Contract address"
    val to = "Receipt address"
    val value = BigInteger.ONE
    val function = Function(
        "transfer",
        listOf(
            Address(to),
            Uint256(value)
        ),
        listOf()
    )
    val data = FunctionEncoder.encode(function)
    val callData = builder.execute(erc20Contract, value, data)
    println("callData: $callData")
    val response = client.sendUserOperation(builder)
    val userOpHash = response.userOpHash
    val txHash = response.wait()
    println("userOpHash: $userOpHash")
    println("txHash: $txHash")
}