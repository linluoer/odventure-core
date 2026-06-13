package com.odventure.core.registry;

import com.odventure.core.OdventureCore;
import com.odventure.core.block.MedalBlock;
import com.odventure.core.block.TrophyBaseBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, OdventureCore.MODID);

    public static final RegistryObject<Block> TROPHY_BASE = BLOCKS.register("trophy_base",
            () -> new TrophyBaseBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(1.5F, 6.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()));

    public static final RegistryObject<Block> MEDAL = BLOCKS.register("medal",
            () -> new MedalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(1.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));
}
