package com.k080.fathom.client.rendering;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Trail {

    private final List<Vec3d> points = new LinkedList<>();
    public final Vector3f color;
    public final float baseWidth;

    private final Entity anchorEntity;
    private final Vec3d anchorOffset;
    private final int lifetime;
    private final int maxLength;
    private final int fadeOutTime;

    private int age = 0;
    private Vec3d centerPoint;

    // Parameters for orbital movement
    private double radius;
    private double angle;
    private final double angleVelocity;
    private final double verticalDrift;
    private final double radiusChange;

    private double oscillationAngle = 0;
    private final double oscillationFrequency;
    private final double oscillationAmplitude;
    private final Vec3d oscillationAxis;

    public Trail(Entity anchor, Vec3d anchorOffset, Vector3f color, float baseWidth, int lifetime, int maxLength) {
        this.anchorEntity = anchor;
        this.anchorOffset = anchorOffset;
        this.color = color;
        this.baseWidth = baseWidth;
        this.lifetime = lifetime;
        this.maxLength = maxLength;
        this.fadeOutTime = 20;

        this.centerPoint = anchor.getPos().add(this.anchorOffset);

        Random random = new Random();
        this.radius = 1.2 + random.nextDouble() * 0.8;
        this.angle = random.nextDouble() * Math.PI * 2;
        this.angleVelocity = (random.nextBoolean() ? 1 : -1) * (0.1 + random.nextDouble() * 0.1);
        this.verticalDrift = (random.nextDouble() - 0.5) * 0.05;
        this.radiusChange = -0.02;

        this.oscillationFrequency = 0.3 + random.nextDouble() * 0.2;
        this.oscillationAmplitude = 0.1 + random.nextDouble() * 0.1;
        this.oscillationAxis = new Vec3d(random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5).normalize();
    }

    public void tick() {
        age++;

        boolean shouldGeneratePoints = anchorEntity != null && anchorEntity.isAlive() && age < lifetime;

        if (shouldGeneratePoints) {
            Vec3d targetPoint = anchorEntity.getPos().add(this.anchorOffset);

            double attractionStrength = 0.15;
            this.centerPoint = this.centerPoint.lerp(targetPoint, attractionStrength);

            angle += angleVelocity;
            radius += radiusChange;
            radius = Math.max(0.3, radius);

            double x = centerPoint.x + Math.cos(angle) * radius;
            double z = centerPoint.z + Math.sin(angle) * radius;
            double y = centerPoint.y + (age * verticalDrift);

            Vec3d orbitalPosition = new Vec3d(x, y, z);

            oscillationAngle += oscillationFrequency;
            Vec3d wobble = oscillationAxis.multiply(Math.sin(oscillationAngle) * oscillationAmplitude);
            Vec3d finalPosition = orbitalPosition.add(wobble);

            points.add(finalPosition);

            while (points.size() > maxLength) {
                points.remove(0);
            }
        } else {
            if (!points.isEmpty()) {
                points.remove(0);
            }
        }
    }

    public boolean isDead() {
        return age >= lifetime && points.isEmpty();
    }

    public List<Vec3d> getPoints() {
        return points;
    }

    public float getWidth(float segmentProgress) {
        return baseWidth * MathHelper.sin(segmentProgress * ((float)Math.PI / 2.0f));
    }

    public float getAlpha() {
        if (age > lifetime - fadeOutTime) {
            float fadeProgress = (float)(age - (lifetime - fadeOutTime)) / fadeOutTime;
            return MathHelper.clamp(1.0f - fadeProgress, 0.0f, 1.0f);
        }
        return Math.min(1.0f, (float) age / 10.0f);
    }
}