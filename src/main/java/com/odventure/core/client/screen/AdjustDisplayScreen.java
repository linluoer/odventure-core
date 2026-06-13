package com.odventure.core.client.screen;

import com.odventure.core.network.AdjustDisplayPacket;
import com.odventure.core.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class AdjustDisplayScreen extends Screen {
    private final BlockPos pos;
    private final float initScale;
    private final float initYaw;
    private final float initY;

    private EditBox scaleBox;
    private EditBox yawBox;
    private EditBox yBox;

    public AdjustDisplayScreen(BlockPos pos, float scale, float yaw, float y) {
        super(Component.translatable("screen.odventure_core.adjust"));
        this.pos = pos;
        this.initScale = scale;
        this.initYaw = yaw;
        this.initY = y;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        scaleBox = new EditBox(this.font, cx - 60, cy - 40, 120, 18, Component.empty());
        scaleBox.setValue(Float.toString(initScale));
        addRenderableWidget(scaleBox);

        yawBox = new EditBox(this.font, cx - 60, cy - 10, 120, 18, Component.empty());
        yawBox.setValue(Float.toString(initYaw));
        addRenderableWidget(yawBox);

        yBox = new EditBox(this.font, cx - 60, cy + 20, 120, 18, Component.empty());
        yBox.setValue(Float.toString(initY));
        addRenderableWidget(yBox);

        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> apply())
                .bounds(cx - 100, cy + 50, 95, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose())
                .bounds(cx + 5, cy + 50, 95, 20).build());

        setInitialFocus(scaleBox);
    }

    private void apply() {
        float scale = parse(scaleBox.getValue(), initScale);
        float yaw = parse(yawBox.getValue(), initYaw);
        float y = parse(yBox.getValue(), initY);
        ModNetwork.CHANNEL.sendToServer(new AdjustDisplayPacket(pos, scale, yaw, y));
        onClose();
    }

    private float parse(String s, float fallback) {
        try {
            return Float.parseFloat(s.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        int cx = this.width / 2;
        int cy = this.height / 2;
        g.drawCenteredString(this.font, this.title, cx, cy - 65, 0xFFFFFF);
        g.drawString(this.font, Component.translatable("screen.odventure_core.scale"), cx - 60, cy - 52, 0xA0A0A0);
        g.drawString(this.font, Component.translatable("screen.odventure_core.yaw"), cx - 60, cy - 22, 0xA0A0A0);
        g.drawString(this.font, Component.translatable("screen.odventure_core.yoff"), cx - 60, cy + 8, 0xA0A0A0);
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
