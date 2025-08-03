package com.k080.fathom.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class CreakingVineBlock extends MultifaceGrowthBlock implements Waterloggable {


    public CreakingVineBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends MultifaceGrowthBlock> getCodec() {
        return null;
    }

    @Override
    public LichenGrower getGrower() {
        return null;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isClient() && world.isDay() && world.isSkyVisible(pos)) {
            if (random.nextInt(10) == 0) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
            }
        }
    }
}