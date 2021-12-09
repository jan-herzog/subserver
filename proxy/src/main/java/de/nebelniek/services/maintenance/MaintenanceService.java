package de.nebelniek.services.maintenance;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MaintenanceService {

    private final Map<MaintenanceKey, Boolean> storage = new HashMap<>();

    public void set(MaintenanceKey key, boolean b) {
        if (storage.containsKey(key))
            storage.replace(key, b);
        else storage.put(key, b);
    }

    public void toggle(MaintenanceKey key) {
        if (storage.containsKey(key))
            storage.replace(key, !storage.get(key));
        else storage.put(key, true);
    }

    public boolean get(MaintenanceKey key) {
        if (!storage.containsKey(key))
            storage.put(key, false);
        return storage.get(key);
    }

}
