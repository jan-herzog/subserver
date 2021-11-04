package de.nebelniek.database.user.interfaces;

import java.util.concurrent.CompletableFuture;

public interface Loadable {

    CompletableFuture<Void> loadAsync();
    void load();

}
