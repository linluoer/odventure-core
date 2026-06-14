package com.odventure.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.odventure.core.block.entity.TrophyBaseBlockEntity;
import com.odventure.core.config.ConfigCache;
import com.odventure.core.data.TrophyData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class TrophyBaseRenderer implements BlockEntityRenderer<TrophyBaseBlockEntity> {
    private static final float BASE_TOP = 0.25f;

    public TrophyBaseRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TrophyBaseBlockEntity be, float partialTick, PoseStack pose,
                       MultiBufferSource buffer, int light, int overlay) {
        Level level = be.getLevel();
        if (level == null) return;
        TrophyData data = be.getData();

        if (data.getContentType() != TrophyData.ContentType.NONE && data.getContentId() != null) {
            float yRot = facingToDegrees(be.getBlockState().getValue(HorizontalDirectionalBlock.FACING));
            float ds = data.getDisplayScale() <= 0 ? 1f : data.getDisplayScale();

            pose.pushPose();
            pose.translate(0.5, BASE_TOP + data.getDisplayY(), 0.5);
            pose.mulPose(Axis.YP.rotationDegrees(yRot + data.getDisplayYaw()));
            pose.scale(ds, ds, ds);
            DisplayRenderHelper.renderTrophyContent(data, level, pose, buffer, light, overlay);
            pose.popPose();
        }

        if (ConfigCache.showFloatingName() && isLookedAt(be)) {
            FloatingNameRenderer.render(resolveName(data), 1.2f, pose, buffer, light);
        }
    }

    private boolean isLookedAt(TrophyBaseBlockEntity be) {
        HitResult hr = Minecraft.getInstance().hitResult;
        return hr instanceof BlockHitResult bhr
                && hr.getType() == HitResult.Type.BLOCK
                && bhr.getBlockPos().equals(be.getBlockPos());
    }

    private String resolveName(TrophyData data) {
        if (data.isNameLocked() && !data.getCustomName().isEmpty()) {
            return data.getCustomName();
        }
        String key = data.isContentLocked()
                ? "item.odventure_core.trophy_base.finished"
                : "item.odventure_core.trophy_base.empty";
        return Component.translatable(key).getString();
    }

    private float facingToDegrees(Direction facing) {
        return switch (facing) {
            case SOUTH -> 0f;
            case WEST -> 90f;
            case NORTH -> 180f;
            case EAST -> 270f;
            default -> 0f;
        };
    }
}
