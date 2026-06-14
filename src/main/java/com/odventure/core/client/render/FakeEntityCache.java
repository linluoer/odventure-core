package com.odventure.core.client.render;

import com.odventure.core.config.ConfigCache;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class FakeEntityCache {
    private static final Map<EntityType<?>, Entity> CACHE = new HashMap<>();

    public static boolean isBlacklisted(EntityType<?> type) {
        return ConfigCache.isBlacklisted(type);
    }

    public static Entity get(Level level, EntityType<?> type) {
        if (isBlacklisted(type)) return null;
        return CACHE.computeIfAbsent(type, t -> {
            try {
                Entity e = t.create(level);
                if (e instanceof AgeableMob ageable) {
                    ageable.setBaby(false);
                }
                return e;
            } catch (Exception ex) {
                return null;
            }
        });
    }

    public static void clear() {
        CACHE.clear();
    }
}
