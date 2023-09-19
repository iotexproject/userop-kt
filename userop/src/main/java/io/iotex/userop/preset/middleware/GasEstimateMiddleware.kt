package io.iotex.userop.preset.middleware

import io.iotex.userop.api.IUserOperationMiddleware
import io.iotex.userop.api.IUserOperationMiddlewareCtx
import io.iotex.userop.provider.JsonRpcProvider
import org.web3j.protocol.core.Response
import org.web3j.protocol.core.methods.response.EthBlock

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

    override fun process(ctx: IUserOperationMiddlewareCtx) {
        val gasEstimate = provider.send(
            "eth_estimateUserOperationGas",
            listOf(),
            UserOpGasEstimate::class.java
        ).getGasEstimate()

        gasEstimate?.run {
            ctx.op.preVerificationGas = preVerificationGas;
            ctx.op.verificationGasLimit = verificationGasLimit
            ctx.op.callGasLimit = callGasLimit
        }

    }

}