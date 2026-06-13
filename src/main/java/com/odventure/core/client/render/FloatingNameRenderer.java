package com.odventure.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

public class FloatingNameRenderer {
    public static void render(String text, float height, PoseStack pose,
                              MultiBufferSource buffer, int light) {
        if (text == null || text.isEmpty()) return;
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        pose.pushPose();
        pose.translate(0.5, height, 0.5);
        pose.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        pose.scale(-0.025f, -0.025f, 0.025f);
        Matrix4f matrix = pose.last().pose();

        float bgOpacity = mc.options.getBackgroundOpacity(0.25f);
        int bgColor = (int) (bgOpacity * 255.0f) << 24;
        float x = -font.width(text) / 2.0f;

        font.drawInBatch(text, x, 0, 0xFFFFFFFF, false, matrix, buffer,
                Font.DisplayMode.NORMAL, bgColor, light);
        pose.popPose();
    }
}
