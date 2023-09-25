package io.iotex.userop

import io.iotex.userop.api.IUserOperationMiddleware
import java.math.BigInteger

data class PresetBuilderOpts(
    val entryPoint: String,
    val factory: String,
    val salt: BigInteger? = null,
    val bundlerRpc: String,
    val paymasterMiddleware: IUserOperationMiddleware? = null
)