package com.odventure.core.network;

import com.odventure.core.block.entity.TrophyBaseBlockEntity;
import com.odventure.core.data.TrophyData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AdjustDisplayPacket {
    private final BlockPos pos;
    private final float scale;
    private final float yaw;
    private final float yOff;

    public AdjustDisplayPacket(BlockPos pos, float scale, float yaw, float yOff) {
        this.pos = pos;
        this.scale = scale;
        this.yaw = yaw;
        this.yOff = yOff;
    }

    public static void encode(AdjustDisplayPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeFloat(msg.scale);
        buf.writeFloat(msg.yaw);
        buf.writeFloat(msg.yOff);
    }

    public static AdjustDisplayPacket decode(FriendlyByteBuf buf) {
        return new AdjustDisplayPacket(buf.readBlockPos(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public static void handle(AdjustDisplayPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null || !player.isCreative()) return;
            Level level = player.level();
            if (!level.isLoaded(msg.pos)) return;
            if (player.distanceToSqr(Vec3.atCenterOf(msg.pos)) > 64.0) return;

            BlockEntity be = level.getBlockEntity(msg.pos);
            if (be instanceof TrophyBaseBlockEntity trophy) {
                TrophyData d = trophy.getData();
                if (d.isContentLocked() && d.isNameLocked()) {
                    d.setDisplayScale(clamp(msg.scale, 0.05f, 10f));
                    d.setDisplayYaw(msg.yaw % 360f);
                    d.setDisplayY(clamp(msg.yOff, -1f, 2f));
                    trophy.setData(d);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}
