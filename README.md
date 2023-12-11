![](https://user-images.githubusercontent.com/16026265/268563978-46911948-aa87-4fd3-9ddb-0a504f801f3f.png)

## userop-kt
---

## About Account Abstraction Using Alt Mempool
An account abstraction proposal which completely avoids the need for consensus-layer protocol changes. Instead of adding new protocol features and changing the bottom-layer transaction type, this proposal instead introduces a higher-layer pseudo-transaction object called a UserOperation. Users send UserOperation objects into a separate mempool. A special class of actor called bundlers package up a set of these objects into a transaction making a handleOps call to a special contract, and that transaction then gets included in a block.
 
[ERC-4337: Account Abstraction Using Alt Mempool](https://eips.ethereum.org/EIPS/eip-4337)     


## Advanced Supported P256 Account for Trusted Environment

On the basis of [userop.js](https://github.com/stackup-wallet/userop.js) signature, we advanced supported secp256r1-based signature.

The "secp256r1" elliptic curve is a standardized curve by NIST which has the same calculations by different input parameters with""secp256k1” elliptic curve used by the "ecrecover" precompiled contract. The cost of combined attacks and the security conditions are almost the same for both curves. Adding a precompiled contract which is similar to "ecrecover" can provide signature verifications using the "secp256r1" elliptic curve in the smart contracts and multi-faceted benefits can occur. One important factor is that this curve is widely used and supported in many modern devices such as Apple’s Secure Enclave, Webauthn, Android Keychain which proves the user adoption. Additionally, the introduction of this precompile could enable valuable features in the account abstraction which allows more efficient and flexible management of accounts by transaction signs in mobile devices. Most of the modern devices and applications rely on the "secp256r1" elliptic curve. The addition of this precompiled contract enables the verification of device native transaction signing mechanisms. For example:

+ **Apple’s Secure Enclave** :shipit:: There is a separate "Trusted Execution Environment" in Apple hardware which can sign arbitrary messages and can only be accessed by biometric identification.
Webauthn: Web Authentication (WebAuthn) is a web standard published by the World Wide Web Consortium (W3C). WebAuthn aims to standardize an interface for authenticating users to web-based applications and services using public-key cryptography. It is being used by almost all of the modern web browsers.
* **Android Keystore**: Android Keystore is an API that manages the private keys and signing methods. The private keys are not processed while using Keystore as the applications’ signing method. Also, it can be done in the "Trusted Execution Environment" in the microchip.

  
[Reffer to EIP-7212](https://eips.ethereum.org/EIPS/eip-7212)

### Usage

```
implementation 'io.iotex:userop-kt:1.0.1'
```

#### Create a signer

Create a signer object that provides a publicKey and a signature method.

```kotlin
class P256Signer: ISigner {

    override val publicKey: ByteArray
        get() = ByteArray(64)

    override fun sign(data: ByteArray): ByteArray {
        return ByteArray(64)
    }
}
```

#### Get account address

```kotlin
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

```kotlin
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

```kotlin
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
