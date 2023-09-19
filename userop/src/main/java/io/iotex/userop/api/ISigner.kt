package io.iotex.userop.api

interface ISigner {

    val address: String

    val publicKey: ByteArray

    fun sign(data: ByteArray): ByteArray

}