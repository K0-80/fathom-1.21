package com.k080.fathom.block.custom;

import com.k080.fathom.entity.ModBlockEntitys;
import com.k080.fathom.entity.custom.BloodCrucibleBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BloodCrucibleBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final IntProperty RITUAL_STAGE = IntProperty.of("ritual_stage", 0, 2);

    // Codec for block registration
    public static final MapCodec<BloodCrucibleBlock> CODEC = createCodec(BloodCrucibleBlock::new);

    public BloodCrucibleBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(RITUAL_STAGE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(RITUAL_STAGE);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BloodCrucibleBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // Necessary for block entities to render correctly
        return BlockRenderType.MODEL;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        // If the block is replaced with a different block (i.e., broken)
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BloodCrucibleBlockEntity) {
                // Scatter the inventory items into the world
                ItemScatterer.spawn(world, pos, (BloodCrucibleBlockEntity) blockEntity);
                // Update nearby comparators
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            return ItemActionResult.SUCCESS;
        }

        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof BloodCrucibleBlockEntity be) {
            BloodCrucibleBlockEntity.RitualState ritualState = be.getCurrentState();

            switch (ritualState) {
                case IDLE:
                    if (stack.isOf(Items.NETHER_STAR)) {
                        be.startRitual(player);
                        return ItemActionResult.CONSUME;
                    }
                    break;
                case AWAITING_MATERIALS:
                    if (stack.isOf(Items.NETHERITE_INGOT) || stack.isOf(Items.ENDER_EYE)) {
                        be.tryInsertMaterial(stack);
                        return ItemActionResult.CONSUME;
                    }
                    break;
                default:
                    // Do nothing for AWAITING_SACRIFICE or COMPLETE states
                    break;
            }
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        // Register our tick function from the BlockEntity
        return validateTicker(type, ModBlockEntitys.BLOOD_CRUCIBLE_BLOCK_ENTITY,
                (world1, pos, state1, be) -> BloodCrucibleBlockEntity.tick(world1, pos, state1, be));
    }
}