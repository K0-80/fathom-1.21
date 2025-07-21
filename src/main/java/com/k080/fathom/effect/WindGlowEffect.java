package com.k080.fathom.effect;

import com.k080.fathom.particle.ModParticles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class WindGlowEffect extends StatusEffect {
    public WindGlowEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld().isClient) {
            if (entity.age % 5 == 0) {
                Random random = entity.getWorld().getRandom();
                int particleCount = 2;

                for (int i = 0; i < particleCount; i++) {
                    double radius = 2.5;
                    double angle = random.nextDouble() * 2 * Math.PI;
                    double height = (random.nextDouble() - 0.5) * 2;

                    double offsetX = Math.cos(angle) * radius;
                    double offsetZ = Math.sin(angle) * radius;

                    double spawnX = entity.getX() + offsetX;
                    double spawnY = entity.getBodyY(0.5) + height;
                    double spawnZ = entity.getZ() + offsetZ;

                    Vec3d targetPos = entity.getPos().add(0, entity.getHeight() / 2.0, 0);
                    Vec3d spawnPos = new Vec3d(spawnX, spawnY, spawnZ);

                    Vec3d velocity = targetPos.subtract(spawnPos).normalize().multiply(0.1);

                    entity.getWorld().addParticle(ModParticles.MARKED_PARTICLE,
                            spawnX, spawnY, spawnZ,
                            velocity.x, velocity.y, velocity.z);
                }
            }
        }

        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}