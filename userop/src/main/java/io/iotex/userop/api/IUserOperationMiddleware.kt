package io.iotex.userop.api

interface IUserOperationMiddleware {

    fun process(ctx: IUserOperationMiddlewareCtx)

}