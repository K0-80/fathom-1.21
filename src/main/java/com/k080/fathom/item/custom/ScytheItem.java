package com.k080.fathom.item.custom;

import com.google.common.collect.Iterables;
import com.k080.fathom.component.ModComponents;
import com.k080.fathom.damage.ModDamageTypes;
import com.k080.fathom.effect.ModEffects;
import com.k080.fathom.enchantment.ModEnchantments;
import com.k080.fathom.particle.ModParticles;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
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
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;


public class ScytheItem extends SwordItem  {


    public ScytheItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        int flowstateLevel = getEnchantmentLevel(world, stack, ModEnchantments.FLOWSTATE);

            // Flowstate II: Instant ability
            if (flowstateLevel >= 2) {
                if (!world.isClient()) {
                if (!user.getItemCooldownManager().isCoolingDown(this)) {
                    triggerFlowstateEffect(user, world, stack, flowstateLevel);
                    user.getItemCooldownManager().set(this, 10); // to prevent wierd issues
                    stack.damage(2, user, PlayerEntity.getSlotForHand(user.getActiveHand()));
                }
                }
                return TypedActionResult.success(stack);
            }

        // Flowstate I or Rupture: Begin charging
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }


    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient() || !(user instanceof PlayerEntity player)) {
            return;
        }
        int chargeTime = 72000 - remainingUseTicks;
        int flowstateLevel = getEnchantmentLevel(world, stack, ModEnchantments.FLOWSTATE);

        if (player.getItemCooldownManager().isCoolingDown(this)) {
            player.stopUsingItem();
            return;
        }

        if (chargeTime % 10 == 0 && world instanceof ServerWorld) {
            world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.PLAYERS, 0.4f, 1.25f + ((float) chargeTime /40));
            world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_SCULK_SPREAD, SoundCategory.PLAYERS, 2.0f, 0.2f);
        }

        // --- Flowstate I Ability ---
        if (flowstateLevel == 1) {
            if (chargeTime == 30) {
                player.stopUsingItem();
                if (!player.getItemCooldownManager().isCoolingDown(this)) {
                    triggerFlowstateEffect(player, world, stack,flowstateLevel);
                    player.getItemCooldownManager().set(this, 10);
                    stack.damage(2, player, PlayerEntity.getSlotForHand(player.getActiveHand()));
                }
            }
            // --- Rupture Ability ---
        } else {
            if (chargeTime == 30) { // Triggers at 1.5 seconds
                int soulsAvailable = getSouls(stack);
                int baseCost = 8;
                int extraSouls = 0;

                if (soulsAvailable >= baseCost) {
                    extraSouls = soulsAvailable - baseCost;
                    paySoulCost(player, stack, soulsAvailable);
                } else {
                    paySoulCost(player, stack, baseCost);
                }

                float radius = 5.0f + extraSouls;
                int effectDuration = (3 + extraSouls) * 20;

                player.getItemCooldownManager().set(this, 10);
                playRuptureActivationEffects(player, world, radius, extraSouls);

                Box aoe = new Box(player.getPos(), player.getPos()).expand(radius);
                for (LivingEntity target : world.getNonSpectatingEntities(LivingEntity.class, aoe)) {
                    if (target != player && player.getPos().distanceTo(target.getPos()) <= radius) {
                        target.addStatusEffect(new StatusEffectInstance(ModEffects.ANCHORED, effectDuration, 0, true, false ));
                    }
                }
                player.stopUsingItem();
                stack.damage(2, player, PlayerEntity.getSlotForHand(player.getActiveHand()));
            }
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient) {
            if (!target.isAlive()) {
                addSouls(stack, 1);
            }
        }
        return super.postHit(stack, target, attacker);
    }

    private int getSouls(ItemStack stack) {
        return stack.getOrDefault(ModComponents.SOULS, 0);
    }
    private void setSouls(ItemStack stack, int souls) {
        stack.set(ModComponents.SOULS, Math.min(10, Math.max(0, souls)));
    }
    private void addSouls(ItemStack stack, int amount) {
        int currentSouls = getSouls(stack);
        setSouls(stack, currentSouls + amount);
    }
    private void removeSouls (ItemStack stack, int amount) {
        int currentSouls = getSouls(stack);
        setSouls(stack, currentSouls - amount);
    }

    private int getEnchantmentLevel(World world, ItemStack stack, RegistryKey<Enchantment> enchantmentKey) {
        return world.getRegistryManager().get(RegistryKeys.ENCHANTMENT)
                .getEntry(enchantmentKey)
                .map(entry -> EnchantmentHelper.getLevel(entry, stack))
                .orElse(0);
    }

    private void paySoulCost(PlayerEntity player, ItemStack stack, int cost) {
        World world = player.getWorld();
        int currentSouls = getSouls(stack);

        if (currentSouls >= cost) {
            removeSouls(stack, cost);

            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.PLAYERS, 1.0f, 1.2f);
        } else {
            int missingSouls = cost - currentSouls;
            setSouls(stack, 0);

            int sanguineLevel = getEnchantmentLevel(world, stack, ModEnchantments.SANGUINE_COVENANT);
            float damagePerSoul = sanguineLevel > 0 ? 1.0f : 2.0f;
            float totalDamage = missingSouls * damagePerSoul;

            DamageSource damageSource = new DamageSource(player.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.SCYTHE_COVENANT), player);
            player.damage(damageSource, totalDamage);

            float sound = Math.max(0.5f, 1.2f - (totalDamage * 0.1f));
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.PLAYERS, 1.0f, 1.2f);
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_EVOKER_FANGS_ATTACK, SoundCategory.PLAYERS, sound, sound);

            if (world instanceof ServerWorld serverWorld) {
                BlockStateParticleEffect particleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK,
                        (sanguineLevel >= 1) ? Blocks.RED_GLAZED_TERRACOTTA.getDefaultState() : Blocks.BLACK_GLAZED_TERRACOTTA.getDefaultState());

                int particleCount = Math.max(10, (int)(totalDamage * 6));

                serverWorld.spawnParticles(particleEffect,
                        player.getX(),
                        player.getBodyY(0.5),
                        player.getZ(),
                        particleCount,
                        0.4, 0.5, 0.4, 0.1
                );
            }
        }
    }

    private void triggerFlowstateEffect(PlayerEntity player, World world, ItemStack stack, int flowstateLevel) {

        int soulCost = 6;
        paySoulCost(player, stack, soulCost);

        playFlowstateActivationEffects(player, world, flowstateLevel);

        Iterable<ItemStack> fullInventory = Iterables.concat(
                player.getInventory().main,
                player.getInventory().armor,
                player.getInventory().offHand
        );

        for (ItemStack inventoryStack : fullInventory) {
            if (inventoryStack.isEmpty()) {
                continue;
            }

            Item item = inventoryStack.getItem();
            if (item != this && player.getItemCooldownManager().isCoolingDown(item)) {
                player.getItemCooldownManager().remove(item);
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.PLAYERS, 0.5f, 1.5f + world.getRandom().nextFloat() * 0.5f);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("Souls: " + getSouls(stack) + " / 10").formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }
    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    private void playFlowstateActivationEffects(PlayerEntity player, World world, int flowstateLevel) {
        if (world instanceof ServerWorld serverWorld) {
            if (flowstateLevel >= 2) {
                world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.PLAYERS, 0.8f, 1.2f);
            }
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 0.5f, 2.0f);
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.PLAYERS, 2.0f, 0.5f);
            serverWorld.spawnParticles(ModParticles.FLOWSTATE_PARTICLE, player.getX(), player.getY() +0.5, player.getZ(), 1, 0, 0, 0, 0);
        }
    }

    private void playRuptureActivationEffects(PlayerEntity player, World world, float radius, int extraSouls) {
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 0.8f, 0.80f);
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PUFFER_FISH_DEATH, SoundCategory.PLAYERS, 1f, 0.5f);
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 0.6f, 0.57f);
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.PLAYERS, 2.0f, 0.5f);
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.PLAYERS, 2.0f, 0.5f);

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ModParticles.RAPTURE_PARTICLE, player.getX(), player.getY() + 1, player.getZ(), 6, 0, 0, 0, 0);
        }
    }
}
