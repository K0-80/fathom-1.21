package com.k080.fathom.mixin;

import com.k080.fathom.Fathom;
import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.util.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PillarBlock.class)
public abstract class PillarBlockMixin extends Block {
    public PillarBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);

        if (state.isIn(ModTags.Blocks.DRIFTWOOD_TRANSFORMABLE_LOGS) && isInWater(state, world, pos)) {

            ((ServerWorld) world).spawnParticles(
                    ParticleTypes.FALLING_WATER,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    30,
                    0.5, 0.5, 0.5,
                    0.0
            );

            world.scheduleBlockTick(pos, this, 600);
        }
    }


    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        if (state.isIn(ModTags.Blocks.DRIFTWOOD_TRANSFORMABLE_LOGS) && isInWater(state, world, pos)) {

            world.spawnParticles(
                    ParticleTypes.BUBBLE_POP,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    30,
                    0.5, 0.5, 0.5,
                    0.0
            );

            world.setBlockState(pos, ModBlocks.DRIFTWOOD_LOG.getDefaultState()
                    .with(PillarBlock.AXIS, state.get(PillarBlock.AXIS)));
        }
    }


    private static boolean isInWater(BlockState state, BlockView world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (world.getFluidState(pos.offset(direction)).isIn(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }
}

