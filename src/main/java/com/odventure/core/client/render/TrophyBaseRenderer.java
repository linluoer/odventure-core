package com.odventure.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.odventure.core.block.entity.TrophyBaseBlockEntity;
import com.odventure.core.data.TrophyData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
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

            switch (data.getContentType()) {
                case BLOCK -> renderBlock(data, pose, buffer, light, overlay);
                case ITEM -> renderItem(data, level, pose, buffer, light, overlay);
                case ENTITY -> renderEntity(data, level, pose, buffer, light);
                default -> {}
            }
            pose.popPose();
        }

        if (isLookedAt(be)) {
            String name = resolveName(data);
            FloatingNameRenderer.render(name, 1.2f, pose, buffer, light);
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

    private void renderBlock(TrophyData data, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        Block block = BuiltInRegistries.BLOCK.get(data.getContentId());
        if (block == null) return;
        BlockState state = block.defaultBlockState();
        float s = 0.6f;
        pose.pushPose();
        pose.scale(s, s, s);
        pose.translate(-0.5, 0.0, -0.5);
        Minecraft.getInstance().getBlockRenderer()
                .renderSingleBlock(state, pose, buffer, light, overlay);
        pose.popPose();
    }

    private void renderItem(TrophyData data, Level level, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        Item item = BuiltInRegistries.ITEM.get(data.getContentId());
        if (item == null) return;
        ItemStack stack = new ItemStack(item);
        float s = 0.6f;
        pose.pushPose();
        pose.translate(0.0, 0.35, 0.0);
        pose.scale(s, s, s);
        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack, ItemDisplayContext.FIXED, light, overlay,
                pose, buffer, level, 0);
        pose.popPose();
    }

    private void renderEntity(TrophyData data, Level level, PoseStack pose, MultiBufferSource buffer, int light) {
        try {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(data.getContentId());
            if (type == null || FakeEntityCache.isBlacklisted(type)) return;
            Entity entity = FakeEntityCache.get(level, type);
            if (entity == null) return;

            entity.tickCount = 0;
            entity.xOld = entity.getX();
            entity.yOld = entity.getY();
            entity.zOld = entity.getZ();
            entity.setYRot(0f);
            entity.setXRot(0f);
            if (entity instanceof LivingEntity living) {
                living.yBodyRot = 0f;
                living.yBodyRotO = 0f;
                living.yHeadRot = 0f;
                living.yHeadRotO = 0f;
                living.walkAnimation.setSpeed(0f);
                living.walkAnimation.update(0f, 0f);
                living.attackAnim = 0f;
                living.oAttackAnim = 0f;
                living.hurtTime = 0;
            }

            float h = Math.max(entity.getBbHeight(), 0.1f);
            float w = Math.max(entity.getBbWidth(), 0.1f);
            float target = 0.8f;
            float scale = target / Math.max(h, w);

            pose.pushPose();
            pose.scale(scale, scale, scale);
            pose.mulPose(Axis.YP.rotationDegrees(180f));

            EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            dispatcher.setRenderShadow(false);
            dispatcher.render(entity, 0, 0, 0, 0f, 0f, pose, buffer, light);
            dispatcher.setRenderShadow(true);
            pose.popPose();
        } catch (Exception ignored) {
        }
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
