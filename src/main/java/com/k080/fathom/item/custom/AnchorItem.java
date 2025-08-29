package com.k080.fathom.item.custom;

import com.k080.fathom.component.ModComponents;
import com.k080.fathom.enchantment.ModEnchantments;
import com.k080.fathom.entity.ModEntities;
import com.k080.fathom.entity.custom.AnchorProjectileEntity;
import com.k080.fathom.item.ModToolMaterials;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class AnchorItem extends ToolItem {

    public AnchorItem(ModToolMaterials anchor, Settings settings) {
        super(ModToolMaterials.ANCHOR, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 10, 0));
        World world = target.getWorld();

        world.playSound(null, target.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 0.1f, 0.6f);
        world.playSound(null, target.getBlockPos(), SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.PLAYERS, 2.0f, 1.0f);
        world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED, SoundCategory.PLAYERS, 0.5f, 1.2f);
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SPLASH,
                    target.getX(), target.getBodyY(0.5), target.getZ(), 20, 0.3, 0.5, 0.3, 0.0);
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (world instanceof ServerWorld serverWorld) {
            Optional<UUID> projectileUuidOpt = itemStack.get(ModComponents.THROWN_ANCHOR_UUID);

            if (projectileUuidOpt != null && projectileUuidOpt.isPresent()) { //whle anchor exists
                Entity entity = serverWorld.getEntity(projectileUuidOpt.get());

                if (entity instanceof AnchorProjectileEntity anchorProjectile && anchorProjectile.isAlive()) {
                    if (!anchorProjectile.isReturning()) {
                        int heaveLevel = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(ModEnchantments.HEAVE).map(entry -> EnchantmentHelper.getLevel(entry, itemStack)).orElse(0);

                        if (heaveLevel > 0 && !anchorProjectile.isFlying()) { //heave enchant
//                            Vec3d anchorPos = anchorProjectile.getPos();
//                            Vec3d playerPos = user.getPos();
//                            Vec3d direction = anchorPos.subtract(playerPos);
//
//                            if (direction.lengthSquared() > 0.01) {
//                                double maxSpeed = 3.5;
//                                double speed = Math.min(maxSpeed, 1 + (heaveLevel * 0.25));
//                                Vec3d velocity = direction.normalize().multiply(speed);
//
//                                user.setVelocity(velocity);
//                                user.velocityModified = true;
//                            }
//                            user.fallDistance = 0.0f;
//
//                            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_TRIDENT_RIPTIDE_3, SoundCategory.PLAYERS, 1.5f, 0.6f + (heaveLevel * 0.1f));
//                            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_CHAIN_BREAK, SoundCategory.PLAYERS, 2.0f, 0.8f);
//
//                            anchorProjectile.discard();
//                            itemStack.remove(ModComponents.THROWN_ANCHOR_UUID);
//                            itemStack.damage(1, user, LivingEntity.getSlotForHand(hand));

                        } else { //return
                            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.PLAYERS, 1f, 0.5f);
                            anchorProjectile.startReturning();
                        }
                    }
                } else {
                    itemStack.remove(ModComponents.THROWN_ANCHOR_UUID);
                }

            } else { // Throw
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 0.8f, 0.6f);
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.PLAYERS, 2.0f, 1.2f);

                AnchorProjectileEntity anchorProjectile = new AnchorProjectileEntity(ModEntities.ANCHOR_PROJECTILE, world);

                int heaveLevel = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(ModEnchantments.HEAVE).map(entry -> EnchantmentHelper.getLevel(entry, itemStack)).orElse(0);
                anchorProjectile.setHeaveLevel(heaveLevel);
                int undertowLevel = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(ModEnchantments.UNDERTOW).map(entry -> EnchantmentHelper.getLevel(entry, itemStack)).orElse(0);
                anchorProjectile.setUndertowLevel(undertowLevel);
                int crushingDepthLevel = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(ModEnchantments.CRUSHING_DEPTH).map(entry -> EnchantmentHelper.getLevel(entry, itemStack)).orElse(0);
                anchorProjectile.setCrushingDepthLevel(crushingDepthLevel);

                anchorProjectile.setOwner(user);
                anchorProjectile.setPosition(user.getEyePos().getX(), user.getEyePos().getY() - 0.5, user.getEyePos().getZ());
                anchorProjectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 2.5F, 1.0F);

                world.spawnEntity(anchorProjectile);
                itemStack.set(ModComponents.THROWN_ANCHOR_UUID, Optional.of(anchorProjectile.getUuid()));

                user.incrementStat(Stats.USED.getOrCreateStat(this));
                itemStack.damage(2, user, LivingEntity.getSlotForHand(hand));
            }
        }
        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClient() && entity instanceof PlayerEntity player) {
            Optional<UUID> projectileUuidOpt = stack.get(ModComponents.THROWN_ANCHOR_UUID);

            if (projectileUuidOpt != null && projectileUuidOpt.isPresent()) {
                Entity projectile = ((ServerWorld) world).getEntity(projectileUuidOpt.get());

                if (projectile instanceof AnchorProjectileEntity anchorProjectile) {
                    if (anchorProjectile.isFlying()) {
                        if (world.getRandom().nextInt(2) == 0) {
                            final float MAX_SOUND_DISTANCE = 27.0f;
                            final float MAX_VOLUME = 1.3f;
                            final float MIN_VOLUME = 0.2f;

                            float distance = player.distanceTo(anchorProjectile);
                            float progress = 1.0f - MathHelper.clamp(distance / MAX_SOUND_DISTANCE, 0.0f, 1.0f);
                            float volume = MathHelper.lerp(progress, MIN_VOLUME, MAX_VOLUME);

                            float pitch = 0.7f + world.getRandom().nextFloat() * 0.4f;

                            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.PLAYERS, volume, pitch);
                            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.PLAYERS, volume, pitch);

                        }
                    }
                } else {
                    stack.remove(ModComponents.THROWN_ANCHOR_UUID);
                }
            }
        }
    }
}