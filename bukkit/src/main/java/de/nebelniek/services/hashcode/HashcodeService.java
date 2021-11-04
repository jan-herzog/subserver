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
public class HashcodeService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

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

    private final SecureRandom rnd = new SecureRandom();

    private String generateHash(int len) {
        StringBuilder sb = new StringBuilder(len);
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

}
