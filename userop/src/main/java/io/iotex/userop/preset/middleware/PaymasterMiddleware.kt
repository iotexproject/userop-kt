package io.iotex.userop.preset.middleware

import io.iotex.userop.api.IUserOperationMiddleware
import io.iotex.userop.api.IUserOperationMiddlewareCtx
import io.iotex.userop.provider.JsonRpcProvider
import org.web3j.protocol.core.Response

class PaymasterMiddleware(private val paymasterRpc: String): IUserOperationMiddleware {

    private val FUN_SPONSOR_USER_OPERATION = "pm_sponsorUserOperation"

    override fun process(ctx: IUserOperationMiddlewareCtx) {
        val provider = JsonRpcProvider(paymasterRpc)
        val pm = provider.send(
            FUN_SPONSOR_USER_OPERATION,
            listOf(ctx.op, ctx.entryPoint, ""),
            VerifyingPaymasterResponse::class.java
        ).getPaymaster()
        pm?.run {
            ctx.op.paymasterAndData = paymasterAndData
            ctx.op.callGasLimit = callGasLimit
            ctx.op.preVerificationGas = preVerificationGas
            ctx.op.verificationGasLimit = verificationGasLimit
        }
    }

}

class VerifyingPaymasterResponse: Response<VerifyingPaymasterResponse.VerifyingPaymaster>() {

    fun getPaymaster(): VerifyingPaymaster? {
        return result
    }

    class VerifyingPaymaster {
        val paymasterAndData: String = "0x"
        val preVerificationGas: String = "0x0"
        val verificationGasLimit: String = "0x0"
        val callGasLimit: String = "0x0"
    }

}

