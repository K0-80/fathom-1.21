package com.k080.fathom.particle.custom;

import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RaptureParticle extends SpriteBillboardParticle {
    private final Quaternionf randomRotation;
    private final SpriteProvider spriteProvider;

    public RaptureParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;
        this.maxAge = 14;
        this.scale = 10f;
        this.gravityStrength = 0;
        this.velocityX = 0;
        this.velocityY = 0;
        this.velocityZ = 0;
        this.red = 1;
        this.green = 1;
        this.blue = 1;
        this.alpha = 1f;
        this.setSpriteForAge(this.spriteProvider);
        this.randomRotation = new Quaternionf().rotateY((float)(this.random.nextFloat() * Math.PI * 2.0F)).rotateX((float)(this.random.nextFloat() * Math.PI * 2.0F));
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d vec3d = camera.getPos();
        float f = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3d.getZ());

        Quaternionf quaternionf;
        if (this.angle == 0.0f) {
            quaternionf = this.randomRotation;
        } else {
            quaternionf = new Quaternionf(this.randomRotation);
            float i = MathHelper.lerp(tickDelta, this.prevAngle, this.angle);
            quaternionf.rotateZ(i);
        }

        Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float size = this.getSize(tickDelta);

        for(int j = 0; j < 4; ++j) {
            Vector3f vector3f = vector3fs[j];
            vector3f.rotate(quaternionf);
            vector3f.mul(size);
            vector3f.add(f, g, h);
        }

        int light = this.getBrightness(tickDelta);
        float uMin = this.getMinU();
        float uMax = this.getMaxU();
        float vMin = this.getMinV();
        float vMax = this.getMaxV();

        // Front Face
        vertexConsumer.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z()).texture(uMax, vMax).color(this.red, this.green, this.blue, this.alpha).light(light);
        vertexConsumer.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z()).texture(uMax, vMin).color(this.red, this.green, this.blue, this.alpha).light(light);
        vertexConsumer.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z()).texture(uMin, vMin).color(this.red, this.green, this.blue, this.alpha).light(light);
        vertexConsumer.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z()).texture(uMin, vMax).color(this.red, this.green, this.blue, this.alpha).light(light);

        // Back Face (flipped winding order)
        vertexConsumer.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z()).texture(uMin, vMax).color(this.red, this.green, this.blue, this.alpha).light(light);
        vertexConsumer.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z()).texture(uMin, vMin).color(this.red, this.green, this.blue, this.alpha).light(light);
        vertexConsumer.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z()).texture(uMax, vMin).color(this.red, this.green, this.blue, this.alpha).light(light);
        vertexConsumer.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z()).texture(uMax, vMax).color(this.red, this.green, this.blue, this.alpha).light(light);
    }

    @Override
    public float getSize(float tickDelta) {
        float d = (this.age + tickDelta) / (float)this.maxAge;
        return this.scale * MathHelper.clamp(d, 0, 1);
    }


    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
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
            return new RaptureParticle(clientWorld, d, e, f, this.spriteProvider);
        }
    }
}
