package com.k080.fathom.entity.custom;

import com.k080.fathom.item.ModItems;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AnchorProjectileEntity extends PersistentProjectileEntity {
    private static final TrackedData<Boolean> IS_RETURNING = DataTracker.registerData(AnchorProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final ItemStack DEFAULT_STACK = new ItemStack(ModItems.ANCHOR);

    public  AnchorProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_RETURNING, false);
    }


    @Override
    public void tick() {
        Entity owner = this.getOwner();

        if (owner == null || !owner.isAlive()) {
            this.discard();
            return;
        }

        if (!this.inGround && this.age % 10 == 0) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_VEX_CHARGE, this.getSoundCategory(), 0.2f, 0.6f);
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_BEACON_AMBIENT, this.getSoundCategory(), 0.15f, 0.7f);
        }

        if (this.isReturning()) {
            if (!this.getWorld().isClient && this.distanceTo(owner) < 2.5f) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_TRIDENT_RETURN, this.getSoundCategory(), 0.8f, 1.1f);
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, this.getSoundCategory(), 0.7f, 1.2f);                this.discard();
                return;
            }
            Vec3d directionToOwner = owner.getEyePos().subtract(this.getPos());
            this.setVelocity(directionToOwner.normalize().multiply(1.5));
        }
        else if (this.distanceTo(owner) > 19.0f) {
            this.setReturning(true);
        }

        else if (this.inGround) {
            this.getWorld().addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);

            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_ANVIL_LAND, this.getSoundCategory(), 0.3f, 0.9f);
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST, this.getSoundCategory(), 0.8f, 1.0f);
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_SHIELD_BLOCK, this.getSoundCategory(), 0.7f, 0.7f);

            float knockbackRadius = 3.0f;
            for (LivingEntity nearbyEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(knockbackRadius), LivingEntity::isAlive)) {
                //if (nearbyEntity == owner) continue;
                Vec3d pushDirection = nearbyEntity.getPos().subtract(this.getPos()).normalize();
                nearbyEntity.addVelocity(pushDirection.x, pushDirection.y + 0.2, pushDirection.z);
                nearbyEntity.fallDistance = 0.0f;
            }
            this.inGround = false;
            this.setNoClip(true);
            this.setReturning(true);
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
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_TRIDENT_RETURN, this.getSoundCategory(), 0.8f, 1.1f);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, this.getSoundCategory(), 2.0f, 1.2f);                this.discard();
        return this.isOwner(player);
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