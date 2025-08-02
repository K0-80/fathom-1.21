package com.k080.fathom.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.LichenGrower;
import net.minecraft.block.MultifaceGrowthBlock;
import net.minecraft.block.Waterloggable;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Map;

public class CreakingVineBlock extends MultifaceGrowthBlock implements Waterloggable {

    @Override
    protected MapCodec<? extends MultifaceGrowthBlock> getCodec() {
        return null;
    }

    private static final Map<Direction, BooleanProperty> FACING_PROPERTIES =
            ImmutableMap.<Direction, BooleanProperty>builder()
                    .put(Direction.NORTH, Properties.NORTH)
                    .put(Direction.EAST, Properties.EAST)
                    .put(Direction.SOUTH, Properties.SOUTH)
                    .put(Direction.WEST, Properties.WEST)
                    .put(Direction.UP, Properties.UP)
                    .put(Direction.DOWN, Properties.DOWN)
                    .build();

    private static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

    public CreakingVineBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(Properties.NORTH, false)
                .with(Properties.EAST, false)
                .with(Properties.SOUTH, false)
                .with(Properties.WEST, false)
                .with(Properties.UP, false)
                .with(Properties.DOWN, false)
        );
    }

    public static BooleanProperty getFacingProperty(Direction direction) {
        return FACING_PROPERTIES.get(direction);
    }

    public static boolean canGrowOn(World world, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.offset(direction.getOpposite());
        BlockState neighborState = world.getBlockState(neighborPos);
        return neighborState.isSideSolidFullSquare(world, neighborPos, direction);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape voxelShape = VoxelShapes.empty();
        if (state.get(Properties.UP)) {
            voxelShape = VoxelShapes.combine(voxelShape, UP_SHAPE, BooleanBiFunction.OR);
        }
        if (state.get(Properties.DOWN)) {
            voxelShape = VoxelShapes.combine(voxelShape, DOWN_SHAPE, BooleanBiFunction.OR);
        }
        if (state.get(Properties.EAST)) {
            voxelShape = VoxelShapes.combine(voxelShape, EAST_SHAPE, BooleanBiFunction.OR);
        }
        if (state.get(Properties.WEST)) {
            voxelShape = VoxelShapes.combine(voxelShape, WEST_SHAPE, BooleanBiFunction.OR);
        }
        if (state.get(Properties.SOUTH)) {
            voxelShape = VoxelShapes.combine(voxelShape, SOUTH_SHAPE, BooleanBiFunction.OR);
        }
        if (state.get(Properties.NORTH)) {
            voxelShape = VoxelShapes.combine(voxelShape, NORTH_SHAPE, BooleanBiFunction.OR);
        }
        return voxelShape.isEmpty() ? VoxelShapes.fullCube() : voxelShape;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.NORTH, Properties.EAST, Properties.SOUTH, Properties.WEST, Properties.UP, Properties.DOWN);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.get(getFacingProperty(direction))) {
            return state;
        }
        if (!neighborState.isSideSolidFullSquare(world, neighborPos, direction.getOpposite())) {
            return state.with(getFacingProperty(direction), false);
        }
        return state;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // MultifaceBlock's getPlacementState handles detecting available faces
        return super.getPlacementState(ctx);
    }

    @Override
    public LichenGrower getGrower() {
        return null;
    }

}