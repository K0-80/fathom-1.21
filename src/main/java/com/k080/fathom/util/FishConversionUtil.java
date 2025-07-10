package com.k080.fathom.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FishConversionUtil {
    private static final Map<Item, EntityType<?>> FISH_CONVERSION_MAP = new HashMap<>();

    public static void register(Item fishItem, EntityType<?> fishEntity) {
        FISH_CONVERSION_MAP.put(fishItem, fishEntity);
    }

    public static Optional<Entity> tryConvert(ItemStack stack, FishingBobberEntity bobber) {
        if (!FISH_CONVERSION_MAP.containsKey(stack.getItem())) {
            return Optional.empty();
        }

        World world = bobber.getWorld();
        PlayerEntity player = bobber.getPlayerOwner();
        if (player == null) {
            return Optional.empty();
        }

        EntityType<?> entityType = FISH_CONVERSION_MAP.get(stack.getItem());
        Entity fishEntity = entityType.create(world);

        if (fishEntity != null) {

            fishEntity.setPosition(bobber.getPos());

            double d = player.getX() - bobber.getX();
            double e = player.getEyeY() - bobber.getY();
            double f = player.getZ() - bobber.getZ();

            double upwardForce = Math.sqrt(d * d + e * e + f * f) * 0.05;

            Vec3d velocity = new Vec3d(d * 0.1, e * 0.1 + upwardForce, f * 0.1);
            fishEntity.setVelocity(velocity);

            world.spawnEntity(fishEntity);
            return Optional.of(fishEntity);
        }

        return Optional.empty();
    }
}
