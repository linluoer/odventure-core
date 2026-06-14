package com.odventure.core;

import com.odventure.core.config.ModConfig;
import com.odventure.core.network.ModNetwork;
import com.odventure.core.registry.ModBlockEntities;
import com.odventure.core.registry.ModBlocks;
import com.odventure.core.registry.ModCreativeTabs;
import com.odventure.core.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(OdventureCore.MODID)
public class OdventureCore {
    public static final String MODID = "odventure_core";

    public OdventureCore() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(bus);
        ModItems.ITEMS.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModCreativeTabs.TABS.register(bus);

        ModLoadingContext.get().registerConfig(Type.CLIENT, ModConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.COMMON_SPEC);

        bus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
    }
}
