package com.odventure.core.client;

import com.odventure.core.client.render.MedalItemRenderer;
import com.odventure.core.client.render.TrophyItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class ClientItemExtensions {

    public static IClientItemExtensions trophy() {
        return new IClientItemExtensions() {
            private TrophyItemRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new TrophyItemRenderer();
                return renderer;
            }
        };
    }

    public static IClientItemExtensions medal() {
        return new IClientItemExtensions() {
            private MedalItemRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new MedalItemRenderer();
                return renderer;
            }
        };
    }
}
