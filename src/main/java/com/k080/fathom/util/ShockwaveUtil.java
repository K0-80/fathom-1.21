package com.k080.fathom.util;

import com.k080.fathom.entity.block.ShockwaveBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class ShockwaveUtil {

    /**
     * Creates a shockwave effect by spawning multiple ShockwaveBlockEntity instances.
     * This method must be called on the server side.
     * @param world The world instance.
     * @param center The center position of the shockwave.
     * @param radius The radius of the shockwave in blocks.
     * @param speed A multiplier for the wave's travel speed. Lower is faster. (e.g., 2-4 is a good range).
     */
    public static void triggerShockwave(World world, BlockPos center, int radius, int speed) {
        // This check is crucial.
        // Entity spawning and major game logic should happen on the server.
        // The server will then synchronize the new entities to the client for rendering.
        // If we were to run this on the client, 'world' would be a ClientWorld,
        // and attempting server operations would lead to a ClassCastException.
        if (world.isClient()) {
            return;
        }

        int radiusSq = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radiusSq) {
                    continue; // Keep the shockwave circular
                }

                BlockPos currentPos = center.add(x, 0, z);

                // Find the highest solid block at this X,Z coordinate
                BlockPos groundPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, currentPos).down();
                BlockState groundState = world.getBlockState(groundPos);

                if (!groundState.isAir() && groundState.isSideSolid(world, groundPos, Direction.UP, SideShapeType.FULL)) {
                    double distance = Math.sqrt(x * x + z * z);
                    // The delay is based on the distance from the center, creating the wave effect.
                    int delay = (int) (distance * speed);

                    ShockwaveBlockEntity shockwave = ShockwaveBlockEntity.create(world, groundPos, groundState, delay);
                    world.spawnEntity(shockwave);
                }
            }
        }
    }
}