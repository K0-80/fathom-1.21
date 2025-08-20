package com.k080.fathom.entity.client.model;

import com.k080.fathom.Fathom;
import com.k080.fathom.entity.custom.SkeletonFishEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class SkeletonFishModel<T extends SkeletonFishEntity> extends SinglePartEntityModel<T> {

    public static final EntityModelLayer SKELETON_FISH = new EntityModelLayer(Identifier.of(Fathom.MOD_ID, "skeleton_fish"), "main");

    private final ModelPart skeleton_fish;
    private final ModelPart tail;
    private final ModelPart body;

    public SkeletonFishModel(ModelPart root) {
        this.skeleton_fish = root.getChild("skeleton_fish");
        this.tail = this.skeleton_fish.getChild("tail");
        this.body = this.skeleton_fish.getChild("body");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData skeleton_fish = modelPartData.addChild("skeleton_fish", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 22.0F, 0.95F));

        ModelPartData tail = skeleton_fish.addChild("tail", ModelPartBuilder.create().uv(10, 10).cuboid(-0.0241F, -2.5F, 3.0189F, 0.0F, 5.0F, 4.0F, new Dilation(0.0F))
                .uv(18, 10).cuboid(-1.0241F, -2.0F, 1.0689F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(3, 5).cuboid(-0.5241F, -0.5F, 2.0689F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(8, 8).cuboid(-0.5F, -0.5F, 0.05F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData body = skeleton_fish.addChild("body", ModelPartBuilder.create().uv(0, 17).cuboid(-1.0F, -2.0F, -2.5F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(3, 5).cuboid(-0.5F, -0.5F, -3.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(8, 8).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -2.45F));

        ModelPartData head = body.addChild("head", ModelPartBuilder.create().uv(0, 10).cuboid(-1.0F, -2.0F, -2.5F, 2.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -4.0F));

        ModelPartData bone = skeleton_fish.addChild("bone", ModelPartBuilder.create().uv(18, 15).cuboid(-1.0F, -2.0F, 0.5F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(18, 15).cuboid(-1.0F, -2.0F, -1.5F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(8, 8).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -1.45F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);

        if (entity.isTouchingWater()) {

            float wiggleAngle = MathHelper.sin(animationProgress * 0.4F) * 0.15F;
            this.body.yaw = wiggleAngle;
            this.tail.yaw = wiggleAngle * 1.5F;

            this.body.yaw = MathHelper.sin(animationProgress * 0.2F) * 0.1F;

        } else {

            this.skeleton_fish.roll = MathHelper.PI / 2.0F;

            float wiggleAngle = MathHelper.sin(animationProgress * 0.7F) * 0.4F;

            this.body.yaw = wiggleAngle;
            this.tail.yaw = wiggleAngle * 1.3F;
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int colour) {
        skeleton_fish.render(matrices, vertexConsumer, light, overlay, colour);
    }
    @Override
    public ModelPart getPart() {
        return skeleton_fish;
    }
}