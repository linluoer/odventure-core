package com.odventure.core.registry;

import com.odventure.core.OdventureCore;
import com.odventure.core.item.MedalItem;
import com.odventure.core.item.TrophyBaseItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, OdventureCore.MODID);

    public static final RegistryObject<Item> TROPHY_BASE = ITEMS.register("trophy_base",
            () -> new TrophyBaseItem(ModBlocks.TROPHY_BASE.get(), new Item.Properties()));

    public static final RegistryObject<Item> MEDAL = ITEMS.register("medal",
            () -> new MedalItem(ModBlocks.MEDAL.get(), new Item.Properties()));
}
