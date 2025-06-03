package org.readutf.gameservice.client.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public class FutureUtils {
    public static <T> CompletableFuture<T> toCompletableFuture(Future<T> future, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return future.get(); // This will block
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }
}