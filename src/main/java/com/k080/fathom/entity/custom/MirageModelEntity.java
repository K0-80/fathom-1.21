package com.k080.fathom.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.UUID;

public class MirageModelEntity extends Entity {
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(MirageModelEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Integer> MODEL_TYPE = DataTracker.registerData(MirageModelEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final TrackedData<Float> TARGET_X = DataTracker.registerData(MirageModelEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> TARGET_Y = DataTracker.registerData(MirageModelEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> TARGET_Z = DataTracker.registerData(MirageModelEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> HAS_TARGET = DataTracker.registerData(MirageModelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);


    public static final int STATIONARY_TICKS = 7;
    public static final int FLY_DURATION = 15;
    private static final int MAX_LIFETIME = STATIONARY_TICKS + FLY_DURATION;

    // Client-side only
    @Nullable
    private Vec3d clientSpawnPos;

    public MirageModelEntity(EntityType<MirageModelEntity> type, World world) {
        super(type, world);
        this.setNoGravity(true);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(OWNER_UUID, Optional.empty());
        builder.add(MODEL_TYPE, 0);
        builder.add(TARGET_X, 0f);
        builder.add(TARGET_Y, 0f);
        builder.add(TARGET_Z, 0f);
        builder.add(HAS_TARGET, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            if (this.age > MAX_LIFETIME) {
                this.discard();
            }
        } else {
            // Stop client physics from moving the entity
            this.setVelocity(Vec3d.ZERO);
            spawnParticles();
        }
    }

    private void spawnParticles() {
        if (!this.getWorld().isClient()) return;

        DustParticleEffect particleEffect = new DustParticleEffect(new Vector3f(0.8f, 0.9f, 1.0f), 1.0f);

        if (this.age <= STATIONARY_TICKS) {
            int particleCount = 5;
            for (int i = 0; i < particleCount; i++) {
                double radius = 1.5;
                double offsetX = this.random.nextDouble() * 2.0 - 1.0;
                double offsetY = this.random.nextDouble() * 2.0 - 1.0;
                double offsetZ = this.random.nextDouble() * 2.0 - 1.0;
                Vec3d offset = new Vec3d(offsetX, offsetY, offsetZ).normalize().multiply(radius);
                Vec3d particleSpawnPos = this.getPos().add(offset);

                Vec3d targetCenter = this.getPos().add(0, 0.8, 0);
                Vec3d velocity = targetCenter.subtract(particleSpawnPos).normalize().multiply(0.15);

                this.getWorld().addParticle(particleEffect, particleSpawnPos.x, particleSpawnPos.y, particleSpawnPos.z, velocity.x, velocity.y, velocity.z);
            }
        } else if (this.age <= MAX_LIFETIME) {
            Vec3d spawnPos = this.clientSpawnPos;
            Vec3d targetPos = this.getClientTargetPos();

            if (spawnPos == null || targetPos == null) return;

            float progress = (this.age - STATIONARY_TICKS) / (float) FLY_DURATION;
            progress = MathHelper.clamp(progress, 0.0f, 1.0f);
            float easedProgress = 1.0f - (float)Math.pow(1.0f - progress, 3.0);
            Vec3d currentPos = spawnPos.lerp(targetPos, easedProgress);

            int particleCount = 3;
            for (int i = 0; i < particleCount; i++) {
                double offsetX = (this.random.nextDouble() - 0.5) * 0.5;
                double offsetY = (this.random.nextDouble() - 0.5) * 0.5;
                double offsetZ = (this.random.nextDouble() - 0.5) * 0.5;

                this.getWorld().addParticle(particleEffect,
                        currentPos.x + offsetX,
                        currentPos.y + 0.8 + offsetY,
                        currentPos.z + offsetZ,
                        0, 0, 0);
            }
        }
    }


    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        if (this.getWorld().isClient()) {
            this.clientSpawnPos = this.getPos();
        }
    }

    public void setTargetPosition(Vec3d pos) {
        this.getDataTracker().set(TARGET_X, (float)pos.x);
        this.getDataTracker().set(TARGET_Y, (float)pos.y);
        this.getDataTracker().set(TARGET_Z, (float)pos.z);
        this.getDataTracker().set(HAS_TARGET, true);
    }

    @Nullable
    public Vec3d getClientTargetPos() {
        if (!this.getDataTracker().get(HAS_TARGET)) {
            return null;
        }
        return new Vec3d(
                this.getDataTracker().get(TARGET_X),
                this.getDataTracker().get(TARGET_Y),
                this.getDataTracker().get(TARGET_Z)
        );
    }

    public int getModelType() {
        return this.getDataTracker().get(MODEL_TYPE);
    }

    public void setModelType(int type) {
        this.getDataTracker().set(MODEL_TYPE, type);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.getDataTracker().set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }
}