package com.odventure.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.odventure.core.config.ConfigCache;
import com.odventure.core.data.MedalData;
import com.odventure.core.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MedalItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final double VERTICAL_OFFSET = -0.08;
    private static final float ITEM_SCALE = 0.4f;

    public MedalItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
              Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack pose,
                             MultiBufferSource buffer, int light, int overlay) {
        BlockState medalState = ModBlocks.MEDAL.get().defaultBlockState();

        if (!ConfigCache.renderFinishedItems()) {
            renderPlainBlock(medalState, ctx, pose, buffer, light, overlay);
            return;
        }

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        MedalData data = tag != null ? MedalData.load(tag) : new MedalData();

        pose.pushPose();
        ItemTransforms.apply(ctx, pose);

        Minecraft.getInstance().getBlockRenderer()
                .renderSingleBlock(medalState, pose, buffer, light, overlay);

        ItemStack embedded = data.getEmbeddedItem();
        if (!embedded.isEmpty()) {
            pose.pushPose();
            pose.translate(0.5, 0.5 + VERTICAL_OFFSET, 0.5);
            pose.translate(0.0, 0.0, 0.5 - 1.5 / 16.0 + 0.01);
            pose.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
            Minecraft.getInstance().getItemRenderer().renderStatic(
                    embedded, ItemDisplayContext.FIXED, light, overlay,
                    pose, buffer, level, 0);
            pose.popPose();
        }

        pose.popPose();
    }

    private void renderPlainBlock(BlockState state, ItemDisplayContext ctx, PoseStack pose,
                                  MultiBufferSource buffer, int light, int overlay) {
        pose.pushPose();
        ItemTransforms.apply(ctx, pose);
        Minecraft.getInstance().getBlockRenderer()
                .renderSingleBlock(state, pose, buffer, light, overlay);
        pose.popPose();
    }
}
