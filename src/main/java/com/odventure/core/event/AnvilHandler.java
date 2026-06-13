package com.odventure.core.event;

import com.odventure.core.OdventureCore;
import com.odventure.core.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OdventureCore.MODID)
public class AnvilHandler {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        if (isOurItem(left)) {

            event.setCanceled(true);
        }
    }

    private static boolean isOurItem(ItemStack stack) {
        return stack.getItem() == ModItems.TROPHY_BASE.get()
                || stack.getItem() == ModItems.MEDAL.get();
    }
}
