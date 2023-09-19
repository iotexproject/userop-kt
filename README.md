![](https://user-images.githubusercontent.com/16026265/268563978-46911948-aa87-4fd3-9ddb-0a504f801f3f.png)

## userop-kt
---

### Summary

An account abstraction proposal which completely avoids the need for consensus-layer protocol changes. Instead of adding new protocol features and changing the bottom-layer transaction type, this proposal instead introduces a higher-layer pseudo-transaction object called a UserOperation. Users send UserOperation objects into a separate mempool. A special class of actor called bundlers package up a set of these objects into a transaction making a handleOps call to a special contract, and that transaction then gets included in a block.

[ERC-4337: Account Abstraction Using Alt Mempool](https://eips.ethereum.org/EIPS/eip-4337)
---

### Usage

#### Create a signer

Create a signer object that provides a publicKey and a signature method.

```
class P256Signer: ISigner {
    override val address: String
        get() = "0x00000000000000000000000000000000"

    override val publicKey: ByteArray
        get() = ByteArray(64)

    override fun sign(data: ByteArray): ByteArray {
        return ByteArray(64)
    }
}
```

#### Get account address

```
val builder = P256AccountBuilder.init(RPC_URL, P256Signer(), PresetBuilderOpts(
    entryPoint = ENTRY_POINT,
    factory = ACCOUNT_FACTORY,
    salt = BigInteger.ZERO,
    bundlerRpc = BUNDLER_RPC
))
val address = builder.sender
println("Account address: $address")
```

#### Send currency

```
val to = "Receipt address"
val value = BigInteger.ONE
val data = "0x"
val callData = builder.execute(to, value, data)
println("callData: $callData")
val response = client.sendUserOperation(builder)
val userOpHash = response.userOpHash
val txHash = response.wait()
println("userOpHash: $userOpHash")
println("txHash: $txHash")
```

#### Send Erc20

```
val erc20Contract = "Contract address"
val to = "Receipt address"
val value = BigInteger.ONE
val function = Function(
    "transfer",
    listOf(
        Address(to),
        Uint256(value)
    ),
    listOf()
)
val data = FunctionEncoder.encode(function)
val callData = builder.execute(erc20Contract, value, data)
println("callData: $callData")
val response = client.sendUserOperation(builder)
val userOpHash = response.userOpHash
val txHash = response.wait()
println("userOpHash: $userOpHash")
println("txHash: $txHash")
```