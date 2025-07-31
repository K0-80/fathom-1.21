package com.k080.fathom.item.custom;

import com.k080.fathom.effect.ModEffects;
import com.k080.fathom.enchantment.ModEnchantments;
import com.k080.fathom.particle.ModParticles;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
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
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WindBladeItem extends SwordItem {
    public static final int CHARGE_TIME = 30;
    private static final int COOLDOWN = 60;
    private static final double RAY_TOLERANCE_FUZZINESS = 1.5;
    private static final int AIM_LOST_GRACE_PERIOD = 5;
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
        return getChargeTime(user, stack) + 1;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (user.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.pass(itemStack);
        }

        if (raycastForEntity(user, itemStack).isPresent()) {
            user.setCurrentHand(hand);
            this.aimLostTicks = 0;
            return TypedActionResult.consume(itemStack);
        }
        return TypedActionResult.fail(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;

        int currentChargeTime = getChargeTime(user, stack);
        int timeUsed = this.getMaxUseTime(stack, user) - remainingUseTicks;
        Optional<EntityHitResult> hitResult = raycastForEntity(player, stack);

        if (hitResult.isEmpty()) {
            aimLostTicks++;
            if (aimLostTicks >= AIM_LOST_GRACE_PERIOD) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 0.5f, 1.2f);
                player.getItemCooldownManager().set(this, 10);
                player.stopUsingItem();
                return;
            }
        } else {
            aimLostTicks = 0;
            if (hitResult.get().getEntity() instanceof LivingEntity target) {
                target.addStatusEffect(new StatusEffectInstance(ModEffects.WIND_GLOW, 1, 0, false, false));
            }
        }

        if (timeUsed < currentChargeTime) {
            int soundPoint1 = 1;
            int soundPoint2 = currentChargeTime / 2;
            int soundPoint3 = currentChargeTime - 2;

            if (timeUsed == soundPoint1) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.PLAYERS, 0.4f, 0.5f);
            } else if (timeUsed == soundPoint2) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.PLAYERS, 0.4f, 1.0f);
            } else if (timeUsed == soundPoint3) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.PLAYERS, 0.4f, 1.5f);
            }
        } else {
            if (hitResult.isPresent()) {
                Entity target = hitResult.get().getEntity();
                Vec3d playerLookDir = player.getRotationVec(1.0f).normalize();
                Vec3d teleportPos = hitResult.get().getPos().subtract(playerLookDir.multiply(1.5));

                int galeForceLevel = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT)
                        .getEntry(ModEnchantments.GALE_FORCE)
                        .map(entry -> EnchantmentHelper.getLevel(entry, stack))
                        .orElse(0);
                double yOffset = target.isOnGround() ? 0.5 : 0.0;

                Vec3d preTeleportPos = player.getPos();

                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PUFFER_FISH_DEATH, SoundCategory.PLAYERS, 0.7f, 1.2f);
                world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ITEM_TRIDENT_RIPTIDE_1, SoundCategory.PLAYERS, 1.5f, 0.5f);
                world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ENTITY_BREEZE_WIND_BURST, SoundCategory.PLAYERS, 0.5f, 2.0f);
                world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 5.0f, 1.0f);

                if (!world.isClient) {
                    player.requestTeleport(teleportPos.getX(), teleportPos.getY() + yOffset, teleportPos.getZ());

                    player.getItemCooldownManager().set(this, COOLDOWN);
                    stack.damage(2, player, PlayerEntity.getSlotForHand(player.getActiveHand()));// only takes durabilty on use or on hit
                    spawnDashParticles((ServerWorld) world, preTeleportPos, teleportPos);

                    if (galeForceLevel > 0) {
                        applyGaleForce(world, player, galeForceLevel, target);
                    }
                }
            } else {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 0.5f, 1.2f);
                player.getItemCooldownManager().set(this, 10);
            }
            player.stopUsingItem();
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int timeUsed = this.getMaxUseTime(stack, user) - remainingUseTicks;
        if (timeUsed < getChargeTime(user, stack)) {
            if (user instanceof PlayerEntity player) {
                //player.getItemCooldownManager().set(this, COOLDOWN);
            }
            //world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 0.5f, 1.2f);
        }
        this.aimLostTicks = 0;
    }

    private int getChargeTime(@NotNull LivingEntity user, ItemStack stack) {
        int level = user.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT)
                .getEntry(ModEnchantments.ALACRITY)
                .map(entry -> EnchantmentHelper.getLevel(entry, stack))
                .orElse(0);
        return switch (level) {
            case 1 -> 24;
            case 2 -> 18;
            case 3 -> 10;
            default -> CHARGE_TIME;
        };
    }

    private double getGazeFuzziness(PlayerEntity player, ItemStack stack) {
        int level = player.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT)
                .getEntry(ModEnchantments.GAZE)
                .map(entry -> EnchantmentHelper.getLevel(entry, stack))
                .orElse(0);
        return RAY_TOLERANCE_FUZZINESS + (level * 0.5);
    }

    private void applyGaleForce(World world, PlayerEntity player, int level, Entity teleportTarget) {
        if (world.isClient) return;

        float radius = 1.0f + level;
        Vec3d playerPos = player.getPos();
        ((ServerWorld)world).spawnParticles(ParticleTypes.GUST, player.getX(), player.getY(), player.getZ(), 1, radius / 2, 0.5, radius / 2, 0.0);

        Box areaOfEffect = new Box(playerPos, playerPos).expand(radius);
        for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, areaOfEffect, e -> e != player && e != teleportTarget && e.isAlive())) {
            Vec3d knockbackDir = entity.getPos().subtract(playerPos).normalize();
            entity.addVelocity(knockbackDir.x * 1.2, 0.4, knockbackDir.z * 1.2);
        }
    }

    private double getTeleportRange(PlayerEntity player, ItemStack stack) {
        int level = player.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT)
                .getEntry(ModEnchantments.GALE_FORCE)
                .map(entry -> EnchantmentHelper.getLevel(entry, stack))
                .orElse(0);
        return 24.0 + (level * 4.0);
    }

    private void spawnDashParticles(ServerWorld world, Vec3d start, Vec3d end) {
        final double spiralRadius = 0.5;
        final double totalRotations = 10;
        final double particlesPerBlock = 3.0;

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

    private Optional<EntityHitResult> raycastForEntity(PlayerEntity player, ItemStack stack) {
        double currentRange = getTeleportRange(player, stack);
        Vec3d startPos = player.getEyePos();
        Vec3d rotation = player.getRotationVec(1.0f);
        Vec3d endPos = startPos.add(rotation.multiply(currentRange));
        Box searchBox = player.getBoundingBox().expand(currentRange);

        Entity targetEntity = null;
        Vec3d targetHitPos = null;
        double gazeFuzziness = getGazeFuzziness(player, stack);
        double minDistanceSqToRay = gazeFuzziness * gazeFuzziness;

        for (Entity entity : player.getWorld().getOtherEntities(player, searchBox)) {
            if (!(entity instanceof LivingEntity) || entity.isSpectator() || !entity.isAlive()) {
                continue;
            }

            Vec3d entityCenter = entity.getBoundingBox().getCenter();
            Vec3d closestPointOnRay = getClosestPointOnSegment(startPos, endPos, entityCenter);

            double distSqToRay = closestPointOnRay.squaredDistanceTo(entityCenter);

            if (distSqToRay < minDistanceSqToRay && player.squaredDistanceTo(entity) <= currentRange * currentRange) {
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