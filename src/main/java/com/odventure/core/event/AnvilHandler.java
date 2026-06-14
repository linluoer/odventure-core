package com.odventure.core.event;

import com.odventure.core.OdventureCore;
import com.odventure.core.config.ModConfig;
import com.odventure.core.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OdventureCore.MODID)
public class AnvilHandler {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (!ModConfig.ENABLE_ANVIL_BLOCK.get()) return;
        ItemStack left = event.getLeft();
        if (left.getItem() == ModItems.TROPHY_BASE.get()
                || left.getItem() == ModItems.MEDAL.get()) {
            event.setCanceled(true);
        }
    }
}
