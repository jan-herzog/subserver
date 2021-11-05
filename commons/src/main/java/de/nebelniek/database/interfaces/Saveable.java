package de.nebelniek.database.interfaces;

import java.util.concurrent.CompletableFuture;

public interface Saveable {
    CompletableFuture<Void> saveAsync();
    void save();
}
