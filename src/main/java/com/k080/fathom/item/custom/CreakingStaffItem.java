package com.k080.fathom.item.custom;

import com.k080.fathom.component.ModComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class CreakingStaffItem extends ToolItem {
    public CreakingStaffItem(ToolMaterial material, Settings settings) {
        super(material, settings
                .component(ModComponents.IS_CHARGED, false)
                .component(ModComponents.IS_WATCHED, false));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient() || !(entity instanceof PlayerEntity player)) {
            super.inventoryTick(stack, world, entity, slot, selected);
            return;
        }

        if (player.getOffHandStack() != stack) {
            if (stack.getOrDefault(ModComponents.IS_WATCHED, false)) {
                stack.set(ModComponents.IS_WATCHED, false);
            }
            super.inventoryTick(stack, world, entity, slot, selected);
            return;
        }

        boolean isBeingWatched = isPlayerBeingWatched(player, world);
        stack.set(ModComponents.IS_WATCHED, isBeingWatched);

        if (isBeingWatched) {
            if (player.hasStatusEffect(StatusEffects.SPEED)) {
                player.removeStatusEffect(StatusEffects.SPEED);
            }
            if (!player.hasStatusEffect(StatusEffects.SLOWNESS)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 0, true, false, true));
            }

            boolean isCharged = stack.getOrDefault(ModComponents.IS_CHARGED, false);
            if (!isCharged) {
                // 1 in 1000 chance per tick
                if (world.random.nextInt(1000) == 0) {
                    stack.set(ModComponents.IS_CHARGED, true);
                    // The animation will play once here, serving as a good visual cue.
                }
            }

        } else {
            if (player.hasStatusEffect(StatusEffects.SLOWNESS)) {
                player.removeStatusEffect(StatusEffects.SLOWNESS);
            }
            if (!player.hasStatusEffect(StatusEffects.SPEED)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, 0, true, false, true));
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private boolean isPlayerBeingWatched(PlayerEntity targetPlayer, World world) {
        return world.getPlayers().stream()
                .anyMatch(observerPlayer ->
                        observerPlayer != targetPlayer && !observerPlayer.isSpectator() && canSee(observerPlayer, targetPlayer)
                );
    }

    private boolean canSee(PlayerEntity observer, PlayerEntity target) {
        if (observer.squaredDistanceTo(target) > 4096.0D) { // 64 blocks max range
            return false;
        }

        Vec3d observerEyePos = observer.getEyePos();
        Vec3d targetCenter = target.getBoundingBox().getCenter();
        Vec3d lookVec = observer.getRotationVector();
        Vec3d toTargetVec = targetCenter.subtract(observerEyePos).normalize();

        // Check if target is within a ~120 degree field of view
        if (lookVec.dotProduct(toTargetVec) < 0.5D) {
            return false;
        }

        // Raycast to check for obstructions
        HitResult hitResult = observer.getWorld().raycast(new RaycastContext(
                observerEyePos,
                targetCenter,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                observer
        ));

        return hitResult.getType() != HitResult.Type.BLOCK;
    }
}