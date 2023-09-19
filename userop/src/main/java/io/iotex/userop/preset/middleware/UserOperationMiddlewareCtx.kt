package io.iotex.userop.preset.middleware

import io.iotex.userop.UserOperation
import io.iotex.userop.api.IUserOperationMiddlewareCtx
import io.iotex.userop.utils.cleanHexPrefix
import io.iotex.userop.utils.toHexByteArray
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.DynamicStruct
import org.web3j.abi.datatypes.generated.Bytes32
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Hash
import java.math.BigInteger

class UserOperationMiddlewareCtx(
    override val op: UserOperation,
    override val entryPoint: String,
    override val chainId: BigInteger
) : IUserOperationMiddlewareCtx {

    override fun getUserOpHash(): String {
        val struct = DynamicStruct(
            Address(160, op.sender),
            Uint256(op.nonce.cleanHexPrefix().toBigInteger(16)),
            Bytes32(Hash.sha3(op.initCode.toHexByteArray())),
            Bytes32(Hash.sha3(op.callData.toHexByteArray())),
            Uint256(op.callGasLimit.cleanHexPrefix().toBigInteger(16)),
            Uint256(op.verificationGasLimit.cleanHexPrefix().toBigInteger(16)),
            Uint256(op.preVerificationGas.cleanHexPrefix().toBigInteger(16)),
            Uint256(op.maxFeePerGas.cleanHexPrefix().toBigInteger(16)),
            Uint256(op.maxPriorityFeePerGas.cleanHexPrefix().toBigInteger(16)),
            Bytes32(Hash.sha3(op.paymasterAndData.toHexByteArray()))
        )
        val encodedOp = FunctionEncoder.encodeConstructor(listOf(struct))
        val packedStr = encodedOp.substring(64, encodedOp.length)
        val hashBytes = Hash.sha3(packedStr.toHexByteArray())
        return FunctionEncoder.encodeConstructor(
            listOf(
                Bytes32(hashBytes),
                Address(entryPoint),
                Uint256(chainId)
            )
        )
    }

}