package com.k080.fathom.entity.client;

import com.k080.fathom.Fathom;
import com.k080.fathom.entity.custom.CreakingEyeEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CreakingEyeModel<T extends CreakingEyeEntity> extends EntityModel<T> {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Identifier.of(Fathom.MOD_ID, "creaking_eye"), "main");
    private final ModelPart bb_main;

    public CreakingEyeModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-14.0F, -14.0F, 0.5F, 29.0F, 12.0F, 0.0F, new Dilation(0.0F))
                .uv(32, 31).cuboid(-4.0F, -7.5F, -3.5F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 13).cuboid(-4.5F, -8.0F, -4.0F, 9.0F, 9.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.bb_main.yaw = netHeadYaw * ((float)Math.PI / 180F);
        this.bb_main.pitch = headPitch * ((float)Math.PI / 180F);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        bb_main.render(matrices, vertexConsumer, light, overlay, color);
    }
}