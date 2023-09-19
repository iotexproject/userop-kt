package io.iotex.userop.utils

import org.web3j.utils.Numeric

fun String.prependHexPrefix(): String {
    return Numeric.prependHexPrefix(this)
}

fun String.toHexByteArray(): ByteArray {
    return Numeric.hexStringToByteArray(this)
}

fun String.cleanHexPrefix(): String {
    return Numeric.cleanHexPrefix(this)
}

fun ByteArray.toHexString(): String {
    return Numeric.toHexString(this)
}
