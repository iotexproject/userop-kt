package io.iotex.test.compat;

import static java.sql.DriverManager.println;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import io.iotex.userop.api.IClient;
import io.iotex.userop.preset.builder.P256AccountBuilder;
import kotlinx.coroutines.Deferred;

public class SupportJava {

    public static void main(String[] args) {

        new Thread(() -> {
            try {
                // init P256AccountBuilder
                Deferred<P256AccountBuilder> builderDeferred = UseropManager.INSTANCE.initP256AccountBuilder();
                CompletableFuture<P256AccountBuilder> builderFuture = Utils.performAsyncOperation(builderDeferred);
                P256AccountBuilder p256AccountBuilder = builderFuture.get();
                println("Account address: " + p256AccountBuilder.getSender());

                // init Client
                Deferred<IClient> clientDeferred = UseropManager.INSTANCE.initClient();
                CompletableFuture<IClient> clientFuture = Utils.performAsyncOperation(clientDeferred);
                IClient client = clientFuture.get();

                // transfer currency
                Deferred<String> transferDeferred = UseropManager.INSTANCE.sendCurrency();
                CompletableFuture<String> transferFuture = Utils.performAsyncOperation(transferDeferred);
                String hash = transferFuture.get();
                println("hash: " + hash);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


}
