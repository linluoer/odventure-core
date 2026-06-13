package com.odventure.core.client;

import com.odventure.core.client.screen.AdjustDisplayScreen;
import com.odventure.core.client.screen.NameScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class ClientHelper {
    public static void openNameScreen(BlockPos pos, String initial) {
        Minecraft.getInstance().setScreen(new NameScreen(pos, initial));
    }

    public static void openAdjustScreen(BlockPos pos, float scale, float yaw, float yOff) {
        Minecraft.getInstance().setScreen(new AdjustDisplayScreen(pos, scale, yaw, yOff));
    }
}
