package de.nebelniek.services.hashcode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.UUID;

public abstract class HashcodeService {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public abstract String getHash(UUID key);

    public abstract UUID deleteHash(String hash);

    public abstract void storeHash(UUID key);

    public abstract boolean isHashPresent(String hash);

    private final SecureRandom rnd = new SecureRandom();

    protected String generateHash(int len) {
        StringBuilder sb = new StringBuilder(len);
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

}
