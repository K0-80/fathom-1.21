package com.k080.fathom.entity.block;

import com.k080.fathom.entity.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShockwaveBlockEntity extends Entity {

    private BlockState blockState = Blocks.AIR.getDefaultState();
    private int animationDelay;
    private final int animationDuration = 20;

    public ShockwaveBlockEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = true;
    }

    public static ShockwaveBlockEntity create(World world, BlockPos pos, BlockState state, int delay) {
        ShockwaveBlockEntity entity = new ShockwaveBlockEntity(ModEntities.SHOCKWAVE_BLOCK, world);
        entity.blockState = state;
        entity.animationDelay = delay;
        entity.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        return entity;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }


    @Override
    public void tick() {

        if (!this.getWorld().isClient) {
            return;
        }
        if (this.age > this.animationDelay + this.animationDuration) {
            this.discard();
        }
    }

    public float getVerticalOffset(float tickDelta) {
        float interpolatedAge = (this.age - 1) + tickDelta;
        float ticksSinceStart = interpolatedAge - this.animationDelay;

        if (ticksSinceStart < 0) {
            return 0f;
        }

        float progress = Math.min(ticksSinceStart / (float) this.animationDuration, 1.0f);
        return (float) Math.sin(progress * Math.PI) * 0.75f;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }
}