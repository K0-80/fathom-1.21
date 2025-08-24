package com.k080.fathom.particle.custom;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class MarkedParticle extends SpriteBillboardParticle {

    protected MarkedParticle(ClientWorld world, double x, double y, double z,
                             SpriteProvider spriteProvider, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        this.velocityMultiplier = 0.96F;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;

        this.scale = 0.075f;
        this.maxAge = 30;
        this.setSpriteForAge(spriteProvider);

        this.gravityStrength = 0.0f;
    }

    @Override
    public int getBrightness(float tickDelta) {
        return 15728880;
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
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new MarkedParticle(clientWorld, x, y, z, this.spriteProvider, velocityX, velocityY, velocityZ);
        }
    }
}