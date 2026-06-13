package com.odventure.core.block.entity;

import com.odventure.core.data.MedalData;
import com.odventure.core.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class MedalBlockEntity extends BlockEntity {
    private MedalData data = new MedalData();

    public MedalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MEDAL.get(), pos, state);
    }

    public MedalData getData() {
        return data;
    }

    public void setData(MedalData data) {
        this.data = data == null ? new MedalData() : data;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos()).inflate(1.0);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        data.save(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.data = MedalData.load(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        data.save(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            this.data = MedalData.load(pkt.getTag());
        }
    }
}
