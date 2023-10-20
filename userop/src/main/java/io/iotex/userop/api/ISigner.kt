package io.iotex.userop.api

interface ISigner {

    val publicKey: ByteArray

    fun sign(data: ByteArray): ByteArray

}