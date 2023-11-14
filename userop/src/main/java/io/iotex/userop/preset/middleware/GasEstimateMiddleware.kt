package io.iotex.userop.preset.middleware

import io.iotex.userop.api.IUserOperationMiddleware
import io.iotex.userop.api.IUserOperationMiddlewareCtx
import io.iotex.userop.provider.JsonRpcProvider
import org.web3j.protocol.core.Response
import org.web3j.utils.Numeric
import java.math.BigInteger

class UserOpGasEstimate : Response<UserOpGasEstimate.GasEstimate>() {

    fun getGasEstimate(): GasEstimate? {
        return result
    }

    class GasEstimate {
        val preVerificationGas: String = "0x0"
        val verificationGasLimit: String = "0x0"
        val callGasLimit: String = "0x0"
    }
}


class GasEstimateMiddleware(val provider: JsonRpcProvider) : IUserOperationMiddleware {

    private val FUN_ESTIMATE_USER_OPERATIONGAS = "eth_estimateUserOperationGas"

    override fun process(ctx: IUserOperationMiddlewareCtx) {
        val gasEstimate = provider.send(
            FUN_ESTIMATE_USER_OPERATIONGAS,
            listOf(ctx.op, ctx.entryPoint),
            UserOpGasEstimate::class.java
        ).getGasEstimate()
        gasEstimate?.run {
            ctx.op.preVerificationGas = Numeric.prependHexPrefix(BigInteger(preVerificationGas).toString(16))
            ctx.op.verificationGasLimit = Numeric.prependHexPrefix(BigInteger(verificationGasLimit).toString(16))
            ctx.op.callGasLimit = Numeric.prependHexPrefix(BigInteger(callGasLimit).toString(16))
        }

    }

}