package com.odventure.core.api;

import com.odventure.core.block.entity.MedalBlockEntity;
import com.odventure.core.block.entity.TrophyBaseBlockEntity;
import com.odventure.core.data.MedalData;
import com.odventure.core.data.TrophyData;
import com.odventure.core.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public final class OdventureCoreAPI {
    private OdventureCoreAPI() {}

    public static ItemStack createTrophy(TrophyData.ContentType contentType,
                                         @Nullable ResourceLocation contentId,
                                         @Nullable String name) {
        return createTrophy(contentType, contentId, name, 1f, 0f, 0f);
    }

    public static ItemStack createTrophy(TrophyData.ContentType contentType,
                                         @Nullable ResourceLocation contentId,
                                         @Nullable String name,
                                         float displayScale, float displayYaw, float displayY) {
        if (contentType == null) contentType = TrophyData.ContentType.NONE;
        boolean hasContent = contentType != TrophyData.ContentType.NONE && contentId != null;
        boolean hasName = name != null && !name.isEmpty();

        TrophyData data = new TrophyData(
                contentType, contentId,
                hasName ? name : "",
                hasContent,
                hasName,
                displayScale, displayYaw, displayY
        );
        return toStack(ModItems.TROPHY_BASE.get(), data.isEmpty() ? null : save(data));
    }

    public static ItemStack createMedal(@Nullable ItemStack embedded, @Nullable String name) {
        ItemStack one = ItemStack.EMPTY;
        if (embedded != null && !embedded.isEmpty()) {
            one = embedded.copy();
            one.setCount(1);
        }
        boolean hasContent = !one.isEmpty();
        boolean hasName = name != null && !name.isEmpty();

        MedalData data = new MedalData(one, hasName ? name : "", hasContent, hasName);
        CompoundTag tag = null;
        if (!data.isEmpty()) {
            tag = new CompoundTag();
            data.save(tag);
        }
        return toStack(ModItems.MEDAL.get(), tag);
    }

    public static boolean writeTrophy(Level level, BlockPos pos, TrophyData data) {
        if (level == null || pos == null || data == null) return false;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TrophyBaseBlockEntity trophy) {
            trophy.setData(data);
            return true;
        }
        return false;
    }

    public static boolean writeMedal(Level level, BlockPos pos, MedalData data) {
        if (level == null || pos == null || data == null) return false;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MedalBlockEntity medal) {
            medal.setData(data);
            return true;
        }
        return false;
    }

    public static TrophyData readTrophy(ItemStack stack) {
        if (stack == null || stack.getItem() != ModItems.TROPHY_BASE.get()) {
            return new TrophyData();
        }
        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        return tag != null ? TrophyData.load(tag) : new TrophyData();
    }

    public static TrophyData readTrophy(Level level, BlockPos pos) {
        if (level != null && level.getBlockEntity(pos) instanceof TrophyBaseBlockEntity trophy) {
            return trophy.getData();
        }
        return new TrophyData();
    }

    public static MedalData readMedal(ItemStack stack) {
        if (stack == null || stack.getItem() != ModItems.MEDAL.get()) {
            return new MedalData();
        }
        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        return tag != null ? MedalData.load(tag) : new MedalData();
    }

    public static MedalData readMedal(Level level, BlockPos pos) {
        if (level != null && level.getBlockEntity(pos) instanceof MedalBlockEntity medal) {
            return medal.getData();
        }
        return new MedalData();
    }

    private static CompoundTag save(TrophyData data) {
        CompoundTag tag = new CompoundTag();
        data.save(tag);
        return tag;
    }

    private static ItemStack toStack(net.minecraft.world.item.Item item, @Nullable CompoundTag beTag) {
        ItemStack stack = new ItemStack(item);
        if (beTag != null) {
            stack.addTagElement("BlockEntityTag", beTag);
        }
        return stack;
    }
}
