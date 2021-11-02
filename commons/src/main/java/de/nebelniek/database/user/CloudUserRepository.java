package de.nebelniek.database.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Repository
public interface CloudUserRepository extends JpaRepository<CloudUser, Long> {

    CloudUser findByUuidIs(UUID uuid);

    CloudUser findByTwitchId(String twitchId);

    @Async
    CompletableFuture<CloudUser> findByUuidEquals(UUID uuid);


    boolean existsByUuid(UUID uuid);

}