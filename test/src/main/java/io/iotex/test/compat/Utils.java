package io.iotex.test.compat;

import java.util.concurrent.CompletableFuture;

import io.reactivex.annotations.NonNull;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.Deferred;

public class Utils {

    public static <T> CompletableFuture<T> performAsyncOperation(Deferred<T> deferred) {
        CompletableFuture<T> future = new CompletableFuture<>();
        deferred.await(new Continuation<T>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object o) {
                future.complete(deferred.getCompleted());
            }
        });

        return future;
    }

}
