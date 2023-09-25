package io.iotex.userop.preset.middleware

import io.iotex.userop.UserOperation
import io.iotex.userop.api.IUserOperationMiddlewareCtx
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.DynamicStruct
import org.web3j.abi.datatypes.generated.Bytes32
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Hash
import org.web3j.utils.Numeric
import java.math.BigInteger

class UserOperationMiddlewareCtx(
    override val op: UserOperation,
    override val entryPoint: String,
    override val chainId: BigInteger
) : IUserOperationMiddlewareCtx {

    override fun getUserOpHash(): String {
        val struct = DynamicStruct(
            Address(160, op.sender),
            Uint256(Numeric.cleanHexPrefix(op.nonce).toBigInteger(16)),
            Bytes32(Hash.sha3(Numeric.hexStringToByteArray(op.initCode))),
            Bytes32(Hash.sha3(Numeric.hexStringToByteArray(op.callData))),
            Uint256(Numeric.cleanHexPrefix(op.callGasLimit).toBigInteger(16)),
            Uint256(Numeric.cleanHexPrefix(op.verificationGasLimit).toBigInteger(16)),
            Uint256(Numeric.cleanHexPrefix(op.preVerificationGas).toBigInteger(16)),
            Uint256(Numeric.cleanHexPrefix(op.maxFeePerGas).toBigInteger(16)),
            Uint256(Numeric.cleanHexPrefix(op.maxPriorityFeePerGas).toBigInteger(16)),
            Bytes32(Hash.sha3(Numeric.hexStringToByteArray(op.paymasterAndData)))
        )
        val encodedOp = FunctionEncoder.encodeConstructor(listOf(struct))
        val packedStr = encodedOp.substring(64, encodedOp.length)
        val hashBytes = Hash.sha3(Numeric.hexStringToByteArray(packedStr))
        return FunctionEncoder.encodeConstructor(
            listOf(
                Bytes32(hashBytes),
                Address(entryPoint),
                Uint256(chainId)
            )
        )
    }

}