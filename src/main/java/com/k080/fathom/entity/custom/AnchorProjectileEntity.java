package com.k080.fathom.entity.custom;

import com.k080.fathom.Fathom;
import com.k080.fathom.damage.ModDamageTypes;
import com.k080.fathom.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AnchorProjectileEntity extends PersistentProjectileEntity {
    private static final TrackedData<Boolean> IS_RETURNING = DataTracker.registerData(AnchorProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final ItemStack DEFAULT_STACK = new ItemStack(ModItems.ANCHOR);
    private static final TrackedData<Integer> MAELSTROM_LEVEL = DataTracker.registerData(AnchorProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MOMENTUM_LEVEL = DataTracker.registerData(AnchorProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> RESONANCE_LEVEL = DataTracker.registerData(AnchorProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private int maelstromTicks;
    private int maelstromPulls;


    public AnchorProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

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
    public boolean isProjectileOwner(Entity entity) {
        return this.isOwner(entity);
    }

    private static final int MAELSTROM_PULL_DELAY = 20;

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
                if(owner instanceof PlayerEntity player) {
                    this.reduceCooldown(player);
                }
                return;
            }
            double returnSpeed = 2.0 + (this.getMomentumLevel() * 0.4f);
            Vec3d directionToOwner = owner.getBoundingBox().getCenter().subtract(this.getPos());
            this.setVelocity(directionToOwner.normalize().multiply(returnSpeed));
            super.tick();
            return;
        }

        if (this.inGround) {
            if (getMaelstromLevel() > 0) {

                if (this.maelstromPulls < getMaelstromLevel() && !isReturning()) {
                    this.maelstromTicks++;

                    if (this.maelstromTicks == 1) { // First tick in ground
                        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_ANVIL_LAND, this.getSoundCategory(), 0.2f, 0.5f);
                        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_SHIELD_BLOCK, this.getSoundCategory(), 0.7f, 0.7f);
                    }

                    spawnWhirlpoolEffect();

                    if (this.maelstromTicks > 0 && this.maelstromTicks % MAELSTROM_PULL_DELAY == 0) {
                        performMaelstromPull();
                        this.maelstromPulls++;
                    }

                    if (this.maelstromPulls >= getMaelstromLevel()) {
                        this.setReturning(true);
                        this.setNoClip(true);
                    }
                }
            } else if (!isReturning()) { // No maelstrom
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_ANVIL_LAND, this.getSoundCategory(), 0.2f, 0.5f);
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_SHIELD_BLOCK, this.getSoundCategory(), 0.7f, 0.7f);

                handleAreaOfEffect(null);

                this.setReturning(true);
                this.setNoClip(true);
            }
        }


        if (this.distanceTo(owner) > 15f + (3 * this.getMomentumLevel())) {
            this.setReturning(true);
            this.setNoClip(true);
        }
        if (this.age > 250) {
            this.setReturning(true);
            this.setNoClip(true);
        }

        if (!this.inGround && this.age % 7 == 0) {
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
        DamageSource damageSource = new DamageSource(this.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.ANCHOR_THROW), this, owner);
        hitEntity.damage(damageSource, 1F + ( 1.0f * this.getMomentumLevel()));
        hitEntity.setVelocity(Vec3d.ZERO);

//        if (owner != null) {
//            if (this.getMaelstromLevel() == 0) {
//                double maxEffectiveDistance = 10.0 + (this.getMomentumLevel() * 5);
//                double minStrength = 0.5 + (this.getMomentumLevel() * 0.05);
//                double maxStrength = 2.5 + (this.getMomentumLevel() * 0.25);
//
//                double distance = owner.distanceTo(hitEntity);
//                double progress = MathHelper.clamp(distance / maxEffectiveDistance, 0.0, 1.0);
//                double pullStrength = MathHelper.lerp(progress, minStrength, maxStrength);
//
//                Vec3d pullDirection = owner.getEyePos().subtract(hitEntity.getPos()).normalize();
//                Vec3d pullVelocity = pullDirection.multiply(pullStrength);
//
//                hitEntity.addVelocity(pullVelocity.x, pullVelocity.y + 0.1, pullVelocity.z);
//            }
//        }

        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_TRIDENT_HIT, this.getSoundCategory(), 1.0f, 0.9f);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0f, 0.8f);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, this.getSoundCategory(), 0.3f, 1.4f);

        handleAreaOfEffect(hitEntity);

        this.inGround = false;
        this.setNoClip(true);
        this.setReturning(true);
    }

    private void spawnWhirlpoolEffect() {
        if (!this.getWorld().isClient) return;
        if (this.getMaelstromLevel() <= 0) return;

        float radius = 3f;
        int particleCount = (int)(radius * 8);

        for (int i = 0; i < particleCount; i++) {
            double angle = (double)i / particleCount * Math.PI * 2.0 + this.age * 0.2;
            double currentRadius = radius * (1.0 - ((double)(this.maelstromTicks % MAELSTROM_PULL_DELAY) / MAELSTROM_PULL_DELAY));

            double xOffset = Math.cos(angle) * currentRadius;
            double zOffset = Math.sin(angle) * currentRadius;
            double y = this.getY() + 0.1;

            this.getWorld().addParticle(ParticleTypes.SPLASH, this.getX() + xOffset, y, this.getZ() + zOffset, 0.0, 0.1, 0.0);
            if (this.random.nextFloat() < 0.5f) {
                this.getWorld().addParticle(ParticleTypes.BUBBLE_COLUMN_UP, this.getX() + xOffset, y, this.getZ() + zOffset, 0.0, 0.1, 0.0);
            }
        }
    }

    private void performMaelstromPull() {
        if (this.getMaelstromLevel() <= 0) return;

        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED, this.getSoundCategory(), 0.3f, 0.3f);
            float pullRadius =3f;
            float pullStrength = 0.8f;
            for (LivingEntity nearbyEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(pullRadius), e -> e.isAlive() && !this.isOwner(e))) {
                Vec3d pullDirection = this.getPos().subtract(nearbyEntity.getPos()).normalize();
                nearbyEntity.addVelocity(pullDirection.multiply(pullStrength));
            }
    }

    private void handleAreaOfEffect(Entity excludedEntity) {
        int resonanceLevel = this.getResonanceLevel();
        if (resonanceLevel > 0) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON, this.getSoundCategory(), 0.6f, 1.8f);
            float pushRadius = 1.5f + resonanceLevel /2f;
            float pushStrength = 0.3f * resonanceLevel;
            for (LivingEntity nearbyEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(pushRadius), entity -> entity.isAlive() && entity != excludedEntity)) {Vec3d pushDirection = nearbyEntity.getBoundingBox().getCenter().subtract(this.getPos()).normalize();
                nearbyEntity.addVelocity(pushDirection.multiply(pushStrength));
                nearbyEntity.fallDistance = 0.0f;
            }
        }
    }

    public boolean isReturning() {
        return this.getDataTracker().get(IS_RETURNING);
    }

    public void setReturning(boolean returning) {
        this.getDataTracker().set(IS_RETURNING, returning);
    }

    private void reduceCooldown(PlayerEntity player) {
        final int TOTAL_COOLDOWN_TICKS = 60;
        final int REDUCTION_TICKS = 40;

        float progress = player.getItemCooldownManager().getCooldownProgress(ModItems.ANCHOR, 0f);
        if (progress > 0) {
            int remainingTicks = (int) (progress * TOTAL_COOLDOWN_TICKS);
            int newCooldown = Math.max(0, remainingTicks - REDUCTION_TICKS);
            player.getItemCooldownManager().set(ModItems.ANCHOR, newCooldown);
        }
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
        return 1.0F;
    }

    @Override
    protected double getGravity() {
        return 0.03F;
    }
}