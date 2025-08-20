package com.k080.fathom.entity.client;

import com.k080.fathom.Fathom;
import com.k080.fathom.entity.client.model.BackwardMirageModel;
import com.k080.fathom.entity.client.model.ForwardMirageModel;
import com.k080.fathom.entity.custom.MirageModelEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class MirageModelEntityRenderer extends EntityRenderer<MirageModelEntity> {

    private static final Identifier TEXTURE = Identifier.of(Fathom.MOD_ID, "textures/entity/mirage_clone.png");
    private final ForwardMirageModel forwardModel;
    private final BackwardMirageModel backwardModel;

    public MirageModelEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.forwardModel = new ForwardMirageModel(ctx.getPart(ForwardMirageModel.LAYER));
        this.backwardModel = new BackwardMirageModel(ctx.getPart(BackwardMirageModel.LAYER));
    }

    @Override
    public void render(MirageModelEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Vec3d spawnPos = entity.getPos();
        Vec3d targetPos = entity.getClientTargetPos();

        // Don't render until the client has the synced target data
        if (targetPos == null) {
            return;
        }

        matrices.push();

        float progress = 0f;
        if (entity.age > MirageModelEntity.STATIONARY_TICKS) {
            progress = (entity.age + tickDelta - MirageModelEntity.STATIONARY_TICKS) / (float) MirageModelEntity.FLY_DURATION;
            progress = MathHelper.clamp(progress, 0.0f, 1.0f);
        }

        // Apply an easing function for deceleration (ease-out cubic)
        float easedProgress = 1.0f - (float)Math.pow(1.0f - progress, 3.0);
        Vec3d currentPos = spawnPos.lerp(targetPos, easedProgress);

        // The dispatcher translates to the entity's base position (spawnPos).
        // We only need to add the translation from spawnPos to our animated position.
        Vec3d renderOffset = currentPos.subtract(spawnPos);
        matrices.translate(renderOffset.getX(), renderOffset.getY(), renderOffset.getZ());

        float renderYaw = entity.getYaw();

        matrices.translate(0.0, 1.5, 0.0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - renderYaw));
        matrices.scale(-1.0f, -1.0f, 1.0f);

        // Alpha is based on the linear progress, not the eased progress
        float alpha = 1.0f - progress;
        int color = ColorHelper.Argb.fromFloats(alpha, 1.0f, 1.0f, 1.0f);
        float animationProgress = entity.age + tickDelta;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(entity)));

        if (entity.getModelType() == 0) { // Forward
            this.forwardModel.setAngles(entity, 0.0f, 0.0f, animationProgress, 0.0f, 0.0f);
            this.forwardModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, color);
        } else { // Backward
            this.backwardModel.setAngles(entity, 0.0f, 0.0f, animationProgress, 0.0f, 0.0f);
            this.backwardModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, color);
        }

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(MirageModelEntity entity) {
        return TEXTURE;
    }
}