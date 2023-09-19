package io.iotex.userop.contract

import org.web3j.abi.EventEncoder
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.Event
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Bytes16
import org.web3j.abi.datatypes.generated.Bytes20
import org.web3j.abi.datatypes.generated.Bytes32
import org.web3j.abi.datatypes.generated.Uint192
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.RemoteFunctionCall
import org.web3j.protocol.core.methods.request.EthFilter
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthLog
import org.web3j.tx.Contract
import org.web3j.tx.ReadonlyTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger

class EntryPoint(
    contract: String,
    web3j: Web3j,
    transactionManager: TransactionManager,
    contractGasProvider: ContractGasProvider
) : Contract(BINARY, contract, web3j, transactionManager, contractGasProvider) {

    fun getSenderAddress(initCode: ByteArray): RemoteFunctionCall<Nothing> {
        val function = Function(
            FUNC_GET_SENDER_ADDRESS,
            listOf(
                DynamicBytes(initCode),
            ),
            listOf()
        )
        return executeRemoteCallSingleValueReturn(function, Nothing::class.java)
    }

    fun getNonce(address: String): RemoteFunctionCall<BigInteger> {
        val function = Function(
            FUNC_GET_NONCE,
            listOf(Address(address), Uint192(BigInteger.ZERO)),
            listOf(object : TypeReference<Uint256>() {})
        )
        return executeRemoteCallSingleValueReturn(function, BigInteger::class.java)
    }

    fun getUserOperationEvent(
        fromBlock: DefaultBlockParameter,
        toBlock: DefaultBlockParameter
    ): EthLog? {
        val event = Event(
            "UserOperationEvent",
            listOf(
                object : TypeReference<Bytes32>(true) {},
                object : TypeReference<Address>(true) {},
                object : TypeReference<Address>(true) {},
                object : TypeReference<Uint256>(true) {},
                object : TypeReference<Bool>(true) {},
                object : TypeReference<Uint256>(true) {},
                object : TypeReference<Uint256>(true) {}
            )
        )
        val ethFilter = EthFilter(
            fromBlock,
            toBlock,
            contractAddress
        )
        ethFilter.addSingleTopic(EventEncoder.encode(event))
        return web3j.ethGetLogs(ethFilter).send()
    }

    companion object {
        const val BINARY = "Bin file was not provided"
        const val FUNC_GET_NONCE = "getNonce"
        const val FUNC_GET_SENDER_ADDRESS = "getSenderAddress"

        fun load(
            contract: String,
            web3j: Web3j
        ): EntryPoint {
            val transactionManager = ReadonlyTransactionManager(web3j, contract)
            val contractGasProvider = DefaultGasProvider()
            return EntryPoint(contract, web3j, transactionManager, contractGasProvider)
        }
    }
}