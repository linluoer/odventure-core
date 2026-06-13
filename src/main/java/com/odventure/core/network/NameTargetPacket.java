package com.odventure.core.network;

import com.odventure.core.block.entity.MedalBlockEntity;
import com.odventure.core.block.entity.TrophyBaseBlockEntity;
import com.odventure.core.data.MedalData;
import com.odventure.core.data.TrophyData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class NameTargetPacket {
    private final BlockPos pos;
    private final String name;

    public NameTargetPacket(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    public static void encode(NameTargetPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeUtf(msg.name);
    }

    public static NameTargetPacket decode(FriendlyByteBuf buf) {
        return new NameTargetPacket(buf.readBlockPos(), buf.readUtf());
    }

    public static void handle(NameTargetPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            Level level = player.level();
            if (!level.isLoaded(msg.pos)) return;

            if (player.distanceToSqr(Vec3.atCenterOf(msg.pos)) > 64.0) return;

            BlockEntity be = level.getBlockEntity(msg.pos);
            if (be instanceof MedalBlockEntity medal) {
                MedalData d = medal.getData();
                if (d.isContentLocked() && !d.isNameLocked()) {
                    d.setCustomName(msg.name);
                    d.setNameLocked(true);
                    medal.setData(d);
                }
            } else if (be instanceof TrophyBaseBlockEntity trophy) {
                TrophyData d = trophy.getData();
                if (d.isContentLocked() && !d.isNameLocked()) {
                    d.setCustomName(msg.name);
                    d.setNameLocked(true);
                    trophy.setData(d);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
