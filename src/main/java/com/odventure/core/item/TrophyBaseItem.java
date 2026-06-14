package com.odventure.core.item;

import com.odventure.core.client.ClientItemExtensions;
import com.odventure.core.data.TrophyData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class TrophyBaseItem extends BlockItem {
    public TrophyBaseItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(ClientItemExtensions.trophy());
    }

    private boolean isFinished(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        if (tag == null) return false;
        return tag.getBoolean("ContentLocked") || tag.getBoolean("NameLocked")
                || (tag.contains("ContentType") && !"NONE".equals(tag.getString("ContentType")));
    }

    @Override
    public Component getName(ItemStack stack) {
        if (isFinished(stack)) {
            return Component.translatable("item.odventure_core.trophy_base.finished");
        }
        return Component.translatable("item.odventure_core.trophy_base.empty");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (!isFinished(stack)) {
            super.appendHoverText(stack, level, tooltip, flag);
            return;
        }
        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        TrophyData data = tag != null ? TrophyData.load(tag) : new TrophyData();

        String name = data.isNameLocked() && !data.getCustomName().isEmpty()
                ? data.getCustomName()
                : Component.translatable("tooltip.odventure_core.unnamed").getString();
        tooltip.add(Component.literal(name).withStyle(ChatFormatting.GRAY));

        if (data.isContentLocked() && data.getContentId() != null) {
            Component contentName = resolveContentName(data);
            if (contentName != null) {
                tooltip.add(contentName.copy().withStyle(ChatFormatting.GRAY));
            }
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Nullable
    private Component resolveContentName(TrophyData data) {
        ResourceLocation id = data.getContentId();
        if (id == null) return null;
        return switch (data.getContentType()) {
            case BLOCK -> BuiltInRegistries.BLOCK.get(id).getName();
            case ITEM -> BuiltInRegistries.ITEM.get(id).getDescription();
            case ENTITY -> BuiltInRegistries.ENTITY_TYPE.get(id).getDescription();
            default -> null;
        };
    }
}
