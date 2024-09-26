package br.com.pentamc.pvp.controller;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class CombatController {

    protected final Cache<UUID, UUID> cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();

    public void addCombat(UUID playerId, UUID targetId) {
        remove(playerId);
        remove(targetId);

        cache.put(playerId, targetId);
    }

    public UUID read(UUID uniqueId) {
        if (cache.asMap().containsKey(uniqueId)) {
            return cache.getIfPresent(uniqueId);
        } else if (cache.asMap().containsValue(uniqueId)) {
            for (Map.Entry<UUID, UUID> entry : cache.asMap().entrySet()) {
                if (entry.getValue().equals(uniqueId)) {
                    return entry.getKey();
                }
            }
        }

        return null;
    }

    public void remove(UUID id) {
        Map<UUID, UUID> map = cache.asMap();
        Iterator<Map.Entry<UUID, UUID>> iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, UUID> entry = iterator.next();

            UUID
                    key = entry.getKey(),
                    value = entry.getValue();

            if (key.equals(id) || value.equals(id))
                iterator.remove();
        }
    }
}
