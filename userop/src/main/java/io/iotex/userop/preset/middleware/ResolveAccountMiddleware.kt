package io.iotex.userop.preset.middleware

import io.iotex.userop.api.IUserOperationMiddleware
import io.iotex.userop.api.IUserOperationMiddlewareCtx
import io.iotex.userop.contract.EntryPoint

class ResolveAccountMiddleware(val entryPont: EntryPoint, val initCode: String): IUserOperationMiddleware {

    override fun process(ctx: IUserOperationMiddlewareCtx) {
        ctx.op.nonce = entryPont.getNonce(ctx.op.sender).send().toString(16)
        ctx.op.initCode = initCode
    }

}