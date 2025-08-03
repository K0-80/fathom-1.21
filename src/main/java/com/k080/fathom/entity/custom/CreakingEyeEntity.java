package com.k080.fathom.entity.custom;

import com.k080.fathom.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.block.GlowLichenBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class CreakingEyeEntity extends PathAwareEntity {

    private int despawnTimer = 400;
    private final List<BlockPos> placedLichenPositions = new ArrayList<>();
    private int lichenSpreadTimer = 0;
    private int lichenSpreadRadius = 0;
    private static final int MAX_LICHEN_RADIUS = 10;
    private boolean isDespawning = false;
    private int lichenRemovalIndex = 0;
    private int blocksToRemovePerTick = 1;

    private final List<PlayerEntity> trappedPlayers = new ArrayList<>();
    private static final double PULL_RADIUS = 10.0;
    private static final double PULL_STRENGTH = 0.08;
    private static final double UPWARD_BOOST_STRENGTH = 0.04;


    public CreakingEyeEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new CreakingEyeWatcherGoal(this)); // Our new goal
    }

    public static DefaultAttributeContainer.Builder createCreakingEyeAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
    }

    static class CreakingEyeWatcherGoal extends Goal {
        private final CreakingEyeEntity eye;
        private LivingEntity targetEntity;
        private Vec3d lookAtPos;

        private int lookTime;
        private int sequenceStep; // 0, 1, 2 = random look. 3 = player look.

        private final double watchDistance = 16.0;

        public CreakingEyeWatcherGoal(CreakingEyeEntity eye) {
            this.eye = eye;
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return true;
        }

        @Override
        public boolean shouldContinue() {
            return true;
        }

        @Override
        public void start() {
            this.sequenceStep = 0;
            this.setupNextAction();
        }

        private void setupNextAction() {
            // Steps 0, 1, 2: Look at a random position for 1 second.
            if (this.sequenceStep < 3) {
                this.targetEntity = null;
                this.lookAtPos = this.getRandomLookPos();
                this.lookTime = 20;
            }
            // Step 3: Look at a player for 2 seconds.
            else {
                this.lookAtPos = null;
                List<PlayerEntity> players = this.eye.getWorld().getEntitiesByClass(PlayerEntity.class, this.eye.getBoundingBox().expand(this.watchDistance), LivingEntity::isAlive);
                players.removeIf(player -> player.distanceTo(this.eye) > this.watchDistance || !player.isAlive());

                if (!players.isEmpty()) {
                    this.targetEntity = players.get(this.eye.getRandom().nextInt(players.size()));
                    this.lookTime = 40;
                } else {
                    this.targetEntity = null;
                    this.lookAtPos = this.getRandomLookPos();
                    this.lookTime = 40;
                }
            }
        }

        private Vec3d getRandomLookPos() {
            Random random = this.eye.getRandom();
            double lookRange = 16.0;
            return this.eye.getPos().add(
                    (random.nextDouble() - 0.5) * lookRange,
                    (random.nextDouble() - 0.5) * lookRange,
                    (random.nextDouble() - 0.5) * lookRange
            );
        }

        @Override
        public void tick() {
            if (this.targetEntity != null) {
                this.eye.getLookControl().lookAt(this.targetEntity.getX(), this.targetEntity.getEyeY(), this.targetEntity.getZ());
            } else if (this.lookAtPos != null) {
                this.eye.getLookControl().lookAt(this.lookAtPos.getX(), this.lookAtPos.getY(), this.lookAtPos.getZ());
            }

            this.lookTime--;
            if (this.lookTime <= 0) {
                this.sequenceStep++;
                if (this.sequenceStep > 3) {
                    this.sequenceStep = 0;
                }
                this.setupNextAction();
            }
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean wasDamaged = super.damage(source, amount);
        if (wasDamaged && !this.getWorld().isClient()) {

            ((ServerWorld) this.getWorld()).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, ModBlocks.CREAKING_VINE.getDefaultState()),
                    this.getX(), this.getY() + this.getHeight() / 2.0, this.getZ(),
                    15, this.getWidth() / 2.0, this.getHeight() / 2.0, this.getWidth() / 2.0, 0.05);
        }
        return wasDamaged;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (!this.getWorld().isClient() && !this.isDespawning) {
            if (this.getWorld() instanceof ServerWorld serverWorld) {

                serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, ModBlocks.CREAKING_VINE.getDefaultState()),
                        this.getX(), this.getY() + this.getHeight() / 2.0, this.getZ(),
                        30, this.getWidth(), this.getHeight() / 2.0, this.getWidth(), 0.1);
            }
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), this.getDeathSound(), this.getSoundCategory(), 1.0F, 1.0F);
            this.beginDespawning();
        }
        // no super so it can clean itself up
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            return;
        }
        if (!this.isDespawning) {

            // PULL START
            List<PlayerEntity> playersInRadius = this.getWorld().getEntitiesByClass(
                    PlayerEntity.class,
                    this.getBoundingBox().expand(PULL_RADIUS),
                    player -> !player.isCreative() && !player.isSpectator()
            );

            for (PlayerEntity player : playersInRadius) {
                if (!this.trappedPlayers.contains(player)) {
                    this.trappedPlayers.add(player);
                    Text message = Text.translatable("actionbar.fathom.creaking_eye.watched", player.getName()).formatted(Formatting.GRAY);
                    for (PlayerEntity trappedPlayer : this.trappedPlayers) {
                        trappedPlayer.sendMessage(message, true);
                    }
                }
            }

            this.trappedPlayers.removeIf(player -> {
                if (!player.isAlive() || player.isRemoved()) {
                    return true;
                }

                double distanceSq = player.squaredDistanceTo(this);
                if (distanceSq > PULL_RADIUS * PULL_RADIUS) {
                    Vec3d pullDirection = this.getPos().subtract(player.getPos()).normalize();
                    player.addVelocity(pullDirection.multiply(PULL_STRENGTH));

                    if (!player.isOnGround()) {
                        player.addVelocity(0, UPWARD_BOOST_STRENGTH, 0);
                    }
                    player.velocityModified = true;

                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 41, 0, false, false, true));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 41, 0, false, false, true));
                }
                return false;
            });
            //PULL END

            if (this.despawnTimer-- <= 0) {
                this.beginDespawning();
                return;
            }

            if (this.lichenSpreadRadius <= MAX_LICHEN_RADIUS) {
                if (this.lichenSpreadTimer-- <= 0) {
                    this.lichenSpreadTimer = 1;
                    this.spreadLichenInRing(this.lichenSpreadRadius);
                    this.lichenSpreadRadius++;
                }
            }
        } else {
            if (this.isRemoved()) {
                return;
            }
            if (this.placedLichenPositions.isEmpty()) {
                this.discard();
                return;
            }

            for (int i = 0; i < this.blocksToRemovePerTick; i++) {
                if (this.lichenRemovalIndex >= this.placedLichenPositions.size()) {
                    this.discard();
                    return; // All lichen has been removed
                }

                BlockPos pos = this.placedLichenPositions.get(this.lichenRemovalIndex);
                if (this.getWorld().getBlockState(pos).isOf(ModBlocks.CREAKING_VINE)) {
                    this.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                }
                this.lichenRemovalIndex++;
            }
        }
    }

    private void beginDespawning() {
        if (!this.isDespawning) {
            this.isDespawning = true;
            Collections.reverse(this.placedLichenPositions);
            this.lichenRemovalIndex = 0;
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_WARDEN_DIG, SoundCategory.HOSTILE, 1.0f, 1.0f);
            int totalBlocks = this.placedLichenPositions.size();
            int durationTicks = 10;

            if (totalBlocks > 0) {
                this.blocksToRemovePerTick = (int) Math.ceil((double) totalBlocks / durationTicks);
            }
        }
    }

    private void spreadLichenInRing(int radius) {
        BlockPos center = this.getBlockPos();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.round(Math.sqrt(x * x + y * y + z * z)) == radius) {
                        BlockPos currentPos = center.add(x, y, z);
                        if (this.getWorld().getBlockState(currentPos).isReplaceable()) {

                            BlockState lichenState = ModBlocks.CREAKING_VINE.getDefaultState();
                            boolean canPlace = false;

                            for (Direction direction : Direction.values()) {
                                if (canPlaceLichenOn(currentPos, direction)) {
                                    lichenState = lichenState.with(GlowLichenBlock.getProperty(direction), true);
                                    canPlace = true;
                                }
                            }

                            if (canPlace) {
                                this.getWorld().setBlockState(currentPos, lichenState, 3);
                                this.placedLichenPositions.add(currentPos);


                                this.getWorld().playSound(null, currentPos, SoundEvents.BLOCK_CAVE_VINES_PLACE, SoundCategory.BLOCKS, 0.5f, 0.8f + this.getRandom().nextFloat() * 0.4f);
                                if (this.getWorld() instanceof ServerWorld serverWorld) {
                                    serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, lichenState), currentPos.getX() + 0.5, currentPos.getY() + 0.5, currentPos.getZ() + 0.5, 5, // count
                                            0.4, 0.4, 0.4, 0.0
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean canPlaceLichenOn(BlockPos pos, Direction side) {
        BlockPos neighborPos = pos.offset(side);
        BlockState neighborState = this.getWorld().getBlockState(neighborPos);
        return neighborState.isSideSolidFullSquare(this.getWorld(), neighborPos, side.getOpposite());
    }

    @Override
    public boolean hasNoGravity () {
        return true;
    }

    @Override
    public boolean isPushable () {
        return false;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_CAVE_VINES_BREAK;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WARDEN_DEATH;
    }
}