package com.k080.fathom.block.custom;

import com.k080.fathom.entity.ModBlockEntitys;
import com.k080.fathom.entity.block.AmethystResonatorBlockEntity;
import com.k080.fathom.item.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AmethystResonatorBlock extends BlockWithEntity implements BlockEntityProvider {
    public AmethystResonatorBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AmethystResonatorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntitys.AMETHYST_RESONATOR_BLOCK_ENTITY, AmethystResonatorBlockEntity::tick);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof AmethystResonatorBlockEntity resonatorEntity) {
                Hand hand = player.getActiveHand();

                if (resonatorEntity.isCharged()) {
                    ItemStack stackInHand = player.getStackInHand(hand);
                    if (stackInHand.isOf(Items.NETHERITE_INGOT)) {
                        if (!player.getAbilities().creativeMode) {
                            stackInHand.decrement(1);
                        }

                        if (world instanceof ServerWorld serverWorld) {
                            double x = pos.getX() + 0.5;
                            double y = pos.getY() + 0.5;
                            double z = pos.getZ() + 0.5;

                            serverWorld.spawnParticles(ParticleTypes.REVERSE_PORTAL, x, y, z, 40, 0.6, 0.6, 0.6, 0.05);
                        }

                        world.playSound(null, pos, SoundEvents.BLOCK_LODESTONE_BREAK, SoundCategory.BLOCKS, 1.6f, 1.0f);
                        world.playSound(null, pos, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.BLOCKS, 0.3f, 2.0f);

                        world.breakBlock(pos, false);
                        ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, new ItemStack(ModItems.MIRAGE));

                        itemEntity.setNoGravity(true);
                        itemEntity.setNeverDespawn();
                        itemEntity.setPickupDelay(40);
                        itemEntity.setVelocity(0, 0, 0);

                        world.spawnEntity(itemEntity);
                        world.spawnEntity(itemEntity);
                        return ActionResult.SUCCESS;
                    }
                } else if (!resonatorEntity.isEventActive()) {
                    resonatorEntity.startEvent(player);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }
}