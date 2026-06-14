package com.odventure.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.odventure.core.config.ConfigCache;
import com.odventure.core.data.TrophyData;
import com.odventure.core.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TrophyItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final float BASE_TOP = 0.25f;

    public TrophyItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
              Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack pose,
                             MultiBufferSource buffer, int light, int overlay) {
        BlockState baseState = ModBlocks.TROPHY_BASE.get().defaultBlockState();

        if (!ConfigCache.renderFinishedItems()) {
            renderPlainBlock(baseState, ctx, pose, buffer, light, overlay);
            return;
        }

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        TrophyData data = tag != null ? TrophyData.load(tag) : new TrophyData();

        pose.pushPose();
        ItemTransforms.apply(ctx, pose);

        Minecraft.getInstance().getBlockRenderer()
                .renderSingleBlock(baseState, pose, buffer, light, overlay);

        if (data.getContentType() != TrophyData.ContentType.NONE && data.getContentId() != null) {
            float ds = data.getDisplayScale() <= 0 ? 1f : data.getDisplayScale();
            pose.pushPose();
            pose.translate(0.5, BASE_TOP + data.getDisplayY(), 0.5);
            pose.mulPose(Axis.YP.rotationDegrees(data.getDisplayYaw()));
            pose.scale(ds, ds, ds);
            DisplayRenderHelper.renderTrophyContent(data, level, pose, buffer, light, overlay);
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
