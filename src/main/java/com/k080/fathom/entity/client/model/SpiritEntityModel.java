package com.k080.fathom.entity.client.model;

import com.k080.fathom.Fathom;
import com.k080.fathom.entity.custom.SpiritEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SpiritEntityModel extends EntityModel<SpiritEntity> {
    private final ModelPart base;
    public static final EntityModelLayer SPIRIT = new EntityModelLayer(Identifier.of(Fathom.MOD_ID, "spirit"), "main");


    public SpiritEntityModel(ModelPart root) {
        this.base = root.getChild("base");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.pivot(0.0F, 20.0F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(SpiritEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        base.render(matrices, vertices, light, overlay, color);
    }
}