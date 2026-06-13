package com.odventure.core.registry;

import com.odventure.core.OdventureCore;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, OdventureCore.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("odventure_core",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.odventure_core"))
                    .icon(() -> new ItemStack(ModItems.TROPHY_BASE.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.TROPHY_BASE.get());
                        output.accept(ModItems.MEDAL.get());
                    })
                    .build());
}
