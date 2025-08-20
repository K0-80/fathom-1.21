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

public class ForwardMirageModel extends EntityModel<MirageModelEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(Identifier.of(Fathom.MOD_ID, "forward_mirage"), "main");
    private final ModelPart Head;
    private final ModelPart Body;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    public ForwardMirageModel(ModelPart root) {
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
        ModelPartData Head = modelPartData.addChild("Head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.25F, 0.0F));

        ModelPartData HatLayer_r1 = Head.addChild("HatLayer_r1", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -15.0174F, -1.1057F, 8.0F, 8.0F, 8.0F, new Dilation(0.5F))
                .uv(0, 0).cuboid(-4.0F, -15.0174F, -1.1057F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 7.3779F, -0.6973F, 0.5236F, 0.0F, 0.0F));

        ModelPartData Body = modelPartData.addChild("Body", ModelPartBuilder.create(), ModelTransform.of(0.0F, 6.5235F, 3.1257F, 0.2182F, 0.0F, 0.0F));

        ModelPartData BodyLayer_r1 = Body.addChild("BodyLayer_r1", ModelPartBuilder.create().uv(16, 32).cuboid(-4.0F, -8.1615F, -5.3839F, 8.0F, 12.0F, 4.0F, new Dilation(0.25F))
                .uv(16, 16).cuboid(-4.0F, -8.1615F, -5.3839F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.1044F, 0.427F, 0.0436F, 0.0F, 0.0F));

        ModelPartData RightArm = modelPartData.addChild("RightArm", ModelPartBuilder.create(), ModelTransform.of(-6.7481F, 7.1351F, 0.485F, -0.5672F, 0.0F, 0.0F));

        ModelPartData RightArmLayer_r1 = RightArm.addChild("RightArmLayer_r1", ModelPartBuilder.create().uv(39, 32).cuboid(-1.689F, -0.5885F, -1.8999F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F))
                .uv(39, 16).cuboid(-1.689F, -0.5885F, -1.8999F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.1209F, -3.6731F, 1.1765F, -0.0774F, 0.0404F));

        ModelPartData LeftArm = modelPartData.addChild("LeftArm", ModelPartBuilder.create(), ModelTransform.of(6.7481F, 6.4721F, 0.6122F, -0.3927F, 0.0F, 0.0F));

        ModelPartData LeftArmLayer_r1 = LeftArm.addChild("LeftArmLayer_r1", ModelPartBuilder.create().uv(48, 48).cuboid(-1.5616F, -5.2956F, -1.2929F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F))
                .uv(32, 48).cuboid(-1.5616F, -5.2956F, -1.2929F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-0.4981F, 0.0429F, -0.0076F, 1.0455F, 0.0756F, -0.0437F));

        ModelPartData RightLeg = modelPartData.addChild("RightLeg", ModelPartBuilder.create(), ModelTransform.pivot(-1.9F, 12.0F, 0.0F));

        ModelPartData RightLegLayer_r1 = RightLeg.addChild("RightLegLayer_r1", ModelPartBuilder.create().uv(0, 32).cuboid(-4.4F, 3.5247F, -6.5872F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F))
                .uv(0, 16).cuboid(-4.4F, 3.5247F, -6.5872F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(1.9F, -4.8721F, -1.9473F, 0.48F, 0.0F, 0.0F));

        ModelPartData LeftLeg = modelPartData.addChild("LeftLeg", ModelPartBuilder.create(), ModelTransform.pivot(1.9F, 12.0F, 0.0F));

        ModelPartData LeftLegLayer_r1 = LeftLeg.addChild("LeftLegLayer_r1", ModelPartBuilder.create().uv(0, 48).cuboid(0.4F, 5.467F, -3.7925F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F))
                .uv(16, 48).cuboid(0.4F, 5.467F, -3.7925F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-1.9F, -4.3721F, 2.5527F, -0.0436F, 0.0F, 0.0F));
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