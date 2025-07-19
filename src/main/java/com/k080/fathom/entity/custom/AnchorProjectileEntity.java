package com.k080.fathom.entity.custom;

import com.k080.fathom.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AnchorProjectileEntity extends PersistentProjectileEntity {
    private static final TrackedData<Boolean> IS_RETURNING = DataTracker.registerData(AnchorProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final ItemStack DEFAULT_STACK = new ItemStack(ModItems.ANCHOR);

    public AnchorProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_RETURNING, false);
    }

    public boolean isProjectileOwner(Entity entity) {
        return this.isOwner(entity);
    }

    @Override
    public void tick() {
        Entity owner = this.getOwner();

        if (owner == null || !owner.isAlive()) {
            this.discard();
            return;
        }

        if (this.isReturning()) {
            if (!this.getWorld().isClient && this.distanceTo(owner) < 2.5f) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_TRIDENT_RETURN, this.getSoundCategory(), 0.8f, 1.1f);
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, this.getSoundCategory(), 0.7f, 1.2f);
                this.discard();
                return;
            }
            Vec3d directionToOwner = owner.getEyePos().subtract(this.getPos());
            this.setVelocity(directionToOwner.normalize().multiply(1.5));
            super.tick();
            return;
        }

        if (this.inGround) {
            BlockPos pos = this.getBlockPos();
            BlockState blockState = this.getWorld().getBlockState(pos);
            if (blockState.isAir()) {
                blockState = this.getWorld().getBlockState(pos.down());
            }
            if (!blockState.isAir()) {
                double spawnSpread = 0.5;
                for (int i = 0; i < 20; i++) {
                    double px = this.getX() + (this.random.nextDouble() - 0.5) * spawnSpread;
                    double py = this.getY() + this.random.nextDouble() * spawnSpread;
                    double pz = this.getZ() + (this.random.nextDouble() - 0.5) * spawnSpread;
                    double d = this.random.nextGaussian() * 2.5;
                    double e = this.random.nextGaussian() * 2.5;
                    double f = this.random.nextGaussian() * 2.5;
                    this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), px, py, pz, d, e, f);
                }
            }

            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_ANVIL_LAND, this.getSoundCategory(), 0.2f, 0.5f);
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST, this.getSoundCategory(), 0.8f, 1.2f);
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_SHIELD_BLOCK, this.getSoundCategory(), 0.7f, 0.7f);

            float knockbackRadius = 3.0f;
            for (LivingEntity nearbyEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(knockbackRadius), LivingEntity::isAlive)) {
                //for (LivingEntity nearbyEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(knockbackRadius), e -> e.isAlive() && !this.isOwner(e))) {
                Vec3d pushDirection = nearbyEntity.getPos().subtract(this.getPos()).normalize();
                nearbyEntity.addVelocity(pushDirection.x, pushDirection.y + 0.2, pushDirection.z);
                nearbyEntity.fallDistance = 0.0f;
            }

            this.setReturning(true);
            this.setNoClip(true);
        }

        if (this.distanceTo(owner) > 19.0f || this.age > 100) {
            this.setReturning(true);
            this.setNoClip(true);
        }

        if (!this.inGround && this.age % 10 == 0) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_VEX_CHARGE, this.getSoundCategory(), 0.2f, 0.6f);
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_BEACON_AMBIENT, this.getSoundCategory(), 0.15f, 0.7f);
        }

        super.tick();
    }


    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();
        Entity owner = this.getOwner();

        if (owner != null && hitEntity.getUuid().equals(owner.getUuid())) {
            return;
        }

        hitEntity.damage(this.getWorld().getDamageSources().create(DamageTypes.TRIDENT, this, this.getOwner()), 4F);

        hitEntity.setVelocity(Vec3d.ZERO);
        if (owner != null) {
            double maxEffectiveDistance = 25.0;
            double minStrength = 0.5;
            double maxStrength = 2.5;

            double distance = owner.distanceTo(hitEntity);
            double progress = MathHelper.clamp(distance / maxEffectiveDistance, 0.0, 1.0);
            double pullStrength = MathHelper.lerp(progress, minStrength, maxStrength);

            Vec3d pullDirection = owner.getEyePos().subtract(hitEntity.getPos()).normalize();
            Vec3d pullVelocity = pullDirection.multiply(pullStrength);

            hitEntity.addVelocity(pullVelocity.x, pullVelocity.y + 0.1, pullVelocity.z);
        }


        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_TRIDENT_HIT, this.getSoundCategory(), 1.0f, 0.9f);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0f, 0.8f);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, this.getSoundCategory(), 0.3f, 1.4f);

        this.inGround = false;
        this.setNoClip(true);
        this.setReturning(true);
    }

    public boolean isReturning() {
        return this.getDataTracker().get(IS_RETURNING);
    }

    public void setReturning(boolean returning) {
        this.getDataTracker().set(IS_RETURNING, returning);
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        if (this.isOwner(player) || this.isReturning()) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_TRIDENT_RETURN, this.getSoundCategory(), 0.8f, 1.1f);
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, this.getSoundCategory(), 2.0f, 1.2f);
            this.discard();
            return true;
        }
        return false;
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return DEFAULT_STACK.copy();
    }

    @Override
    protected float getDragInWater() {
        return 1.0F;
    }

    @Override
    protected double getGravity() {
        return 0.03F;
    }
}