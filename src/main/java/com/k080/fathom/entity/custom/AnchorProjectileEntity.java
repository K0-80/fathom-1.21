package com.k080.fathom.entity.custom;

import com.k080.fathom.Fathom;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AnchorProjectileEntity extends PersistentProjectileEntity {
    private static final TrackedData<Boolean> IS_RETURNING = DataTracker.registerData(AnchorProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

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

        if (this.isReturning()) {
            if (!this.getWorld().isClient && this.distanceTo(owner) < 2.0f) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_BUNDLE_INSERT, SoundCategory.NEUTRAL,0.6F, 1F );
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.NEUTRAL,0.6F, 1F );

                this.discard();
                return;
            }
            Vec3d directionToOwner = owner.getEyePos().subtract(this.getPos());
            this.setVelocity(directionToOwner.normalize().multiply(1.5));
        }

        else if (this.inGround) {
            this.getWorld().addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.NEUTRAL,2F, 0.7F );
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_ANVIL_FALL, SoundCategory.NEUTRAL,0.3F, 0.9F );

            float knockbackRadius = 5.0f;
            for (LivingEntity nearbyEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(knockbackRadius), LivingEntity::isAlive)) {
                if (nearbyEntity == owner) continue;
                Vec3d pushDirection = nearbyEntity.getPos().subtract(this.getPos()).normalize();
                nearbyEntity.addVelocity(pushDirection.x, pushDirection.y + 0.2, pushDirection.z);
            }
            this.setNoClip(true);
            this.setReturning(true);
        }
        super.tick();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
            Entity hitEntity = entityHitResult.getEntity();
        float damage = 10F;
        hitEntity.damage(this.getWorld().getDamageSources().create(DamageTypes.ARROW, this, this.getOwner()), damage);
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
        return this.isOwner(player);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return ItemStack.EMPTY;
    }
    @Override
    protected float getDragInWater() {
        return 1.0F;
    }
}