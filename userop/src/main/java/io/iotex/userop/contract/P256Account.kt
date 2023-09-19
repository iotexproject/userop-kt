package io.iotex.userop.contract

import org.web3j.protocol.Web3j
import org.web3j.tx.Contract
import org.web3j.tx.ReadonlyTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider

class P256Account(
    contract: String,
    web3j: Web3j,
    transactionManager: TransactionManager,
    contractGasProvider: ContractGasProvider
) : Contract(BINARY, contract, web3j, transactionManager, contractGasProvider) {

    companion object {
        const val BINARY = "Bin file was not provided"

        fun load(
            contract: String,
            web3j: Web3j
        ): P256Account {
            val transactionManager = ReadonlyTransactionManager(web3j, contract)
            val contractGasProvider = DefaultGasProvider()
            return P256Account(contract, web3j, transactionManager, contractGasProvider)
        }
    }

}