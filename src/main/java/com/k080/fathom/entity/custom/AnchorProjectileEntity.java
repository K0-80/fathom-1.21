package com.k080.fathom.entity.custom;

import com.k080.fathom.Fathom;
import com.k080.fathom.damage.ModDamageTypes;
import com.k080.fathom.item.ModItems;
import com.k080.fathom.item.custom.AnchorItem;
import com.k080.fathom.util.ShockwaveUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AnchorProjectileEntity extends PersistentProjectileEntity {

    private static final TrackedData<Boolean> IS_RETURNING = DataTracker.registerData(AnchorProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_STUCK = DataTracker.registerData(AnchorProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Integer> HEAVE_LEVEL = DataTracker.registerData(AnchorProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> UNDERTOW_LEVEL = DataTracker.registerData(AnchorProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CRUSHING_DEPTH_LEVEL = DataTracker.registerData(AnchorProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final ItemStack DEFAULT_STACK = new ItemStack(ModItems.ANCHOR);
    private static final double GRAVITY = 0.02D;  //0.03D old
    private static final float WATER_DRAG = 1.0F;
    private static final int MAX_FLIGHT_TICKS = 300;
    private static final double RETURN_SPEED = 2.5;
    private static final float PICKUP_DISTANCE = 2f;
    private static final float DAMAGE = 10.0f;
    private static final double MAX_DISTANCE = 30.0D;

    private Entity stuckEntity;
    private float stuckYaw;
    private float stuckPitch;

    public AnchorProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_RETURNING, false);
        builder.add(IS_STUCK, false);
        builder.add(HEAVE_LEVEL, 0);
        builder.add(UNDERTOW_LEVEL, 0);
        builder.add(CRUSHING_DEPTH_LEVEL, 0);
    }

    public boolean isReturning() { return this.getDataTracker().get(IS_RETURNING); }
    public void setReturning(boolean returning) { this.getDataTracker().set(IS_RETURNING, returning); }

    public boolean isStuck() { return this.getDataTracker().get(IS_STUCK); }
    public void setStuck(boolean stuck) { this.getDataTracker().set(IS_STUCK, stuck); }

    public void setHeaveLevel(int level) { this.getDataTracker().set(HEAVE_LEVEL, level); }
    public void setUndertowLevel(int level) { this.getDataTracker().set(UNDERTOW_LEVEL, level); }
    public void setCrushingDepthLevel(int level) { this.getDataTracker().set(CRUSHING_DEPTH_LEVEL, level); }
    public int getHeaveLevel() { return this.getDataTracker().get(HEAVE_LEVEL); }
    public int getUndertowLevel() { return this.getDataTracker().get(UNDERTOW_LEVEL); }
    public int getCrushingDepthLevel() { return this.getDataTracker().get(CRUSHING_DEPTH_LEVEL); }

    @Override
    public void tick() {
        if (this.stuckEntity != null) {
            if (!this.stuckEntity.isAlive() || this.stuckEntity.isRemoved()) {
                this.stuckEntity = null;
                this.setStuck(false);
            } else {
                this.setPosition(this.stuckEntity.getBoundingBox().getCenter());
            }
        }

        Entity owner = this.getOwner();
        if (owner == null || !owner.isAlive() || owner.isRemoved()) {
            if (!this.getWorld().isClient) {
                this.discard();
            }
            return;
        }

        if(this.distanceTo(owner) > MAX_DISTANCE) {
            this.startReturning();
        }

        if (isReturning()) {
            tickReturnMovement(owner);
        } else if (isFlying()) {
            if (this.age > MAX_FLIGHT_TICKS) {
                this.discard();
                return;
            }
        }

        super.tick();

        if (this.stuckEntity != null) {
            this.setYaw(this.stuckYaw);
            this.setPitch(this.stuckPitch);
        }
    }

    private void tickReturnMovement(Entity owner) {
        if (this.distanceTo(owner) < PICKUP_DISTANCE) {
            onPickup((PlayerEntity) owner);
            return;
        }
        Vec3d directionToOwner = owner.getBoundingBox().getCenter().subtract(this.getPos()).normalize();
        this.setVelocity(directionToOwner.multiply(RETURN_SPEED));
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (isReturning() || this.stuckEntity != null) {
            return;
        }

        Entity hitEntity = entityHitResult.getEntity();
        if (this.isOwner(hitEntity)) {
            return;
        }

        playEntityHitSounds();
        applyDirectHitDamage(hitEntity);
        this.stuckEntity = hitEntity;
        this.setStuck(true);
        this.stuckYaw = this.getYaw();
        this.stuckPitch = this.getPitch();
        this.setVelocity(Vec3d.ZERO);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (!isReturning()) {
            playImpactSounds();
//            ShockwaveUtil.triggerShockwave(this.getWorld(), blockHitResult.getBlockPos(), 3, 1);
        }
        super.onBlockHit(blockHitResult);
    }

    private void applyDirectHitDamage(Entity hitEntity) {
        Entity owner = this.getOwner();
        DamageSource damageSource = ModDamageTypes.of(this.getWorld(), ModDamageTypes.ANCHOR_THROW, this, owner);
        hitEntity.damage(damageSource, DAMAGE);
    }

    public void startReturning() {
        this.stuckEntity = null;
        this.setReturning(true);
        this.setStuck(false);
        this.setNoClip(true);
        this.inGround = false;
    }

    public boolean isFlying() {
        return !this.isStuck() && !this.inGround;
    }

    private void onPickup(PlayerEntity player) {
        playPickupSounds();
        this.discard();
    }


    private void playImpactSounds() {
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_ANVIL_LAND, this.getSoundCategory(), 0.2f, 0.5f);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_SHIELD_BLOCK, this.getSoundCategory(), 0.7f, 0.7f);
    }

    private void playEntityHitSounds() {
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_TRIDENT_HIT, this.getSoundCategory(), 1.0f, 0.9f);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0f, 0.8f);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_SHIELD_BLOCK, this.getSoundCategory(), 0.7f, 0.7f);
    }

    private void playPickupSounds() {
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_TRIDENT_RETURN, this.getSoundCategory(), 0.8f, 1.1f);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, this.getSoundCategory(), 0.7f, 1.2f);
    }

    @Override
    public boolean hasNoGravity() {return super.hasNoGravity() || this.isStuck();}

    @Override
    protected SoundEvent getHitSound() {return SoundEvents.BLOCK_CHAIN_HIT;}

    @Override
    protected boolean tryPickup(PlayerEntity player) {return false;}

    @Override
    protected ItemStack getDefaultItemStack() {return DEFAULT_STACK.copy();}

    @Override
    protected float getDragInWater() {return WATER_DRAG;}

    @Override
    protected double getGravity() {return GRAVITY;}
}