package com.k080.fathom.util;

import com.k080.fathom.entity.client.ShockwaveBlockEntity;
import com.k080.fathom.networking.packet.ShockwaveS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class ShockwaveUtil {
    private static final float WAVE_SPEED = 2f;

    public static void triggerShockwave(ServerWorld world, BlockPos center, int radius) {
        ShockwaveS2CPacket packet = new ShockwaveS2CPacket(center, radius);

        PlayerLookup.tracking(world, center).forEach(player -> {
            ServerPlayNetworking.send(player, packet);
        });
    }


    public static void createShockwave(World world, BlockPos center, int radius) {
        if (!world.isClient) {
            return;
        }

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distance = Math.sqrt(x * x + z * z);
                if (distance > radius) {
                    continue;
                }

                BlockPos currentPos = center.add(x, 0, z);
                BlockPos surfacePos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, currentPos);
                BlockPos blockBelowPos = surfacePos.down();
                BlockState state = world.getBlockState(blockBelowPos);

                if (!state.isAir() && state.isOpaqueFullCube(world, blockBelowPos)) {
                    int delay = (int) (distance * WAVE_SPEED);

                    ShockwaveBlockEntity entity = ShockwaveBlockEntity.create(world, surfacePos, state, delay);

                    ((ClientWorld) world).addEntity(entity);
                }
            }
        }
    }
}