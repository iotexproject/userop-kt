package io.iotex.userop.api

import org.web3j.protocol.core.methods.response.Log

data class SendUserOperationResponse(
    val error: String?,
    val userOpHash: String?,
    val wait: (suspend () -> Log?)
)