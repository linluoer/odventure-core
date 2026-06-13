package com.odventure.core.client.render;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FakeEntityCache {
    private static final Map<EntityType<?>, Entity> CACHE = new HashMap<>();

    public static final Set<EntityType<?>> BLACKLIST = Set.of(
            EntityType.AREA_EFFECT_CLOUD,
            EntityType.LIGHTNING_BOLT,
            EntityType.FALLING_BLOCK,
            EntityType.ITEM,
            EntityType.PLAYER
    );

    public static boolean isBlacklisted(EntityType<?> type) {
        return BLACKLIST.contains(type);
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
