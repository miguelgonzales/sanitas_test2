package com.mycorp;

import com.ning.http.client.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class ZendeskHelper {

    private ZendeskHelper() {

    }

    public static <T> T complete(ListenableFuture<T> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            throw new ZendeskException(e.getMessage(), e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ZendeskException) {
                throw (ZendeskException) e.getCause();
            }
            throw new ZendeskException(e.getMessage(), e);
        }
    }
}
