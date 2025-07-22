package com.k080.fathom.particle.custom;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;

public class FlowstateParticle extends ExplosionLargeParticle {
    public FlowstateParticle(ClientWorld world, double x, double y, double z, double d, SpriteProvider spriteProvider) {
        super(world, x, y, z, d, spriteProvider);
        this.maxAge = 11;
        this.scale = 4.5f;
        this.gravityStrength = 0;
        this.velocityX = 0;
        this.velocityY = 0;
        this.velocityZ = 0;
        this.red = 1;
        this.green = 1;
        this.blue = 1;
        this.alpha = 1f;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public float getSize(float tickDelta) {
        float d = (this.age + tickDelta) / (this.maxAge);
        return this.scale * MathHelper.clamp(d, 0, 1);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientWorld clientWorld,
                                       double d, double e, double f, double g, double h, double i) {
            return new FlowstateParticle(clientWorld, d, e, f, g, this.spriteProvider);
        }
    }
}
