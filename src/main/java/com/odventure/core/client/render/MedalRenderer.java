package com.odventure.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.odventure.core.block.entity.MedalBlockEntity;
import com.odventure.core.config.ConfigCache;
import com.odventure.core.data.MedalData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class MedalRenderer implements BlockEntityRenderer<MedalBlockEntity> {
    private static final double FRONT_OFFSET = -0.34;
    private static final double VERTICAL_OFFSET = 0.12;
    private static final float ITEM_SCALE = 0.45f;

    public MedalRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MedalBlockEntity be, float partialTick, PoseStack pose,
                       MultiBufferSource buffer, int light, int overlay) {
        Level level = be.getLevel();
        if (level == null) return;
        MedalData data = be.getData();
        ItemStack stack = data.getEmbeddedItem();

        if (!stack.isEmpty()) {
            Direction facing = be.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
            float yRot = switch (facing) {
                case SOUTH -> 0f;
                case WEST -> 90f;
                case NORTH -> 180f;
                case EAST -> 270f;
                default -> 0f;
            };

            pose.pushPose();
            pose.translate(0.5, 0.5 + VERTICAL_OFFSET, 0.5);
            pose.translate(facing.getStepX() * FRONT_OFFSET, 0.0, facing.getStepZ() * FRONT_OFFSET);
            pose.mulPose(Axis.YP.rotationDegrees(yRot));
            pose.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack, ItemDisplayContext.FIXED, light, overlay,
                    pose, buffer, level, 0);
            pose.popPose();
        }

        if (ConfigCache.showFloatingName() && isLookedAt(be)) {
            FloatingNameRenderer.render(resolveName(data), 1.1f, pose, buffer, light);
        }
    }

    private boolean isLookedAt(MedalBlockEntity be) {
        HitResult hr = Minecraft.getInstance().hitResult;
        return hr instanceof BlockHitResult bhr
                && hr.getType() == HitResult.Type.BLOCK
                && bhr.getBlockPos().equals(be.getBlockPos());
    }

    private String resolveName(MedalData data) {
        if (data.isNameLocked() && !data.getCustomName().isEmpty()) {
            return data.getCustomName();
        }
        String key = data.isContentLocked()
                ? "item.odventure_core.medal.finished"
                : "item.odventure_core.medal.empty";
        return Component.translatable(key).getString();
    }
}
