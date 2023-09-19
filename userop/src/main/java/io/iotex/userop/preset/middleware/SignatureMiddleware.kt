package io.iotex.userop.preset.middleware

import io.iotex.userop.api.ISigner
import io.iotex.userop.api.IUserOperationMiddleware
import io.iotex.userop.api.IUserOperationMiddlewareCtx
import io.iotex.userop.utils.toHexByteArray
import io.iotex.userop.utils.toHexString

class SignatureMiddleware(val signer: ISigner): IUserOperationMiddleware {
    override fun process(ctx: IUserOperationMiddlewareCtx) {
        val signature = signer.sign(ctx.getUserOpHash().toHexByteArray())
        ctx.op.signature = signature.toHexString()
    }
}