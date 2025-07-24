package com.k080.fathom.item.custom;

import com.k080.fathom.Fathom;
import com.k080.fathom.component.ModDataComponentTypes;
import com.k080.fathom.enchantment.ModEnchantments;
import com.k080.fathom.entity.ModEntities;
import com.k080.fathom.entity.custom.PlayerCloneEntity;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class Mirageitem extends SwordItem {

    public Mirageitem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        int shatterLevel = getEnchantmentLevel(world, stack, ModEnchantments.SHATTER);
        int phaseShiftLevel = getEnchantmentLevel(world, stack, ModEnchantments.PHASE_SHIFT);
        int projectionLevel = getEnchantmentLevel(world, stack, ModEnchantments.PROJECTION);

        if (!world.isClient()) {
            ServerWorld serverWorld = (ServerWorld) world;
            UUID cloneUuid = stack.get(ModDataComponentTypes.CLONE_UUID);

            if (cloneUuid != null) {
                Entity clone = serverWorld.getEntity(cloneUuid);

                if (clone instanceof PlayerCloneEntity pClone && pClone.isAlive()) {
                    pClone.shatterOnTeleport();
                    user.teleport(pClone.getX(), pClone.getY(), pClone.getZ(), false);
                    pClone.discard();
                    world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
                    stack.remove(ModDataComponentTypes.CLONE_UUID);

            } else {
                PlayerCloneEntity clone = new PlayerCloneEntity(ModEntities.PLAYER_CLONE, world);
                clone.setShatterLevel(shatterLevel); //ppasses the shatter levelk to clone
                clone.copyFrom(user);
                clone.refreshPositionAndAngles(user.getX(), user.getY(), user.getZ(), user.getYaw(), user.getPitch());
                world.spawnEntity(clone);

                stack.set(ModDataComponentTypes.CLONE_UUID, clone.getUuid());
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }

            user.getItemCooldownManager().set(this, 20); // 1-second cooldown
            return TypedActionResult.success(stack);
        }

        return TypedActionResult.pass(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        UUID cloneUuid = stack.get(ModDataComponentTypes.CLONE_UUID);

        if (cloneUuid != null) {
            tooltip.add(Text.literal("Clone Active").formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.literal("No Clone").formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }

    //ingnore this for now TM
//    @Override
//    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
//
//        if (!attacker.isOnGround() && !target.isOnGround()) {
//
//            attacker.setVelocity(Vec3d.ZERO);
//            target.setVelocity(Vec3d.ZERO);
//            target.damage(attacker.getDamageSources().magic(), 1.0f);
//
//            StatusEffectInstance levitationEffect = new StatusEffectInstance(StatusEffects.LEVITATION, 20, 1);
//
//            target.addStatusEffect(levitationEffect);
//            attacker.addStatusEffect(levitationEffect);
//        }
//
//        return super.postHit(stack, target, attacker);
//    }

    private int getEnchantmentLevel(World world, ItemStack stack, RegistryKey<Enchantment> enchantmentKey) {
        return world.getRegistryManager().get(RegistryKeys.ENCHANTMENT)
                .getEntry(enchantmentKey)
                .map(entry -> EnchantmentHelper.getLevel(entry, stack))
                .orElse(0);
    }
}