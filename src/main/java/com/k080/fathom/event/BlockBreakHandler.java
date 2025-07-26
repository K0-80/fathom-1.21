package com.k080.fathom.event;

import com.k080.fathom.entity.block.AmethystResonatorBlockEntity;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBreakHandler {
    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register(BlockBreakHandler::onBlockBreak);
    }

    private static boolean onBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (world.isClient() || !state.isOf(Blocks.BUDDING_AMETHYST)) {
            return true;
        }

        int searchRadius = 10;
        for (BlockPos bePos : BlockPos.iterate(pos.add(-searchRadius, -searchRadius, -searchRadius), pos.add(searchRadius, searchRadius, searchRadius))) {
            BlockEntity be = world.getBlockEntity(bePos);
            if (be instanceof AmethystResonatorBlockEntity resonator) {
                if (resonator.isEventActive()) {
                    resonator.onBuddingAmethystBroken(pos);
                    break;
                }
            }
        }
        return true;
    }
}