package io.iotex.userop.preset.middleware

import io.iotex.userop.api.IUserOperationMiddleware
import io.iotex.userop.api.IUserOperationMiddlewareCtx
import io.iotex.userop.provider.JsonRpcProvider
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthBlock
import org.web3j.protocol.core.methods.response.EthGasPrice
import org.web3j.protocol.core.methods.response.EthMaxPriorityFeePerGas
import org.web3j.utils.Numeric
import java.math.BigInteger

class GasPriceMiddleware(val provider: JsonRpcProvider) : IUserOperationMiddleware {

    override fun process(ctx: IUserOperationMiddlewareCtx) {
        runCatching {
            val gasList = eip1559GasPrice()
            ctx.op.maxFeePerGas = Numeric.prependHexPrefix(gasList[0].toString(16))
            ctx.op.maxPriorityFeePerGas = Numeric.prependHexPrefix(gasList[1].toString(16))
        }.onFailure {
            val gasList = legacyGasPrice()
            ctx.op.maxFeePerGas = Numeric.prependHexPrefix(gasList[0].toString(16))
            ctx.op.maxPriorityFeePerGas = Numeric.prependHexPrefix(gasList[1].toString(16))
        }
    }

    private fun eip1559GasPrice(): List<BigInteger> {
        val fee = provider.send(
            "eth_maxPriorityFeePerGas",
            emptyList(),
            EthMaxPriorityFeePerGas::class.java
        ).maxPriorityFeePerGas
        val block = provider.send(
            "eth_getBlockByNumber",
            listOf(DefaultBlockParameterName.LATEST.value, false),
            EthBlock::class.java
        ).block
        val buffer = fee.div(BigInteger("100")).multiply(BigInteger("13"))
        val maxPriorityFeePerGas = fee.add(buffer)
        val maxFeePerGas = if (block.baseFeePerGas != null) {
            block.baseFeePerGas.multiply(BigInteger("2").add(maxPriorityFeePerGas))
        } else maxPriorityFeePerGas
        return listOf(
            maxFeePerGas,
            maxPriorityFeePerGas
        )
    }

    private fun legacyGasPrice(): List<BigInteger> {
        val gas = provider.send("eth_gasPrice", emptyList(), EthGasPrice::class.java).gasPrice
        return listOf(
            gas,
            gas
        )
    }

}