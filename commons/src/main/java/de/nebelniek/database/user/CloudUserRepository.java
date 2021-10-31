package de.nebelniek.database.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CloudUserRepository extends JpaRepository<CloudUser, Long> {

    CloudUser findByUuid(UUID uuid);

    CloudUser findByTwitchId(UUID uuid);

}