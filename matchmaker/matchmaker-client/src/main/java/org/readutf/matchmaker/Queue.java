package org.readutf.matchmaker;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Queue {

    private final String name;

    public Queue(String name) {
        this.name = name;
    }

    public CompletableFuture<String> join(List<UUID> players) {
        throw new UnsupportedOperationException();
    }

}
