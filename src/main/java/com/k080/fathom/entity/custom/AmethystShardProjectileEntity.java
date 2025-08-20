package com.k080.fathom.entity.custom;

import com.k080.fathom.entity.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class AmethystShardProjectileEntity extends PersistentProjectileEntity {

    private int ticksInGround = 0;
    @Nullable
    private LivingEntity homingTarget;
    private int searchTicks = 0;

    public AmethystShardProjectileEntity(EntityType<? extends AmethystShardProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    public AmethystShardProjectileEntity(LivingEntity owner, World world) {
        super(ModEntities.AMETHYST_SHARD_PROJECTILE, owner, world, ItemStack.EMPTY, null);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient() && !this.inGround) {
            if (this.homingTarget == null || !this.homingTarget.isAlive() || this.homingTarget.isRemoved()) {
                if (this.searchTicks < 20) {
                    this.findTarget();
                }
                this.searchTicks++;
            } else {
                Vec3d currentVelocity = this.getVelocity();
                Vec3d directionToTarget = this.homingTarget.getEyePos().subtract(this.getPos()).normalize();

                double homingStrength = 0.05;
                Vec3d homingVelocity = directionToTarget.multiply(homingStrength);

                Vec3d newVelocity = currentVelocity.add(homingVelocity);

                this.setVelocity(newVelocity.normalize().multiply(currentVelocity.length()));
            }
        }

        if (this.inGround) {
            this.ticksInGround++;
        }
        if (this.ticksInGround > 2 && !this.getWorld().isClient()) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, this.getSoundCategory(), 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
            this.discard();
        }
    }

    private void findTarget() {
        if (!(this.getOwner() instanceof LivingEntity owner)) {
            return;
        }

        Box searchBox = this.getBoundingBox().expand(24.0);
        this.getWorld().getEntitiesByClass(LivingEntity.class, searchBox, entity ->
                        entity.isAlive() && entity.isAttackable() && !entity.equals(owner))
                .stream()
                .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(this)))
                .ifPresent(target -> this.homingTarget = target);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.AMETHYST_SHARD);
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.BLOCK_AMETHYST_BLOCK_HIT;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }
    @Override
    protected double getGravity() {
        return 0.02F;
    }
}