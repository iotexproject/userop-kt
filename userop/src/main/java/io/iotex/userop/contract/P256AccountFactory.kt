package io.iotex.userop.contract

import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.RemoteFunctionCall
import org.web3j.tx.Contract
import org.web3j.tx.ReadonlyTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger

class P256AccountFactory(
    contract: String,
    web3j: Web3j,
    transactionManager: TransactionManager,
    contractGasProvider: ContractGasProvider
) : Contract(BINARY, contract, web3j, transactionManager, contractGasProvider) {

    fun createAddress(pubKey: ByteArray, salt: BigInteger) : RemoteFunctionCall<String> {
        val function = Function(
            FUNC_CREATE_ADDRESS,
            listOf<Type<*>>(
                DynamicBytes(pubKey),
                Uint256(salt)
            ),
            listOf(object : TypeReference<Address>() {})
        )
        return executeRemoteCallSingleValueReturn(function, String::class.java)
    }

    companion object {
        const val BINARY = "Bin file was not provided"
        const val FUNC_CREATE_ADDRESS = "createAccount"

        fun load(
            contract: String,
            web3j: Web3j
        ): P256AccountFactory {
            val transactionManager = ReadonlyTransactionManager(web3j, contract)
            val contractGasProvider = DefaultGasProvider()
            return P256AccountFactory(contract, web3j, transactionManager, contractGasProvider)
        }
    }
}