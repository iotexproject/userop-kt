package io.iotex.userop.preset.middleware

import io.iotex.userop.provider.JsonRpcProvider
import io.iotex.userop.api.IUserOperationMiddleware
import io.iotex.userop.api.IUserOperationMiddlewareCtx
import org.web3j.protocol.core.Response

class VerifyingPaymaster(private val paymasterRpc: String): IUserOperationMiddleware {

    override fun process(ctx: IUserOperationMiddlewareCtx) {
        val provider = JsonRpcProvider(paymasterRpc)
        val pm = provider.send(
            "pm_sponsorUserOperation",
            listOf(ctx.op, ctx.entryPoint, ""),
            Response<VerifyingPaymasterResult>()::class.java
        ).result

        pm?.run {
            ctx.op.paymasterAndData = paymasterAndData
            ctx.op.callGasLimit = callGasLimit
            ctx.op.preVerificationGas = preVerificationGas
            ctx.op.verificationGasLimit = verificationGasLimit
        }
    }

}

class VerifyingPaymasterResult {
    val paymasterAndData: String = "0x"
    val preVerificationGas: String = "0x0"
    val verificationGasLimit: String = "0x0"
    val callGasLimit: String = "0x0"
}