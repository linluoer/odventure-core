package com.odventure.core.network;

import com.odventure.core.OdventureCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(OdventureCore.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int id = 0;

    public static void register() {
        CHANNEL.messageBuilder(NameTargetPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(NameTargetPacket::encode)
                .decoder(NameTargetPacket::decode)
                .consumerMainThread(NameTargetPacket::handle)
                .add();

        CHANNEL.messageBuilder(AdjustDisplayPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(AdjustDisplayPacket::encode)
                .decoder(AdjustDisplayPacket::decode)
                .consumerMainThread(AdjustDisplayPacket::handle)
                .add();
    }
}
