package com.k080.fathom.event;

import com.k080.fathom.damage.ModDamageTypes;
import com.k080.fathom.item.custom.GauntletItem;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class GauntletAttackHandler {
    public static void register() {
        AttackEntityCallback.EVENT.register(GauntletAttackHandler::onAttack);
    }

    private static ActionResult onAttack(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (world.isClient() || !(entity instanceof LivingEntity target) || !(player.getMainHandStack().getItem() instanceof GauntletItem)) {
            return ActionResult.PASS;
        }

        float cooldownProgress = player.getAttackCooldownProgress(0.0f);

        if (cooldownProgress < 0.9f) {
            if (GauntletItem.PLAYER_COMBO_DATA.containsKey(player.getUuid())) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, SoundCategory.PLAYERS, 1f, 1f);
            }
            return ActionResult.PASS;
        }

        handleFullPowerAttack(player, target);
        return ActionResult.FAIL;
    }

    private static void handleFullPowerAttack(PlayerEntity player, LivingEntity target) {
        World world = player.getWorld();
        long currentTime = world.getTime();

        GauntletItem.ComboData comboData = GauntletItem.PLAYER_COMBO_DATA.getOrDefault(player.getUuid(), new GauntletItem.ComboData(0, 0));
        int nextStep = comboData.comboStep() + 1;

        applyComboDamageAndEffects(nextStep, player, target);

        if (nextStep >= 3) {
            GauntletItem.PLAYER_COMBO_DATA.remove(player.getUuid());
        } else {
            GauntletItem.PLAYER_COMBO_DATA.put(player.getUuid(), new GauntletItem.ComboData(nextStep, currentTime));
        }
    }

    private static void applyComboDamageAndEffects(int comboStep, PlayerEntity player, LivingEntity target) {
        World world = player.getWorld();
        ServerWorld serverWorld = (ServerWorld) world;

        switch (comboStep) {
            case 1: // 4 total damage
                target.damage(world.getDamageSources().playerAttack(player), 4.0f);
                world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1.0f, 0.8f);
                serverWorld.spawnParticles(ParticleTypes.CRIT, target.getX(), target.getBodyY(0.5), target.getZ(), 8, 0.3, 0.3, 0.3, 0.1);
                break;
            case 2: // 6 total damage
                target.damage(world.getDamageSources().playerAttack(player), 6.0f);
                world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1.0f, 0.8f);
                world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1.0f, 1.5f);
                serverWorld.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBodyY(0.5), target.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
                break;
            case 3: // 10 total damage, bypass armour
                DamageSource gauntletDamage = world.getDamageSources().create(ModDamageTypes.GAUNTLET_COMBO, player);
                target.damage(gauntletDamage, 10.0f);
                world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1.0f, 0.8f);
                world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.PLAYERS, 0.8f, 1.2f);
                serverWorld.spawnParticles(ParticleTypes.EXPLOSION, target.getX(), target.getEyeY(), target.getZ(), 1, 0, 0, 0, 0);
                serverWorld.spawnParticles(ParticleTypes.LAVA, target.getX(), target.getBodyY(0.5), target.getZ(), 20, 0.3, 0.3, 0.3, 0.0);
                break;
        }
    }
}