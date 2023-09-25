package io.iotex.userop.preset.middleware

import io.iotex.userop.api.ISigner
import io.iotex.userop.api.IUserOperationMiddleware
import io.iotex.userop.api.IUserOperationMiddlewareCtx
import org.web3j.utils.Numeric

class SignatureMiddleware(val signer: ISigner): IUserOperationMiddleware {
    override fun process(ctx: IUserOperationMiddlewareCtx) {
        val signature = signer.sign(Numeric.hexStringToByteArray(ctx.getUserOpHash()))
        ctx.op.signature = Numeric.toHexString(signature)
    }
}