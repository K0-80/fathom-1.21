package com.k080.fathom.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.Bucketable;
import net.minecraft.item.Items;
import com.k080.fathom.item.ModItems;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FishConversionUtil {
    private static final Map<Item, EntityType<?>> FISH_CONVERSION_MAP = new HashMap<>();

    public static void register(Item fishItem, EntityType<?> fishEntity) {
        FISH_CONVERSION_MAP.put(fishItem, fishEntity);
    }


    public static boolean tryConvert(ItemStack stack, FishingBobberEntity bobber) {
        if (!FISH_CONVERSION_MAP.containsKey(stack.getItem())) {
            return false;
        }

        World world = bobber.getWorld();
        PlayerEntity player = bobber.getPlayerOwner();
        if (player == null) {
            return false;
        }

        EntityType<?> entityType = FISH_CONVERSION_MAP.get(stack.getItem());
        Entity fishEntity = entityType.create(world);
        if (fishEntity == null) {
            return false;
        }

        ItemStack offhandStack = player.getOffHandStack();
        if (offhandStack.isOf(Items.BUCKET) && fishEntity instanceof Bucketable bucketableEntity) {
            ItemStack fishBucketStack = bucketableEntity.getBucketItem();
            offhandStack.decrement(1);
            world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_BUCKET_FILL_FISH, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            if (player.getMainHandStack().isEmpty()) {
                player.setStackInHand(player.getActiveHand(), fishBucketStack);
            } else {
                player.getInventory().offerOrDrop(fishBucketStack);
            }
            return true;

        } else {
            fishEntity.setPosition(bobber.getPos());

            double d = player.getX() - bobber.getX();
            double e = player.getEyeY() - bobber.getY();
            double f = player.getZ() - bobber.getZ();
            double upwardForce = Math.sqrt(d * d + f * f) * 0.08; // Slightly increased force

            Vec3d velocity = new Vec3d(d * 0.1, e * 0.1 + upwardForce, f * 0.1);
            fishEntity.setVelocity(velocity);

            world.spawnEntity(fishEntity);
            return true;
        }

    }
}
