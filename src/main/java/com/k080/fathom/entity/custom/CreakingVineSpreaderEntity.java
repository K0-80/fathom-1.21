// src/main/java/com/k080/fathom/entity/custom/CreakingVineSpreaderEntity.java
package com.k080.fathom.entity.custom;

import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.block.custom.CreakingVineBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlowLichenBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class CreakingVineSpreaderEntity extends Entity {
    private static final int MAX_RADIUS = 12; // Max radius in blocks
    private static final int TICKS_PER_RADIUS_INCREMENT = 3; // How many ticks before radius increases by 1
    private static final int SPREAD_ATTEMPTS_PER_TICK = 50; // How many random positions to attempt placing vines per tick

    private int currentRadius = 0;
    private int ticksExisted = 0;
    private BlockPos origin;

    public CreakingVineSpreaderEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = true; // Does not collide with blocks/entities
        this.setNoGravity(true); // Does not fall
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    public CreakingVineSpreaderEntity(EntityType<?> type, World world, BlockPos origin) {
        this(type, world);
        this.origin = origin;
        this.setPosition(origin.getX() + 0.5, origin.getY() + 0.5, origin.getZ() + 0.5);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            return;
        }

        ticksExisted++;

        int newRadius = ticksExisted / TICKS_PER_RADIUS_INCREMENT;
        if (newRadius > currentRadius) {
            currentRadius = Math.min(newRadius, MAX_RADIUS);
        }

        if (currentRadius >= MAX_RADIUS && ticksExisted > MAX_RADIUS * TICKS_PER_RADIUS_INCREMENT + 20) { // Add a small delay after max radius is reached
            this.discard();
            return;
        }

        if (currentRadius == 0) return; // Don't do anything before radius 1

        spreadVinesInCurrentRadius();
    }

    private void spreadVinesInCurrentRadius() {
        World world = this.getWorld();
        BlockPos center = this.origin != null ? this.origin : this.getBlockPos();

        Set<BlockPos> potentialPositions = new HashSet<>();

        int minR = currentRadius > 1 ? currentRadius - 1 : 0; // Defines the inner ring for spread
        int maxR = currentRadius; // Defines the outer ring for spread

        for (int i = 0; i < SPREAD_ATTEMPTS_PER_TICK; i++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            double distance = world.random.nextDouble() * (maxR - minR) + minR;

            int dx = (int) Math.round(distance * Math.cos(angle));
            int dz = (int) Math.round(distance * Math.sin(angle));

            BlockPos basePos = center.add(dx, 0, dz);

            // Check multiple y-levels around the basePos for potential attachment points
            for (int dy = -MAX_RADIUS / 2; dy <= MAX_RADIUS / 2; dy++) {
                potentialPositions.add(basePos.up(dy));
            }
        }

        // Try placing vines at selected positions
        for (BlockPos pos : potentialPositions) {
            if (world.random.nextFloat() < 0.2f) { // Reduced chance to place at each candidate spot
                tryPlaceVine(world, pos);
            }
        }
    }

    private void tryPlaceVine(World world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (CreakingVineBlock.canGrowOn(world, pos, direction)) {
                Direction vineFace = direction.getOpposite();
                BlockState vineState = ModBlocks.CREAKING_VINE.getDefaultState().with(CreakingVineBlock.getFacingProperty(vineFace), true);

                BlockState currentBlockAtPos = world.getBlockState(pos);
                if (currentBlockAtPos.isAir() || currentBlockAtPos.isIn(BlockTags.REPLACEABLE)) {
                    if (vineState.canPlaceAt(world, pos)) {
                        world.setBlockState(pos, vineState, 3);
                        world.playSound(null, pos, SoundEvents.BLOCK_CAVE_VINES_PLACE, SoundCategory.BLOCKS, 1f, 0.9f + world.random.nextFloat() * 0.2f);
                        return;
                    }
                }
            }
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.currentRadius = nbt.getInt("CurrentRadius");
        this.ticksExisted = nbt.getInt("TicksExisted");
        if (nbt.contains("OriginX") && nbt.contains("OriginY") && nbt.contains("OriginZ")) {
            this.origin = new BlockPos(nbt.getInt("OriginX"), nbt.getInt("OriginY"), nbt.getInt("OriginZ"));
        } else {
            this.origin = this.getBlockPos(); // Fallback if origin not saved
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("CurrentRadius", this.currentRadius);
        nbt.putInt("TicksExisted", this.ticksExisted);
        if (this.origin != null) {
            nbt.putInt("OriginX", this.origin.getX());
            nbt.putInt("OriginY", this.origin.getY());
            nbt.putInt("OriginZ", this.origin.getZ());
        }
    }
}