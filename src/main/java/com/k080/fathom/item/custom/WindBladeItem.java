package com.k080.fathom.item.custom;

import com.k080.fathom.effect.ModEffects;
import com.k080.fathom.particle.ModParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
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
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.Optional;

public class WindBladeItem extends SwordItem {
    public static final int CHARGE_TIME = 11;
    private static final double RANGE = 30.0;
    private static final int COOLDOWN = 20;
    private static final double RAY_TOLERANCE_FUZZINESS = 1.4;
    private static final int AIM_LOST_GRACE_PERIOD = 4;
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
        user.getStackInHand(hand).damage(4, user, LivingEntity.getSlotForHand(hand));
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
                        SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 0.5f, 0.5f);
                player.stopUsingItem();
                return;
            }
        } else {
            aimLostTicks = 0;
            if (hitResult.get().getEntity() instanceof LivingEntity target) {
                target.addStatusEffect(new StatusEffectInstance(ModEffects.WIND_GLOW, 2, 0, false, false));
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
                    spawnDashParticles((ServerWorld) world, preTeleportPos, teleportPos);
                }
            } else {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 0.5f, 0.5f);
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
        final double spiralRadius = 0.6;         // How wide the spiral is.
        final double totalRotations = 10;         // How many times the spiral twists around the path.
        final double particlesPerBlock = 5.0;    // Density of the particles.

        world.spawnParticles(ModParticles.WIND_PARTICLE,
                start.x, start.y, start.z, 25, 1.2, 1.2, 1.2, 0.15);

        Vec3d direction = end.subtract(start);
        double distance = direction.length();
        if (distance < 0.1) {
            return;
        }
        direction = direction.normalize();
        int particleCount = (int) (distance * particlesPerBlock);
        Vec3d perpendicular = direction.crossProduct(new Vec3d(0, 1, 0));
        if (perpendicular.lengthSquared() < 1.0E-6) {
            perpendicular = direction.crossProduct(new Vec3d(0, 0, 1));
        }
        perpendicular = perpendicular.normalize();
        Vec3d perpendicular2 = direction.crossProduct(perpendicular).normalize();

        for (int i = 0; i < particleCount; i++) {
            double t = (double) i / (particleCount - 1);
            double currentDistance = t * distance;
            double angle = t * totalRotations * 2 * Math.PI;
            Vec3d linePos = start.add(direction.multiply(currentDistance));
            Vec3d offset = perpendicular.multiply(Math.cos(angle) * spiralRadius)
                    .add(perpendicular2.multiply(Math.sin(angle) * spiralRadius));
            Vec3d particlePos = linePos.add(offset);
            world.spawnParticles(ModParticles.WIND_PARTICLE, particlePos.x, particlePos.y, particlePos.z, 1, 0, 0, 0, 0);
        }

        world.spawnParticles(ModParticles.WIND_PARTICLE,
                end.x, end.y, end.z, 25, 1.2, 1.2, 1.2, 0.15);
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