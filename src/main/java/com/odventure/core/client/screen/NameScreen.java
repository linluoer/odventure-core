package com.odventure.core.client.screen;

import com.odventure.core.network.ModNetwork;
import com.odventure.core.network.NameTargetPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class NameScreen extends Screen {
    private final BlockPos pos;
    private final String initial;
    private EditBox nameBox;

    public NameScreen(BlockPos pos, String initial) {
        super(Component.translatable("screen.odventure_core.name"));
        this.pos = pos;
        this.initial = initial == null ? "" : initial;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        nameBox = new EditBox(this.font, cx - 100, cy - 10, 200, 20, Component.empty());
        nameBox.setMaxLength(50);
        nameBox.setValue(initial);
        addRenderableWidget(nameBox);
        setInitialFocus(nameBox);

        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> confirm())
                .bounds(cx - 100, cy + 20, 95, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose())
                .bounds(cx + 5, cy + 20, 95, 20).build());
    }

    private void confirm() {
        ModNetwork.CHANNEL.sendToServer(new NameTargetPacket(pos, nameBox.getValue()));
        onClose();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        g.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 40, 0xFFFFFF);
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
