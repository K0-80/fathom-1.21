package com.k080.fathom.block.custom;

import com.k080.fathom.item.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

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
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient() && entity instanceof LivingEntity livingEntity) {
            if (!livingEntity.getOffHandStack().isOf(ModItems.CREAKING_STAFF)) {
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 1, false, false, true));
            }
        }
        super.onEntityCollision(state, world, pos, entity);
    }
}