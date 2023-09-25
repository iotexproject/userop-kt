package io.iotex.userop

import io.iotex.userop.api.IUserOperationBuilder
import io.iotex.userop.api.IUserOperationMiddleware
import io.iotex.userop.preset.middleware.UserOperationMiddlewareCtx
import org.web3j.abi.datatypes.Address
import org.web3j.utils.Numeric
import java.math.BigInteger

data class UserOperation(
    var sender: String = Address.DEFAULT.value,
    var nonce: String = "0x0",
    var initCode: String = "0x",
    var callData: String = "0x",
    var callGasLimit: String = Numeric.prependHexPrefix(BigInteger("35000").toString(16)),
    var verificationGasLimit: String = Numeric.prependHexPrefix(BigInteger("70000").toString(16)),
    var preVerificationGas: String = Numeric.prependHexPrefix(BigInteger("21000").toString(16)),
    var maxFeePerGas: String = "0x0",
    var maxPriorityFeePerGas: String = "0x0",
    var paymasterAndData: String = "0x",
    var signature: String = "0x",
)

abstract class UserOperationBuilder: IUserOperationBuilder {
    private val defaultOp: UserOperation = UserOperation()
    private var currOp: UserOperation = defaultOp
    private val middlewareStack = mutableListOf<IUserOperationMiddleware>()

    override var sender: String
        get() = this.currOp.sender
        set(value) { this.currOp.sender = value }

    override var nonce: String
        get() = this.currOp.nonce
        set(value) { this.currOp.nonce =  value }

    override var initCode: String
        get() = this.currOp.initCode
        set(value) { this.currOp.initCode = value }

    override var callData: String
        get() = this.currOp.callData
        set(value) { this.currOp.callData = value }

    override var callGasLimit: String
        get() = this.currOp.callGasLimit
        set(value) { this.currOp.callGasLimit = value }

    override var verificationGasLimit: String
        get() = this.currOp.verificationGasLimit
        set(value) { this.currOp.verificationGasLimit = value }

    override var preVerificationGas: String
        get() = this.currOp.preVerificationGas
        set(value) { this.currOp.preVerificationGas = value }

    override var maxFeePerGas: String
        get() = this.currOp.maxFeePerGas
        set(value) { this.currOp.maxFeePerGas = value }

    override var maxPriorityFeePerGas: String
        get() = this.currOp.maxPriorityFeePerGas
        set(value) { this.currOp.maxPriorityFeePerGas = value }

    override var paymasterAndData: String
        get() = this.currOp.paymasterAndData
        set(value) { this.currOp.paymasterAndData = value }

    override var signature: String
        get() = this.currOp.signature
        set(value) { this.currOp.signature = value }

    override fun buildOp(entryPoint: String, chainId: BigInteger): UserOperation {
        val ctx = UserOperationMiddlewareCtx(currOp, entryPoint, chainId)

        middlewareStack.forEach {
            it.process(ctx)
        }

        this.currOp = ctx.op

        return this.currOp
    }

    override fun resetOp() = apply {
        this.currOp = this.defaultOp
    }

    override fun useMiddleware(vararg middleware: IUserOperationMiddleware) = apply {
        this.middlewareStack.addAll(middleware)
    }

    override fun resetMiddleware() = apply {
        this.middlewareStack.clear()
    }
}