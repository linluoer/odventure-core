package com.odventure.core.item;

import com.odventure.core.data.MedalData;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MedalItem extends BlockItem {
    public MedalItem(Block block, Properties props) {
        super(block, props);
    }

    private boolean isFinished(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        return tag != null && (tag.getBoolean("ContentLocked") || tag.getBoolean("NameLocked")
                || tag.contains("EmbeddedItem"));
    }

    @Override
    public Component getName(ItemStack stack) {
        if (isFinished(stack)) {
            return Component.translatable("item.odventure_core.medal.finished");
        }
        return Component.translatable("item.odventure_core.medal.empty");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (!isFinished(stack)) {
            super.appendHoverText(stack, level, tooltip, flag);
            return;
        }
        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        MedalData data = tag != null ? MedalData.load(tag) : new MedalData();

        String name = data.isNameLocked() && !data.getCustomName().isEmpty()
                ? data.getCustomName()
                : Component.translatable("tooltip.odventure_core.unnamed").getString();
        tooltip.add(Component.literal(name).withStyle(ChatFormatting.GRAY));

        if (data.isContentLocked() && !data.getEmbeddedItem().isEmpty()) {
            tooltip.add(data.getEmbeddedItem().getHoverName().copy().withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }
}
