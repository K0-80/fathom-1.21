package com.k080.fathom.effect;

import com.k080.fathom.item.ModItems;
import com.k080.fathom.particle.ModParticles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;

import java.util.Objects;

public class AnchoredEffect extends StatusEffect {
    protected AnchoredEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld().isClient) {
            if (entity.age % 4 == 0) {
                Random random = entity.getWorld().getRandom();
                int particleCount = 3;

                for (int i = 0; i < particleCount; i++) {
                    double spawnX = entity.getX() + (random.nextDouble() - 0.5) * 0.4;
                    double spawnY = entity.getEyeY() - 0.4;
                    double spawnZ = entity.getZ() + (random.nextDouble() - 0.5) * 0.4;

                    double angle = random.nextDouble() * 2 * Math.PI;
                    double horizontalSpeed = 0.15;
                    double verticalSpeed = -0.2;

                    double velX = Math.cos(angle) * horizontalSpeed;
                    double velY = verticalSpeed;
                    double velZ = Math.sin(angle) * horizontalSpeed;

                    entity.getWorld().addParticle(ModParticles.ANCHORED_PARTICLE,
                            spawnX, spawnY, spawnZ,
                            velX, velY, velZ);
                }
            }
        }
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            int duration = Objects.requireNonNull(player.getStatusEffect(ModEffects.ANCHORED)).getDuration();
            ItemCooldownManager cooldownManager = player.getItemCooldownManager();

            cooldownManager.set(Items.ENDER_PEARL, duration);
            cooldownManager.set(Items.CHORUS_FRUIT, duration);
            cooldownManager.set(Items.WIND_CHARGE, duration);

            cooldownManager.set(ModItems.WIND_BLADE, duration);
            cooldownManager.set(ModItems.ANCHOR, duration);
            cooldownManager.set(ModItems.MIRAGE, duration);

            for (Item item : Registries.ITEM) {
                if (item instanceof BoatItem) {
                    cooldownManager.set(item, duration);
                }
            }
        }

        super.onApplied(entity, amplifier);
    }

}
