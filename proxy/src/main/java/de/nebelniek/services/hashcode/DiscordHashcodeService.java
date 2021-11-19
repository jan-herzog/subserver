package de.nebelniek.services.hashcode;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class DiscordHashcodeService extends HashcodeService {

    private final Cache<UUID, String> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    public String getHash(UUID key) {
        return cache.getIfPresent(key);
    }

    public UUID deleteHash(String hash) {
        UUID uuid = cache.asMap().entrySet().stream().filter(entry -> entry.getValue().equals(hash)).toList().get(0).getKey();
        cache.invalidate(uuid);
        LOGGER.info("Invalidated hash '" + hash + "' for uuid " + uuid);
        return uuid;
    }

    public void storeHash(UUID key) {
        if (cache.asMap().containsKey(key))
            cache.invalidate(key);
        cache.put(key, generateHash(16));
        LOGGER.info("Put hash '" + cache.getIfPresent(key) + "' for uuid " + key);
    }

    public boolean isHashPresent(String hash) {
        return cache.asMap().containsValue(hash);
    }
}
