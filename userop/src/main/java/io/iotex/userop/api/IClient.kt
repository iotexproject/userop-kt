package io.iotex.userop.api

import io.iotex.userop.UserOperation

interface IClient {

    fun buildUserOperation(builder: IUserOperationBuilder): UserOperation

    suspend fun sendUserOperation(builder: IUserOperationBuilder, userOp: UserOperation? = null): SendUserOperationResponse

}