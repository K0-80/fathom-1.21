package com.k080.fathom.item.custom;

import com.k080.fathom.component.ModComponents;
import com.k080.fathom.effect.ModEffects;
import com.k080.fathom.entity.ModEntities;
import com.k080.fathom.entity.custom.AmethystShardProjectileEntity;
import com.k080.fathom.entity.custom.MirageModelEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class Mirageitem extends SwordItem {
    public Mirageitem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings.component(ModComponents.SHARDS, 0));
    }


    public boolean onDamaged(ItemStack stack, LivingEntity wielder) {
        int currentShards = stack.getOrDefault(ModComponents.SHARDS, 0);
        if (currentShards > 0) {
            stack.set(ModComponents.SHARDS, currentShards - 1);
            wielder.getWorld().playSound(null, wielder.getX(), wielder.getY(), wielder.getZ(), SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.PLAYERS, 1.5f, 1f);
            return true;
        }
        return false;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.postHit(stack, target, attacker);

        int shardCount = stack.getOrDefault(ModComponents.SHARDS, 0);
        if (shardCount > 0) {
            target.timeUntilRegen = Math.max(0, 20 - shardCount);
        }

        return result;
    }

    public void onChargedHit(ItemStack stack, PlayerEntity attacker) {
        int currentShards = stack.getOrDefault(ModComponents.SHARDS, 0);
        if (currentShards < 5) {
            stack.set(ModComponents.SHARDS, Math.min(5, currentShards + 1));
            attacker.getWorld().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                    SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.PLAYERS, 1.5f, 1.5f);
        }
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        int shards = stack.getOrDefault(ModComponents.SHARDS, 0);
        tooltip.add(Text.translatable("item.fathom.mirageitem.tooltip.shards", shards).formatted(Formatting.GRAY));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient()) {
            ServerWorld serverWorld = (ServerWorld) world;

            Vec3d originalPos = user.getPos();
            float originalYaw = user.getYaw();
            boolean isShifting = user.isSneaking();
            int shardCount = stack.getOrDefault(ModComponents.SHARDS, 0);

            Vec3d targetPos = teleportPlayer(serverWorld, user, isShifting);

            if (isShifting && shardCount > 0) {
                spawnShardVolley(user, world, shardCount);
                stack.set(ModComponents.SHARDS, 0);
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_EVOKER_CAST_SPELL, SoundCategory.PLAYERS, 1.0f, 1.2f);
            } else if (!isShifting && shardCount > 0) {
                Box areaOfEffect = new Box(BlockPos.ofFloored(targetPos)).expand(3.0);
                List<LivingEntity> entitiesInRadius = world.getEntitiesByClass(LivingEntity.class, areaOfEffect, (entity) -> entity != user && entity.isAlive());

                int duration = 10 * shardCount;
                int amplifier = shardCount - 1;

                for (LivingEntity entity : entitiesInRadius) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, duration, amplifier, false, true, true));
                    entity.addStatusEffect(new StatusEffectInstance(ModEffects.NO_GRAVITY, duration, amplifier, false, true, true  ));
                    spawnDustParticlesOnEntity(serverWorld, entity);
                }

                Vector3f[] colors = {
                        new Vector3f(0.6f, 0.4f, 0.8f),
                        new Vector3f(0.7f, 0.5f, 0.9f),
                        new Vector3f(0.85f, 0.7f, 1.0f)
                };
                double radius = 3.0;

                spawnHexagonParticleRing(serverWorld, new Vec3d(targetPos.x, targetPos.y + 0.1, targetPos.z), radius, colors);
                spawnHexagonParticleRing(serverWorld, new Vec3d(targetPos.x, targetPos.y + user.getHeight() / 2.0, targetPos.z), radius, colors);
                spawnHexagonParticleRing(serverWorld, new Vec3d(targetPos.x, targetPos.y + user.getStandingEyeHeight(), targetPos.z), radius, colors);

                world.playSound(null, targetPos.getX(), targetPos.getY(), targetPos.getZ(), SoundEvents.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, SoundCategory.PLAYERS, 1.0f, 1.0f);
                stack.set(ModComponents.SHARDS, 0);
            }

            MirageModelEntity mirage = new MirageModelEntity(ModEntities.MIRAGE_MODEL, world);
            mirage.setOwnerUuid(user.getUuid());
            mirage.setModelType(isShifting ? 1 : 0);
            mirage.setTargetPosition(targetPos);
            mirage.refreshPositionAndAngles(originalPos.getX(), originalPos.getY(), originalPos.getZ(), originalYaw, 0.0f);
            world.spawnEntity(mirage);

            world.playSound(null, originalPos.getX(), originalPos.getY(), originalPos.getZ(), SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.PLAYERS, 1.0F, 1.0F);

            Vec3d playerPos = user.getPos();
            BlockHitResult hitResult = world.raycast(new RaycastContext(playerPos, playerPos.subtract(0, 0.3, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, user));

            boolean applyNoGravity = true;
            if (hitResult.getType() == BlockHitResult.Type.BLOCK) {
                double distanceToGround = playerPos.getY() - hitResult.getPos().getY();
                if (distanceToGround < 0.4) {
                    applyNoGravity = false;
                }
            }

            if (applyNoGravity) {
                user.addStatusEffect(new StatusEffectInstance(ModEffects.NO_GRAVITY, 17, 0, false, false, true));
            }

            stack.damage(2, user, LivingEntity.getSlotForHand(hand));
            user.getItemCooldownManager().set(this, 2 * 20);
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(stack);
    }

    private void spawnDustParticlesOnEntity(ServerWorld world, LivingEntity entity) {
        Vector3f[] colors = {
                new Vector3f(0.6f, 0.4f, 0.8f),
                new Vector3f(0.7f, 0.5f, 0.9f),
                new Vector3f(0.85f, 0.7f, 1.0f)
        };
        float scale = 1.2f;

        for (int i = 0; i < 20; i++) {
            Vector3f color = colors[world.random.nextInt(colors.length)];
            double x = entity.getX() + (world.random.nextDouble() - 0.5) * entity.getWidth() * 1.2;
            double y = entity.getY() + world.random.nextDouble() * entity.getHeight();
            double z = entity.getZ() + (world.random.nextDouble() - 0.5) * entity.getWidth() * 1.2;
            world.spawnParticles(new DustParticleEffect(color, scale), x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private void spawnHexagonParticleRing(ServerWorld world, Vec3d center, double radius, Vector3f[] colors) {
        int pointsPerSide = 8;
        double angleStep = Math.PI / 3.0;

        for (int side = 0; side < 6; side++) {
            double startAngle = side * angleStep;

            Vec3d p1 = new Vec3d(
                    center.getX() + radius * Math.cos(startAngle),
                    center.getY(),
                    center.getZ() + radius * Math.sin(startAngle)
            );
            Vec3d p2 = new Vec3d(
                    center.getX() + radius * Math.cos(startAngle + angleStep),
                    center.getY(),
                    center.getZ() + radius * Math.sin(startAngle + angleStep)
            );

            for (int i = 0; i <= pointsPerSide; i++) {
                double t = (double) i / pointsPerSide;
                Vec3d particlePos = p1.lerp(p2, t);

                Vector3f color = colors[world.random.nextInt(colors.length)];
                float scale = 1.0f;

                world.spawnParticles(new DustParticleEffect(color, scale), particlePos.x, particlePos.y, particlePos.z, 1, 0, 0, 0, 0);
            }
        }
    }

    private void spawnShardVolley(PlayerEntity player, World world, int count) {
        float radius = 2.0f;
        float forwardSpeed = 1.2f;
        float convergenceStrength = 0.03f;

        Vec3d forward = player.getRotationVector();
        Vec3d spawnCenter = player.getEyePos().add(forward.multiply(1.5));

        Vec3d right = new Vec3d(-forward.z, 0, forward.x).normalize();
        if (right.lengthSquared() < 1.0E-4) {
            right = player.getRotationVector(player.getPitch(), player.getYaw() - 90.0f).withAxis(Direction.Axis.Y, 0).normalize();
        }
        Vec3d up = forward.crossProduct(right);

        for (int i = 0; i < count; i++) {
            if (count <= 0) break;
            double divisor = (count > 1) ? (count - 1) : 1.0;
            double angle = Math.PI * i / divisor;
            double xOffset = Math.cos(angle) * radius;
            double yOffset = Math.sin(angle) * radius;
            Vec3d offset = right.multiply(xOffset).add(up.multiply(yOffset));
            Vec3d spawnPos = spawnCenter.add(offset);
            AmethystShardProjectileEntity projectile = new AmethystShardProjectileEntity(world, player);
            projectile.setPosition(spawnPos);
            projectile.setDamage(5);
            Vec3d forwardVelocity = forward.multiply(forwardSpeed);
            Vec3d inwardVelocity = offset.normalize().multiply(-1.0).multiply(convergenceStrength);

            projectile.setVelocity(forwardVelocity.add(inwardVelocity));

            world.spawnEntity(projectile);
        }
    }

    private Vec3d teleportPlayer(ServerWorld world, PlayerEntity player, boolean isShifting) {
        int distance = 8;
        Vec3d eyePos = player.getEyePos();
        Vec3d lookVec = player.getRotationVector();
        Vec3d endPos;

        if (isShifting) {
            endPos = eyePos.subtract(lookVec.multiply(distance));
        } else {
            endPos = eyePos.add(lookVec.multiply(distance));
        }

        RaycastContext context = new RaycastContext(eyePos, endPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player);
        BlockHitResult hitResult = world.raycast(context);

        Vec3d targetPos = hitResult.getPos();
        player.requestTeleport(targetPos.getX(), targetPos.getY(), targetPos.getZ());
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 0.6F);
        return targetPos;
    }
}