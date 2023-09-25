package io.iotex.userop.api

import io.iotex.userop.UserOperation
import java.math.BigInteger

interface IUserOperationBuilder {

    var sender: String
    var nonce: String
    var initCode: String
    var callData: String
    var callGasLimit: String
    var verificationGasLimit: String
    var preVerificationGas: String
    var maxFeePerGas: String
    var maxPriorityFeePerGas: String
    var paymasterAndData: String
    var signature: String

    fun buildOp(entryPoint: String, chainId: BigInteger): UserOperation

    fun resetOp(): IUserOperationBuilder

    fun useMiddleware(vararg middleware: IUserOperationMiddleware): IUserOperationBuilder

    fun resetMiddleware(): IUserOperationBuilder
}