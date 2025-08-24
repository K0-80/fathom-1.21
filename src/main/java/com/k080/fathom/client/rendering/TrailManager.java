package com.k080.fathom.client.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class TrailManager {

    private static final List<Trail> trails = new CopyOnWriteArrayList<>();

    private static final List<Vector3f> ICOSPHERE_VERTICES;
    private static final List<Integer> ICOSPHERE_INDICES;

    static {
        List<Vector3f> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        int subdivisions = 2;
        float t = (1.0f + (float) Math.sqrt(5.0)) / 2.0f;
        vertices.add(new Vector3f(-1, t, 0).normalize());
        vertices.add(new Vector3f(1, t, 0).normalize());
        vertices.add(new Vector3f(-1, -t, 0).normalize());
        vertices.add(new Vector3f(1, -t, 0).normalize());
        vertices.add(new Vector3f(0, -1, t).normalize());
        vertices.add(new Vector3f(0, 1, t).normalize());
        vertices.add(new Vector3f(0, -1, -t).normalize());
        vertices.add(new Vector3f(0, 1, -t).normalize());
        vertices.add(new Vector3f(t, 0, -1).normalize());
        vertices.add(new Vector3f(t, 0, 1).normalize());
        vertices.add(new Vector3f(-t, 0, -1).normalize());
        vertices.add(new Vector3f(-t, 0, 1).normalize());
        int[] baseIndices = {0, 11, 5, 0, 5, 1, 0, 1, 7, 0, 7, 10, 0, 10, 11, 1, 5, 9, 5, 11, 4, 11, 10, 2, 10, 7, 6, 7, 1, 8, 3, 9, 4, 3, 4, 2, 3, 2, 6, 3, 6, 8, 3, 8, 9, 4, 9, 5, 2, 4, 11, 6, 2, 10, 8, 6, 7, 9, 8, 1};
        for (int i = 0; i < baseIndices.length; i++) indices.add(baseIndices[i]);
        Map<Long, Integer> midpointCache = new HashMap<>();
        for (int i = 0; i < subdivisions; i++) {
            List<Integer> newIndices = new ArrayList<>();
            for (int j = 0; j < indices.size(); j += 3) {
                int i1 = indices.get(j);
                int i2 = indices.get(j + 1);
                int i3 = indices.get(j + 2);
                int m12 = getMidpoint(i1, i2, vertices, midpointCache);
                int m23 = getMidpoint(i2, i3, vertices, midpointCache);
                int m31 = getMidpoint(i3, i1, vertices, midpointCache);
                newIndices.add(i1); newIndices.add(m12); newIndices.add(m31);
                newIndices.add(i2); newIndices.add(m23); newIndices.add(m12);
                newIndices.add(i3); newIndices.add(m31); newIndices.add(m23);
                newIndices.add(m12); newIndices.add(m23); newIndices.add(m31);
            }
            indices = newIndices;
        }
        ICOSPHERE_VERTICES = vertices;
        ICOSPHERE_INDICES = indices;
    }

    private static int getMidpoint(int p1, int p2, List<Vector3f> vertices, Map<Long, Integer> cache) {
        long smaller = Math.min(p1, p2);
        long greater = Math.max(p1, p2);
        long key = (smaller << 32) + greater;
        if (cache.containsKey(key)) return cache.get(key);
        Vector3f v1 = vertices.get(p1);
        Vector3f v2 = vertices.get(p2);
        Vector3f middle = new Vector3f((v1.x + v2.x) / 2f, (v1.y + v2.y) / 2f, (v1.z + v2.z) / 2f).normalize();
        int index = vertices.size();
        vertices.add(middle);
        cache.put(key, index);
        return index;
    }

    public static void addTrail(Trail trail) {
        trails.add(trail);
    }

    public static void tick() {
        trails.forEach(Trail::tick);
        trails.removeIf(Trail::isDead);
    }

    public static void clear() {
        trails.clear();
    }

    public static void render(WorldRenderContext context) {
        if (trails.isEmpty()) return;

        MatrixStack matrixStack = context.matrixStack();
        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();
        VertexConsumerProvider.Immediate vertexConsumers = (VertexConsumerProvider.Immediate) context.consumers();

        matrixStack.push();
        matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);
        Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();

        VertexConsumer buffer = vertexConsumers.getBuffer(FathomRenderLayers.getTrailLayer());
        for (Trail trail : trails) {
            renderTrail(trail, positionMatrix, buffer, camera);
        }

        matrixStack.pop();
        vertexConsumers.draw(FathomRenderLayers.getTrailLayer());
    }

    private static void renderTrail(Trail trail, Matrix4f positionMatrix, VertexConsumer buffer, Camera camera) {
        List<Vec3d> originalPoints = trail.getPoints();
        if (originalPoints.size() < 2) return;
        List<Vec3d> points = new ArrayList<>();
        points.add(originalPoints.get(0));
        float segmentLength = 0.075f;
        for (int i = 0; i < originalPoints.size() - 1; i++) {
            Vec3d start = originalPoints.get(i);
            Vec3d end = originalPoints.get(i + 1);
            double dist = start.distanceTo(end);
            if (dist > segmentLength) {
                int segments = (int) Math.ceil(dist / segmentLength);
                for (int j = 1; j <= segments; j++) points.add(start.lerp(end, (double) j / segments));
            } else {
                points.add(end);
            }
        }
        if (points.size() < 2) return;

        float alpha = trail.getAlpha();
        if (alpha <= 0.01f) return;

        int sides = 6;
        List<List<Vec3d>> ringVertices = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            float segmentProgress = (float) i / (points.size() - 1);
            float radius = trail.getWidth(segmentProgress) / 2.0f;
            Vec3d currentPoint = points.get(i);

            Vec3d direction;
            if (i < points.size() - 1) {
                direction = points.get(i + 1).subtract(currentPoint).normalize();
            } else {
                direction = currentPoint.subtract(points.get(i - 1)).normalize();
            }

            Vec3d referenceUp = new Vec3d(0, 1, 0); // Use world 'up' as a stable reference
            Vec3d side = direction.crossProduct(referenceUp).normalize();
            if (side.lengthSquared() < 1e-6) {
                side = direction.crossProduct(new Vec3d(1, 0, 0)).normalize();
            }
            Vec3d up = direction.crossProduct(side).normalize();

            List<Vec3d> currentRing = new ArrayList<>();
            for (int j = 0; j < sides; j++) {
                double angle = (double) j / sides * 2.0 * Math.PI;
                Vec3d offset = side.multiply(Math.cos(angle)).add(up.multiply(Math.sin(angle))).multiply(radius);
                currentRing.add(currentPoint.add(offset));
            }
            ringVertices.add(currentRing);
        }

        for (int i = 0; i < ringVertices.size() - 1; i++) {
            List<Vec3d> prevRing = ringVertices.get(i);
            List<Vec3d> currentRing = ringVertices.get(i + 1);

            for (int j = 0; j < sides; j++) {
                int next_j = (j + 1) % sides;
                Vec3d v1 = prevRing.get(j);
                Vec3d v2 = currentRing.get(j);
                Vec3d v3 = currentRing.get(next_j);
                Vec3d v4 = prevRing.get(next_j);

                buffer.vertex(positionMatrix, (float)v1.x, (float)v1.y, (float)v1.z).color(trail.color.x(), trail.color.y(), trail.color.z(), alpha);
                buffer.vertex(positionMatrix, (float)v2.x, (float)v2.y, (float)v2.z).color(trail.color.x(), trail.color.y(), trail.color.z(), alpha);
                buffer.vertex(positionMatrix, (float)v3.x, (float)v3.y, (float)v3.z).color(trail.color.x(), trail.color.y(), trail.color.z(), alpha);

                buffer.vertex(positionMatrix, (float)v3.x, (float)v3.y, (float)v3.z).color(trail.color.x(), trail.color.y(), trail.color.z(), alpha);
                buffer.vertex(positionMatrix, (float)v4.x, (float)v4.y, (float)v4.z).color(trail.color.x(), trail.color.y(), trail.color.z(), alpha);
                buffer.vertex(positionMatrix, (float)v1.x, (float)v1.y, (float)v1.z).color(trail.color.x(), trail.color.y(), trail.color.z(), alpha);
            }
        }

        Vec3d headCenter = points.get(points.size() - 1);
        float headRadius = (trail.getWidth(1.0f) / 2.0f) * 1.1f;
        if (headRadius < 0.01f) return;

        for (int i = 0; i < ICOSPHERE_INDICES.size(); i += 3) {
            Vector3f v1 = ICOSPHERE_VERTICES.get(ICOSPHERE_INDICES.get(i));
            Vector3f v2 = ICOSPHERE_VERTICES.get(ICOSPHERE_INDICES.get(i + 1));
            Vector3f v3 = ICOSPHERE_VERTICES.get(ICOSPHERE_INDICES.get(i + 2));

            buffer.vertex(positionMatrix, (float) (headCenter.x + v1.x * headRadius), (float) (headCenter.y + v1.y * headRadius), (float) (headCenter.z + v1.z * headRadius)).color(trail.color.x(), trail.color.y(), trail.color.z(), alpha);
            buffer.vertex(positionMatrix, (float) (headCenter.x + v2.x * headRadius), (float) (headCenter.y + v2.y * headRadius), (float) (headCenter.z + v2.z * headRadius)).color(trail.color.x(), trail.color.y(), trail.color.z(), alpha);
            buffer.vertex(positionMatrix, (float) (headCenter.x + v3.x * headRadius), (float) (headCenter.y + v3.y * headRadius), (float) (headCenter.z + v3.z * headRadius)).color(trail.color.x(), trail.color.y(), trail.color.z(), alpha);
        }
    }

    private static Vec3d calculateSideVector(Vec3d point, Vec3d direction, Camera camera, float width) {
        if (width <= 0) return Vec3d.ZERO;
        Vec3d cameraPos = camera.getPos();
        Vec3d viewVector = cameraPos.subtract(point).normalize();
        Vec3d sideVector = direction.normalize().crossProduct(viewVector).normalize().multiply(width / 2.0);
        if (sideVector.lengthSquared() < 0.01) {
            Vector3f up = camera.getVerticalPlane();
            return new Vec3d(up.x(), up.y(), up.z()).multiply(width / 2.0);
        }
        return sideVector;
    }
}