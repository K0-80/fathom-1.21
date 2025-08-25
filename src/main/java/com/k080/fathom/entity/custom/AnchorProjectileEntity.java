package com.k080.fathom.entity.custom;

import com.k080.fathom.damage.ModDamageTypes;
import com.k080.fathom.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AnchorProjectileEntity extends PersistentProjectileEntity {

    // region Constants
    private static final TrackedData<Boolean> IS_RETURNING = DataTracker.registerData(AnchorProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> MAELSTROM_LEVEL = DataTracker.registerData(AnchorProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MOMENTUM_LEVEL = DataTracker.registerData(AnchorProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> RESONANCE_LEVEL = DataTracker.registerData(AnchorProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final ItemStack DEFAULT_STACK = new ItemStack(ModItems.ANCHOR);

    private static final double GRAVITY = 0.03D;
    private static final float WATER_DRAG = 1.0F;

    private static final int MAX_FLIGHT_TICKS = 250;
    private static final float BASE_MAX_DISTANCE_FROM_OWNER = 15f;
    private static final float MOMENTUM_DISTANCE_SCALE = 3f;

    private static final double BASE_RETURN_SPEED = 2.0;
    private static final double MOMENTUM_RETURN_SPEED_SCALE = 0.4;
    private static final float PICKUP_DISTANCE = 2.5f;

    private static final float BASE_DAMAGE = 1.0f;
    private static final float MOMENTUM_DAMAGE_SCALE = 1.0f;

    private static final double MOMENTUM_PULL_MAX_EFFECTIVE_DISTANCE = 10.0;
    private static final double MOMENTUM_PULL_DISTANCE_SCALE = 5.0;
    private static final double MOMENTUM_PULL_MIN_STRENGTH = 0.5;
    private static final double MOMENTUM_PULL_MIN_STRENGTH_SCALE = 0.05;
    private static final double MOMENTUM_PULL_MAX_STRENGTH = 2.5;
    private static final double MOMENTUM_PULL_MAX_STRENGTH_SCALE = 0.25;

    private static final int MAELSTROM_PULL_DELAY_TICKS = 20;
    private static final float MAELSTROM_PULL_RADIUS = 3f;
    private static final float MAELSTROM_PULL_STRENGTH = 0.8f;
    private static final float MAELSTROM_PARTICLE_RADIUS = 3f;

    private static final float RESONANCE_RADIUS_BASE = 1.5f;
    private static final float RESONANCE_RADIUS_SCALE = 0.5f;
    private static final float RESONANCE_STRENGTH_SCALE = 0.3f;

    private static final int THROW_COOLDOWN_TICKS = 60;
    private static final int CATCH_COOLDOWN_REDUCTION_TICKS = 40;
    // endregion

    private int maelstromTicks;
    private int maelstromPulls;

    public AnchorProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    // region Data and NBT
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_RETURNING, false);
        builder.add(MAELSTROM_LEVEL, 0);
        builder.add(MOMENTUM_LEVEL, 0);
        builder.add(RESONANCE_LEVEL, 0);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("MaelstromTicks", this.maelstromTicks);
        nbt.putInt("MaelstromPulls", this.maelstromPulls);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.maelstromTicks = nbt.getInt("MaelstromTicks");
        this.maelstromPulls = nbt.getInt("MaelstromPulls");
    }

    public void setMaelstromLevel(int level) { this.getDataTracker().set(MAELSTROM_LEVEL, level); }
    public int getMaelstromLevel() { return this.getDataTracker().get(MAELSTROM_LEVEL); }
    public void setMomentumLevel(int level) { this.getDataTracker().set(MOMENTUM_LEVEL, level); }
    public int getMomentumLevel() { return this.getDataTracker().get(MOMENTUM_LEVEL); }
    public void setResonanceLevel(int level) { this.getDataTracker().set(RESONANCE_LEVEL, level); }
    public int getResonanceLevel() { return this.getDataTracker().get(RESONANCE_LEVEL); }

    public boolean isReturning() { return this.getDataTracker().get(IS_RETURNING); }
    public void setReturning(boolean returning) { this.getDataTracker().set(IS_RETURNING, returning); }
    // endregion

    // region Core Logic
    @Override
    public void tick() {
        Entity owner = this.getOwner();
        if (owner == null || !owner.isAlive()) {
            this.discard();
            return;
        }

        if (this.isReturning()) {
            tickReturnMovement(owner);
        } else {
            tickProjectileFlight(owner);
        }

        super.tick();
    }

    private void tickProjectileFlight(Entity owner) {
        if (this.inGround) {
            tickInGround();
        } else {
            tickInAir(owner);
        }
    }

    private void tickInAir(Entity owner) {
        float maxDistance = BASE_MAX_DISTANCE_FROM_OWNER + (this.getMomentumLevel() * MOMENTUM_DISTANCE_SCALE);
        if (this.distanceTo(owner) > maxDistance || this.age > MAX_FLIGHT_TICKS) {
            startReturning();
            return;
        }

        if (this.age % 7 == 0) {
            playTravelSounds();
        }
    }

    private void tickInGround() {
        if (getMaelstromLevel() > 0 && this.maelstromPulls < getMaelstromLevel()) {
            tickMaelstromEffect();
        }
    }

    private void tickReturnMovement(Entity owner) {
        if (this.distanceTo(owner) < PICKUP_DISTANCE) {
            onPickup(owner);
            return;
        }
        double returnSpeed = BASE_RETURN_SPEED + (this.getMomentumLevel() * MOMENTUM_RETURN_SPEED_SCALE);
        Vec3d directionToOwner = owner.getBoundingBox().getCenter().subtract(this.getPos()).normalize();
        this.setVelocity(directionToOwner.multiply(returnSpeed));
    }
    // endregion

    // region Hit Handling
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();
        Entity owner = this.getOwner();

        if (owner != null && hitEntity.getUuid().equals(owner.getUuid())) {
            return;
        }

        playEntityHitSounds();
        applyDirectHitDamage(hitEntity);

        if (owner != null && this.getMaelstromLevel() == 0) {
            applyMomentumPull(hitEntity, owner);
        }

        triggerResonanceEffect(hitEntity);
        startReturning();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!isReturning()) {
            playImpactSounds();
            if (getMaelstromLevel() <= 0) {
                triggerResonanceEffect(null);
                startReturning();
            }
        }
    }

    private void applyDirectHitDamage(Entity hitEntity) {
        Entity owner = this.getOwner();
        float damage = BASE_DAMAGE + (this.getMomentumLevel() * MOMENTUM_DAMAGE_SCALE);
        DamageSource damageSource = ModDamageTypes.of(this.getWorld(), ModDamageTypes.ANCHOR_THROW, this, owner);
        hitEntity.damage(damageSource, damage);
        hitEntity.setVelocity(Vec3d.ZERO);
    }
    // endregion

    // region Effects
    private void applyMomentumPull(Entity hitEntity, Entity owner) {
        double maxEffectiveDistance = MOMENTUM_PULL_MAX_EFFECTIVE_DISTANCE + (this.getMomentumLevel() * MOMENTUM_PULL_DISTANCE_SCALE);
        double minStrength = MOMENTUM_PULL_MIN_STRENGTH + (this.getMomentumLevel() * MOMENTUM_PULL_MIN_STRENGTH_SCALE);
        double maxStrength = MOMENTUM_PULL_MAX_STRENGTH + (this.getMomentumLevel() * MOMENTUM_PULL_MAX_STRENGTH_SCALE);

        double distance = owner.distanceTo(hitEntity);
        double progress = MathHelper.clamp(distance / maxEffectiveDistance, 0.0, 1.0);
        double pullStrength = MathHelper.lerp(progress, minStrength, maxStrength);

        Vec3d pullDirection = owner.getEyePos().subtract(hitEntity.getPos()).normalize();
        hitEntity.addVelocity(pullDirection.multiply(pullStrength).add(0, 0.1, 0));
    }

    private void tickMaelstromEffect() {
        this.maelstromTicks++;
        spawnMaelstromParticles();

        if (this.maelstromTicks > 0 && this.maelstromTicks % MAELSTROM_PULL_DELAY_TICKS == 0) {
            performMaelstromPull();
            this.maelstromPulls++;
        }

        if (this.maelstromPulls >= getMaelstromLevel()) {
            startReturning();
        }
    }

    private void performMaelstromPull() {
        if (getMaelstromLevel() <= 0) return;

        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED, this.getSoundCategory(), 0.3f, 0.3f);
        for (LivingEntity nearbyEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(MAELSTROM_PULL_RADIUS), e -> e.isAlive() && !this.isOwner(e))) {
            Vec3d pullDirection = this.getPos().subtract(nearbyEntity.getPos()).normalize();
            nearbyEntity.addVelocity(pullDirection.multiply(MAELSTROM_PULL_STRENGTH));
        }
    }

    private void spawnMaelstromParticles() {
        if (!this.getWorld().isClient || getMaelstromLevel() <= 0) return;

        int particleCount = (int)(MAELSTROM_PARTICLE_RADIUS * 8);
        for (int i = 0; i < particleCount; i++) {
            double angle = (double)i / particleCount * Math.PI * 2.0 + this.age * 0.2;
            double currentRadius = MAELSTROM_PARTICLE_RADIUS * (1.0 - ((double)(this.maelstromTicks % MAELSTROM_PULL_DELAY_TICKS) / MAELSTROM_PULL_DELAY_TICKS));

            double xOffset = Math.cos(angle) * currentRadius;
            double zOffset = Math.sin(angle) * currentRadius;
            double y = this.getY() + 0.1;

            this.getWorld().addParticle(ParticleTypes.SPLASH, this.getX() + xOffset, y, this.getZ() + zOffset, 0.0, 0.1, 0.0);
            if (this.random.nextFloat() < 0.5f) {
                this.getWorld().addParticle(ParticleTypes.BUBBLE_COLUMN_UP, this.getX() + xOffset, y, this.getZ() + zOffset, 0.0, 0.1, 0.0);
            }
        }
    }

    private void triggerResonanceEffect(Entity excludedEntity) {
        int resonanceLevel = this.getResonanceLevel();
        if (resonanceLevel <= 0) return;

        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON, this.getSoundCategory(), 0.6f, 1.8f);
        float pushRadius = RESONANCE_RADIUS_BASE + resonanceLevel * RESONANCE_RADIUS_SCALE;
        float pushStrength = resonanceLevel * RESONANCE_STRENGTH_SCALE;
        for (LivingEntity nearbyEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(pushRadius), entity -> entity.isAlive() && entity != excludedEntity && !isOwner(entity))) {
            Vec3d pushDirection = nearbyEntity.getBoundingBox().getCenter().subtract(this.getPos()).normalize();
            nearbyEntity.addVelocity(pushDirection.multiply(pushStrength));
            nearbyEntity.fallDistance = 0.0f;
        }
    }
    // endregion

    // region Utility Methods
    private void startReturning() {
        this.setReturning(true);
        this.setNoClip(true);
        this.inGround = false;
    }

    private void onPickup(Entity owner) {
        playPickupSounds();
        if(owner instanceof PlayerEntity player) {
            reduceCooldownOnCatch(player);
        }
        this.discard();
    }

    private void reduceCooldownOnCatch(PlayerEntity player) {
        float progress = player.getItemCooldownManager().getCooldownProgress(ModItems.ANCHOR, 0f);
        if (progress > 0) {
            int remainingTicks = (int) (progress * THROW_COOLDOWN_TICKS);
            int newCooldown = Math.max(0, remainingTicks - CATCH_COOLDOWN_REDUCTION_TICKS);
            player.getItemCooldownManager().set(ModItems.ANCHOR, newCooldown);
        }
    }

    private void playTravelSounds() {
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_VEX_CHARGE, this.getSoundCategory(), 0.2f, 0.6f);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_BEACON_AMBIENT, this.getSoundCategory(), 0.15f, 0.7f);
    }

    private void playImpactSounds() {
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_ANVIL_LAND, this.getSoundCategory(), 0.2f, 0.5f);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_SHIELD_BLOCK, this.getSoundCategory(), 0.7f, 0.7f);
    }

    private void playEntityHitSounds() {
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_TRIDENT_HIT, this.getSoundCategory(), 1.0f, 0.9f);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0f, 0.8f);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, this.getSoundCategory(), 0.3f, 1.4f);
    }

    private void playPickupSounds() {
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_TRIDENT_RETURN, this.getSoundCategory(), 0.8f, 1.1f);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, this.getSoundCategory(), 0.7f, 1.2f);
    }
    // endregion

    // region Overrides
    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.ENTITY_ARROW_HIT;
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        return false;
    }

    @Override
    protected boolean canHit(Entity entity) {
        if (this.getMaelstromLevel() > 0) {
            return false;
        }
        return super.canHit(entity);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return DEFAULT_STACK.copy();
    }

    @Override
    protected float getDragInWater() {
        return WATER_DRAG;
    }

    @Override
    protected double getGravity() {
        return GRAVITY;
    }
    // endregion
}