package com.k080.fathom.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class WindParticle extends SpriteBillboardParticle {

    private static final float ACCELERATION_SCALE = 0.05F;
    private static final float WIND_STRENGTH = 1.5F;
    private static final int MAX_LIFETIME = 300;
    private final float baseAngle;
    private final float rollSpeed;
    private float currentRoll;

    protected WindParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
        super(world, x, y, z);

        this.setSprite(spriteProvider.getSprite(this.random));
        this.currentRoll = (float) Math.toRadians(this.random.nextBoolean() ? -30.0 : 30.0);
        this.baseAngle = this.random.nextFloat();
        this.rollSpeed = (float) Math.toRadians(this.random.nextBoolean() ? -5.0 : 5.0);
        this.maxAge = MAX_LIFETIME;
        this.gravityStrength = 0.00075F;
        float scaleMod = this.random.nextBoolean() ? 0.05F : 0.075F;
        this.scale = scaleMod;
        this.setBoundingBoxSpacing(scaleMod, scaleMod);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        float lifeProgress = (float) this.age / (float) this.maxAge;
        float easedProgress = (float) Math.pow(lifeProgress, 1.25);

        double swirlX = Math.cos(Math.toRadians(this.baseAngle * 60.0)) * WIND_STRENGTH * easedProgress;
        double swirlZ = Math.sin(Math.toRadians(this.baseAngle * 60.0)) * WIND_STRENGTH * easedProgress;

        this.velocityX += swirlX * ACCELERATION_SCALE;
        this.velocityZ += swirlZ * ACCELERATION_SCALE;
        this.velocityY -= this.gravityStrength;

        this.currentRoll += this.rollSpeed / 20.0F;
        this.prevAngle = this.angle;
        this.angle += this.currentRoll / 20.0F;

        this.move(this.velocityX, this.velocityY, this.velocityZ);

        if (this.onGround || (this.age > MAX_LIFETIME - 2 && (this.velocityX == 0.0 || this.velocityZ == 0.0))) {
            this.markDead();
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;
        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new WindParticle(clientWorld, x, y, z, this.spriteProvider);
        }
    }
}