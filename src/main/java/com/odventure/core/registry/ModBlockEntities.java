package com.odventure.core.registry;

import com.odventure.core.OdventureCore;
import com.odventure.core.block.entity.MedalBlockEntity;
import com.odventure.core.block.entity.TrophyBaseBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, OdventureCore.MODID);

    public static final RegistryObject<BlockEntityType<MedalBlockEntity>> MEDAL =
            BLOCK_ENTITIES.register("medal", () ->
                    BlockEntityType.Builder.of(MedalBlockEntity::new, ModBlocks.MEDAL.get()).build(null));

    public static final RegistryObject<BlockEntityType<TrophyBaseBlockEntity>> TROPHY =
            BLOCK_ENTITIES.register("trophy_base", () ->
                    BlockEntityType.Builder.of(TrophyBaseBlockEntity::new, ModBlocks.TROPHY_BASE.get()).build(null));
}
