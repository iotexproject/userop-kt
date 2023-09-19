package io.iotex.userop.api

import io.iotex.userop.UserOperation
import java.math.BigInteger

interface IUserOperationMiddlewareCtx {

    val op: UserOperation
    val entryPoint: String
    val chainId: BigInteger

    fun getUserOpHash(): String
}