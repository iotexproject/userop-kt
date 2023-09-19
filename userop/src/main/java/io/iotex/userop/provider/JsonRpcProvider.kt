package io.iotex.userop.provider

import org.web3j.protocol.core.Request
import org.web3j.protocol.core.Response
import org.web3j.protocol.http.HttpService

open class JsonRpcProvider(val rpcUrl: String) {

    init {
        if (!rpcUrl.startsWith("https://") && !rpcUrl.startsWith("http://")) {
            throw IllegalArgumentException("Invalid rpcUrl")
        }
    }

    open fun <T : Response<*>> send(method: String, params: List<Any>, returnType: Class<T>): T {
        return Request(method, params, HttpService(rpcUrl), returnType).send()
    }


}