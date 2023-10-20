package io.iotex.userop.preset.middleware

import io.iotex.userop.api.IUserOperationMiddleware
import io.iotex.userop.api.IUserOperationMiddlewareCtx
import io.iotex.userop.contract.EntryPoint
import org.web3j.utils.Numeric
import java.math.BigInteger

class ResolveAccountMiddleware(val entryPont: EntryPoint, val initCode: String): IUserOperationMiddleware {

    override fun process(ctx: IUserOperationMiddlewareCtx) {
        val nonce = entryPont.getNonce(ctx.op.sender).send()
        ctx.op.initCode = if (nonce == BigInteger.ZERO) {
            initCode
        } else "0x"
        ctx.op.nonce = Numeric.prependHexPrefix(nonce.toString(16))
    }

}