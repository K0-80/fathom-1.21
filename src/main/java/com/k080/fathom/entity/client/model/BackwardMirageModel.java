package com.k080.fathom.entity.client.model;

import com.k080.fathom.Fathom;
import com.k080.fathom.entity.custom.MirageModelEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BackwardMirageModel extends EntityModel<MirageModelEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(Identifier.of(Fathom.MOD_ID, "backward_mirage"), "main");
    private final ModelPart Head;
    private final ModelPart Body;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;

    public BackwardMirageModel(ModelPart root) {
        this.Head = root.getChild("Head");
        this.Body = root.getChild("Body");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData Head = modelPartData.addChild("Head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData HatLayer_r1 = Head.addChild("HatLayer_r1", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -15.0174F, -1.1057F, 8.0F, 8.0F, 8.0F, new Dilation(0.5F))
                .uv(0, 0).cuboid(-4.0F, -15.0174F, -1.1057F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 7.6279F, 1.5527F, 0.3927F, 0.0F, 0.0F));

        ModelPartData Body = modelPartData.addChild("Body", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F));

        ModelPartData BodyLayer_r1 = Body.addChild("BodyLayer_r1", ModelPartBuilder.create().uv(16, 32).cuboid(-4.0F, -7.0135F, -2.6123F, 8.0F, 12.0F, 4.0F, new Dilation(0.25F))
                .uv(16, 16).cuboid(-4.0F, -7.0135F, -2.6123F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 7.6279F, 1.5527F, -0.0873F, 0.0F, 0.0F));

        ModelPartData RightArm = modelPartData.addChild("RightArm", ModelPartBuilder.create(), ModelTransform.of(-5.0F, 4.0F, 1.0F, 0.3491F, 0.0F, 0.0F));

        ModelPartData RightArmLayer_r1 = RightArm.addChild("RightArmLayer_r1", ModelPartBuilder.create().uv(39, 32).cuboid(-2.5524F, -6.5991F, -3.9074F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F))
                .uv(39, 16).cuboid(-2.5524F, -6.5991F, -3.9074F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-1.25F, 4.5232F, 3.1366F, -0.0435F, 0.0038F, 0.0872F));

        ModelPartData LeftArm = modelPartData.addChild("LeftArm", ModelPartBuilder.create(), ModelTransform.of(5.0F, 4.0F, 1.0F, 0.5236F, 0.0F, 0.0F));

        ModelPartData LeftArmLayer_r1 = LeftArm.addChild("LeftArmLayer_r1", ModelPartBuilder.create().uv(48, 48).cuboid(-1.4263F, -6.842F, -3.8126F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F))
                .uv(32, 48).cuboid(-1.4263F, -6.842F, -3.8126F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(1.25F, 4.49F, 2.7507F, -0.0869F, -0.0076F, -0.0869F));

        ModelPartData RightLeg = modelPartData.addChild("RightLeg", ModelPartBuilder.create(), ModelTransform.pivot(-1.9F, 12.0F, 0.0F));

        ModelPartData RightLegLayer_r1 = RightLeg.addChild("RightLegLayer_r1", ModelPartBuilder.create().uv(0, 32).cuboid(-4.4F, 3.5247F, -6.5872F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F))
                .uv(0, 16).cuboid(-4.4F, 3.5247F, -6.5872F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(1.9F, -4.3721F, -0.6973F, 0.3927F, 0.0F, 0.0F));

        ModelPartData LeftLeg = modelPartData.addChild("LeftLeg", ModelPartBuilder.create(), ModelTransform.pivot(1.9F, 12.0F, 0.0F));

        ModelPartData LeftLegLayer_r1 = LeftLeg.addChild("LeftLegLayer_r1", ModelPartBuilder.create().uv(0, 48).cuboid(0.4F, 5.467F, -3.7925F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F))
                .uv(16, 48).cuboid(0.4F, 5.467F, -3.7925F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-1.9F, -4.3721F, 1.5527F, 0.0436F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(MirageModelEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.RightArm.roll = MathHelper.cos(animationProgress * 0.15F) * 0.1F;
        this.LeftArm.roll = -MathHelper.cos(animationProgress * 0.15F) * 0.1F;

        this.RightLeg.roll = -MathHelper.cos(animationProgress * 0.15F) * 0.05F;
        this.LeftLeg.roll = MathHelper.cos(animationProgress * 0.15F) * 0.05F;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        Head.render(matrices, vertices, light, overlay, color);
        Body.render(matrices, vertices, light, overlay, color);
        RightArm.render(matrices, vertices, light, overlay, color);
        LeftArm.render(matrices, vertices, light, overlay, color);
        RightLeg.render(matrices, vertices, light, overlay, color);
        LeftLeg.render(matrices, vertices, light, overlay, color);
    }
}