package com.k080.fathom.item.custom;

import com.k080.fathom.component.ModComponents;
import com.k080.fathom.entity.ModEntities;
import com.k080.fathom.entity.custom.CreakingVineSpreaderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
            if (!player.hasStatusEffect(StatusEffects.SLOWNESS)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 0, true, false, true));
            }

            boolean isCharged = stack.getOrDefault(ModComponents.IS_CHARGED, false);
            if (!isCharged) {      // 1 in 1000 chance per tick
                if (world.random.nextInt(1000) == 0) {
                    stack.set(ModComponents.IS_CHARGED, true);
                }
            }

        } else {
            if (!player.hasStatusEffect(StatusEffects.SPEED)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, 0, true, false, true));
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient()) {
            return TypedActionResult.pass(stack);
        }

        boolean isCharged = stack.getOrDefault(ModComponents.IS_CHARGED, false);

        if (isCharged) {
            stack.set(ModComponents.IS_CHARGED, false); // Consume charge

            BlockPos playerPos = user.getBlockPos();
            CreakingVineSpreaderEntity spreader = new CreakingVineSpreaderEntity(ModEntities.CREAKING_VINE_SPREADER, world, playerPos);
            world.spawnEntity(spreader);

            return TypedActionResult.success(stack);
        }

        return TypedActionResult.pass(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        boolean isCharged = stack.getOrDefault(ModComponents.IS_CHARGED, false);
        if (isCharged) {
            tooltip.add(Text.translatable("tooltip.fathom.creaking_staff.charged").formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.translatable("tooltip.fathom.creaking_staff.uncharged").formatted(Formatting.GRAY));
        }
    }

    private boolean isPlayerBeingWatched(PlayerEntity targetPlayer, World world) {
        return world.getPlayers().stream()
                .anyMatch(observerPlayer ->
                        observerPlayer != targetPlayer && !observerPlayer.isSpectator() && canSee(observerPlayer, targetPlayer)
                );
    }

    private boolean canSee(PlayerEntity observer, PlayerEntity target) {
        if (observer.squaredDistanceTo(target) > 1024.0D) { // 32 blocks max range
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