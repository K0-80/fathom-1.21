package com.k080.fathom.entity.custom;

import com.k080.fathom.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class AmethystShardProjectileEntity extends PersistentProjectileEntity {

    private LivingEntity target;
    private static final double HOMING_RADIUS = 16.0;
    private static final double DAMAGE_DECAY_PER_TICK = 0.05;

    private static final List<Vector3f> PARTICLE_COLORS = List.of(
            new Vector3f(0.6f, 0.4f, 0.8f),  // Purple
            new Vector3f(0.7f, 0.5f, 0.9f),  // Lighter Purple
            new Vector3f(0.85f, 0.7f, 1.0f) // Light Magenta
    );

    public AmethystShardProjectileEntity(EntityType<? extends AmethystShardProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    public AmethystShardProjectileEntity(World world, LivingEntity owner) {
        super(ModEntities.AMETHYST_SHARD_PROJECTILE, owner, world, ItemStack.EMPTY, null);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    private void spawnTrailParticles() {
        int particleCount = 3;

        for (int i = 0; i < particleCount; i++) {
            Vector3f color = PARTICLE_COLORS.get(this.random.nextInt(PARTICLE_COLORS.size()));
            float size = 0.4f + this.random.nextFloat() * 0.4f;
            DustParticleEffect particleEffect = new DustParticleEffect(color, size);
            this.getWorld().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.05, 0.05, 0.05);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            spawnTrailParticles();
        }

        if (!this.getWorld().isClient) {
            this.setDamage(Math.max(0, this.getDamage() - DAMAGE_DECAY_PER_TICK));
        }

        if (this.inGround) {
            this.discard();
        }

        if (!this.getWorld().isClient) {
            if (target == null || !target.isAlive() || target.squaredDistanceTo(this) > HOMING_RADIUS * HOMING_RADIUS) {
                findNewTarget();
            }

            if (target != null) {
                Vec3d directionToTarget = target.getEyePos().subtract(this.getPos()).normalize();
                Vec3d currentVelocity = this.getVelocity();
                Vec3d newVelocity = currentVelocity.lerp(directionToTarget.multiply(currentVelocity.length()), 0.15f);

                this.setVelocity(newVelocity);
            }
        }
    }

    private void findNewTarget() {
        Box searchBox = this.getBoundingBox().expand(HOMING_RADIUS);
        List<LivingEntity> potentialTargets = this.getWorld().getNonSpectatingEntities(LivingEntity.class, searchBox);

        LivingEntity closestTarget = null;
        double minDistanceSq = Double.MAX_VALUE;

        for (LivingEntity potentialTarget : potentialTargets) {
            if (potentialTarget == this.getOwner() || !potentialTarget.isAttackable()) {
                continue;
            }
            double distanceSq = this.squaredDistanceTo(potentialTarget);
            if (distanceSq < minDistanceSq) {
                minDistanceSq = distanceSq;
                closestTarget = potentialTarget;
            }
        }
        this.target = closestTarget;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity target = entityHitResult.getEntity();
        if (target instanceof LivingEntity livingEntity) {
            livingEntity.timeUntilRegen = 5;
        }
    }

    @Override
    protected void applyGravity() {
        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.02, 0.0));
        }
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.BLOCK_AMETHYST_BLOCK_HIT;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.getWorld().isClient) {
            this.discard();
        }
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.AMETHYST_SHARD);
    }
}