package com.k080.fathom.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Optional;

public class WindBladeItem extends SwordItem {
    public static final int CHARGE_TIME = 10;
    private static final double RANGE = 25.0;
    private static final int COOLDOWN = 20;
    private static final double RAY_TOLERANCE_FUZZINESS = 1.2;
    private static final int AIM_LOST_GRACE_PERIOD = 3;
    private int aimLostTicks = 0;

    public WindBladeItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (user.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.pass(itemStack);
        }

        if (raycastForEntity(user).isPresent()) {
            user.setCurrentHand(hand);
            this.aimLostTicks = 0;
            return TypedActionResult.consume(itemStack);
        }
        return TypedActionResult.fail(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;

        int timeUsed = this.getMaxUseTime(stack, user) - remainingUseTicks;
        Optional<EntityHitResult> hitResult = raycastForEntity(player);

        if (hitResult.isEmpty()) {
            aimLostTicks++;
            if (aimLostTicks >= AIM_LOST_GRACE_PERIOD) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM.value(), SoundCategory.PLAYERS, 0.4f, 0.7f);
                player.stopUsingItem();
                return;
            }
        } else {
            aimLostTicks = 0;
            if (timeUsed % 4 == 0 && !world.isClient && world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.END_ROD, hitResult.get().getPos().x, hitResult.get().getPos().y + 0.1, hitResult.get().getPos().z, 1, 0, 0, 0, 0.01);
            }
        }

        if (timeUsed < CHARGE_TIME) {
            if (timeUsed % 4 == 0) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.PLAYERS, 0.3f, 0.5f + (timeUsed * 0.02f));
            }
        } else {
            if (hitResult.isPresent()) {
                Entity target = hitResult.get().getEntity();
                Vec3d playerLookDir = player.getRotationVec(1.0f).normalize();
                Vec3d teleportPos = hitResult.get().getPos().subtract(playerLookDir.multiply(1.5));
                double yOffset = 0.2;

                // Capture player's position *before* the teleport request
                Vec3d preTeleportPos = player.getPos();

                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PUFFER_FISH_DEATH, SoundCategory.PLAYERS, 0.7f, 1.2f);
                world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ITEM_TRIDENT_RIPTIDE_1, SoundCategory.PLAYERS, 1.5f, 0.5f);
                world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ENTITY_BREEZE_WIND_BURST, SoundCategory.PLAYERS, 0.5f, 2.0f);
                world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 5.0f, 1.0f);

                if (!world.isClient) {
                    player.requestTeleport(teleportPos.getX(), teleportPos.getY() + yOffset, teleportPos.getZ());
                    player.getItemCooldownManager().set(this, COOLDOWN);
                    // Use the captured preTeleportPos
                    spawnDashParticles((ServerWorld) world, preTeleportPos, teleportPos);
                }
            } else {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
            player.stopUsingItem();
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int timeUsed = this.getMaxUseTime(stack, user) - remainingUseTicks;
        if (timeUsed < CHARGE_TIME) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 0.5f, 1.0f);
        }
        this.aimLostTicks = 0;
    }

    private void spawnDashParticles(ServerWorld world, Vec3d start, Vec3d end) {
        world.spawnParticles(ParticleTypes.CLOUD, start.x, start.y + 0.5, start.z, 15, 0.4, 0.4, 0.4, 0.05);
        world.spawnParticles(ParticleTypes.GUST, start.x, start.y + 0.5, start.z, 1, 0,0,0, 0);

            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, end.x, end.y, end.z, 40, 1.5, 1.5, 1.5, 0);
        world.spawnParticles(ParticleTypes.END_ROD, end.x, end.y, end.z, 20, 1.5, 1.5, 1.5, 0);
        world.spawnParticles(ParticleTypes.WHITE_SMOKE, end.x, end.y, end.z, 15, 1, 1, 1, 0);
    }

    private Optional<EntityHitResult> raycastForEntity(PlayerEntity player) {
        Vec3d startPos = player.getEyePos();
        Vec3d rotation = player.getRotationVec(1.0f);
        Vec3d endPos = startPos.add(rotation.multiply(RANGE));
        Box searchBox = player.getBoundingBox().expand(RANGE);

        Entity targetEntity = null;
        Vec3d targetHitPos = null;
        double minDistanceSqToRay = RAY_TOLERANCE_FUZZINESS * RAY_TOLERANCE_FUZZINESS;

        for (Entity entity : player.getWorld().getOtherEntities(player, searchBox)) {
            if (!(entity instanceof LivingEntity) || entity.isSpectator() || !entity.isAlive() || entity.isPlayer()) {
                continue;
            }

            Vec3d entityCenter = entity.getBoundingBox().getCenter();
            Vec3d closestPointOnRay = getClosestPointOnSegment(startPos, endPos, entityCenter);

            double distSqToRay = closestPointOnRay.squaredDistanceTo(entityCenter);

            if (distSqToRay < minDistanceSqToRay && player.squaredDistanceTo(entity) <= RANGE * RANGE) {
                HitResult blockHit = player.getWorld().raycast(new RaycastContext(
                        startPos,
                        entityCenter,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        player
                ));

                if (blockHit.getType() == HitResult.Type.MISS || blockHit.getPos().squaredDistanceTo(startPos) > entityCenter.squaredDistanceTo(startPos)) {
                    targetEntity = entity;
                    targetHitPos = closestPointOnRay;
                    minDistanceSqToRay = distSqToRay;
                }
            }
        }

        if (targetEntity != null) {
            return Optional.of(new EntityHitResult(targetEntity, targetHitPos));
        }
        return Optional.empty();
    }

    private Vec3d getClosestPointOnSegment(Vec3d A, Vec3d B, Vec3d P) {
        Vec3d AB = B.subtract(A);
        Vec3d AP = P.subtract(A);

        double magnitudeAB = AB.lengthSquared();
        if (magnitudeAB < 1.0E-3) {
            return A;
        }

        double t = AP.dotProduct(AB) / magnitudeAB;
        t = Math.max(0, Math.min(1, t));

        return A.add(AB.multiply(t));
    }
}