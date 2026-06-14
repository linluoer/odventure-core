package com.odventure.core.config;

import com.odventure.core.OdventureCore;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = OdventureCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigCache {
    private static Set<EntityType<?>> blacklist = new HashSet<>();

    public static boolean isBlacklisted(EntityType<?> type) {
        return blacklist.contains(type);
    }

    public static boolean renderFinishedItems() {
        return ModConfig.RENDER_FINISHED_ITEMS.get();
    }

    public static boolean showFloatingName() {
        return ModConfig.SHOW_FLOATING_NAME.get();
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        rebuild();
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        rebuild();
    }

    private static void rebuild() {
        try {
            Set<EntityType<?>> set = new HashSet<>();
            for (String s : ModConfig.RENDER_BLACKLIST.get()) {
                ResourceLocation id = ResourceLocation.tryParse(s);
                if (id != null && BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
                    set.add(BuiltInRegistries.ENTITY_TYPE.get(id));
                }
            }
            blacklist = set;
        } catch (Exception ignored) {
        }
    }
}
