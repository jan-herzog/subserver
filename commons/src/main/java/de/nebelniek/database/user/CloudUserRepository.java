package de.nebelniek.database.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface CloudUserRepository extends JpaRepository<CloudUser, Long> {

    CloudUser findByUuid(UUID uuid);

    CloudUser findByTwitchId(String twitchId);

    @Async
    CompletableFuture<CloudUser> findByUuidAsync(UUID uuid);

    @Async
    CompletableFuture<CloudUser> findByTwitchIdAsync(UUID uuid);

    boolean existsByUuid(UUID uuid);

}