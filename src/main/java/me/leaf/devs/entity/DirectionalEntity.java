package me.leaf.devs.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class DirectionalEntity extends Entity {
    private static final EntityDataAccessor<Float> ROTATION_YAW =
            SynchedEntityData.defineId(DirectionalEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ROTATION_PITCH =
            SynchedEntityData.defineId(DirectionalEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SIZE =
            SynchedEntityData.defineId(DirectionalEntity.class, EntityDataSerializers.FLOAT);

    public DirectionalEntity(EntityType<?> type, Level level) {
        super(type, level);

    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ROTATION_YAW, 0.0f);
        this.entityData.define(ROTATION_PITCH, 0.0f);
        this.entityData.define(SIZE, 3.0f);
    }

    public void setRotation(float yaw, float pitch) {
        this.entityData.set(ROTATION_YAW, yaw);
        this.entityData.set(ROTATION_PITCH, pitch);
    }

    public float getYaw() {
        return this.entityData.get(ROTATION_YAW);
    }

    public float getPitch() {
        return this.entityData.get(ROTATION_PITCH);
    }

    public float getSize() {
        return this.entityData.get(SIZE);
    }

    public void setScale(float size) {
        this.entityData.set(SIZE, size);
    }

    @Override
    public void tick() {
        super.tick();
        // Update logic, if needed
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
