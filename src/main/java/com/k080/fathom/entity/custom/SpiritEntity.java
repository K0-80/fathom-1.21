package com.k080.fathom.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpiritEntity extends PathAwareEntity {
    private static final TrackedData<Integer> TICKS_ALIVE = DataTracker.registerData(SpiritEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final int FAST_FLIGHT_DURATION = 120; // 6 seconds (6 * 20 ticks)
    private static final int LAUNCH_INTERVAL = 40; // 2 seconds (2 * 20 ticks)
    private static final double LAUNCH_SPEED = 1.2;
    private static final int LAUNCH_RANGE = 16;


    public SpiritEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 10, true);
        this.experiencePoints = 0;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(TICKS_ALIVE, 0);
    }

    public static DefaultAttributeContainer.Builder createSpiritAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1.0D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.8D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        super.initialize(world, difficulty, spawnReason, entityData);
        if (!world.isClient()) {
            launchToRandomLocation();
        }
        return entityData;
    }

    @Override
    public void checkDespawn() {
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            int ticks = this.dataTracker.get(TICKS_ALIVE);

            if (isMovingFast()) {
                this.dataTracker.set(TICKS_ALIVE, ticks + 1);

                if (ticks > 0 && (ticks % LAUNCH_INTERVAL == 0)) {
                    launchToRandomLocation();
                }
            } else {
                if (!this.getVelocity().equals(Vec3d.ZERO)) {
                    this.setVelocity(Vec3d.ZERO);
                }
            }
        }
    }

    private void launchToRandomLocation() {
        Vec3d currentPos = this.getPos();
        Vec3d randomOffset = new Vec3d(
                (this.random.nextDouble() * 2.0 - 1.0),
                (this.random.nextDouble() * 2.0 - 1.0),
                (this.random.nextDouble() * 2.0 - 1.0)
        ).normalize().multiply(this.random.nextInt(LAUNCH_RANGE / 2) + LAUNCH_RANGE / 2);

        Vec3d targetPos = currentPos.add(randomOffset);
        Vec3d direction = targetPos.subtract(currentPos).normalize();
        this.setVelocity(direction.multiply(LAUNCH_SPEED));
    }

    public boolean isMovingFast() {
        return this.dataTracker.get(TICKS_ALIVE) < FAST_FLIGHT_DURATION;
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world);
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(true);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (source.getAttacker() instanceof PlayerEntity) {
            if (!getWorld().isClient) {
                ((ServerWorld) this.getWorld()).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.LIGHT_BLUE_STAINED_GLASS.getDefaultState()),
                        this.getX(), this.getBodyY(0.5), this.getZ(), 30,
                        this.getWidth() / 2.0, this.getHeight() / 2.0, this.getWidth() / 2.0, 0.05);
            }
            this.playSound(SoundEvents.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
            this.discard();
            return true;
        }
        return super.damage(source, amount);
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ALLAY_AMBIENT_WITH_ITEM;
    }


}