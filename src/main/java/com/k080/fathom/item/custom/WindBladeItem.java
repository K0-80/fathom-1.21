package com.k080.fathom.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WindBladeItem extends SwordItem {
    public static final int CHARGE_TIME = 8;
    private static final double RANGE = 25;
    private static final int COOLDOWN = 20;
    private static final float TARGET_HITBOX_EXPANSION = 0.5f;


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
        if (raycastForEntity(user) != null) {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
        return TypedActionResult.fail(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;

        int timeUsed = this.getMaxUseTime(stack, user) - remainingUseTicks;
        EntityHitResult hitResult = raycastForEntity(player);

        if (hitResult == null) {
            player.stopUsingItem();
            return;
        }

        if (timeUsed < CHARGE_TIME) {
            if (timeUsed % 4 == 0) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.PLAYERS, 0.3f, 0.5f + (timeUsed * 0.02f));
            }
        } else {
            Entity target = hitResult.getEntity();
            Vec3d direction = player.getRotationVec(1.0f).normalize();
            Vec3d teleportPos = hitResult.getPos().subtract(direction.multiply(1.5));
            Vec3d startPos = player.getPos();


            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PUFFER_FISH_DEATH, SoundCategory.PLAYERS, 0.7f, 1.2f);
            world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ITEM_TRIDENT_RIPTIDE_1, SoundCategory.PLAYERS, 1.5f, 0.5f);
            world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ENTITY_BREEZE_WIND_BURST, SoundCategory.PLAYERS, 0.5f, 2.0f);
            world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 5.0f, 1.0f);

            if (!world.isClient) {
                player.requestTeleport(teleportPos.getX(), teleportPos.getY() + 0.2, teleportPos.getZ());
                player.getItemCooldownManager().set(this, COOLDOWN);
                spawnDashParticles((ServerWorld) world, startPos, teleportPos);
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
    }

    private void spawnDashParticles(ServerWorld world, Vec3d start, Vec3d end) {
        world.spawnParticles(ParticleTypes.CLOUD, start.x, start.y + 0.5, start.z, 15, 0.4, 0.4, 0.4, 0.05);
        world.spawnParticles(ParticleTypes.GUST, start.x, start.y + 0.5, start.z, 5, 0.3, 0.3, 0.3, 0.1);

        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, end.x, end.y, end.z, 40, 0.5, 0.5, 0.5, 0.8);
        world.spawnParticles(ParticleTypes.GUST_EMITTER_SMALL, end.x, end.y, end.z, 1, 0, 0, 0, 0);
        world.spawnParticles(ParticleTypes.WHITE_SMOKE, end.x, end.y, end.z, 15, 0.8, 0.8, 0.8, 0.2);
    }

    private EntityHitResult raycastForEntity(PlayerEntity player) {
        Vec3d startPos = player.getEyePos();
        Vec3d rotation = player.getRotationVec(1.0f);
        Vec3d endPos = startPos.add(rotation.multiply(RANGE));
        Box box = new Box(startPos, endPos).expand(10.0);
        return ProjectileUtil.raycast(player, startPos, endPos, box,
                (entity) -> !entity.isSpectator() && entity.isAlive(), RANGE * RANGE
        );
    }
}