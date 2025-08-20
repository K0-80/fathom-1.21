package com.k080.fathom.entity.client;

import com.k080.fathom.Fathom;
import com.k080.fathom.entity.client.model.CreakingEyeModel;
import com.k080.fathom.entity.custom.CreakingEyeEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CreakingEyeRenderer extends MobEntityRenderer<CreakingEyeEntity, CreakingEyeModel<CreakingEyeEntity>> {
    private static final Identifier TEXTURE = Identifier.of(Fathom.MOD_ID, "textures/entity/creaking_eye.png");

    public CreakingEyeRenderer(EntityRendererFactory.Context context) {
        super(context, new CreakingEyeModel<>(context.getPart(CreakingEyeModel.MODEL_LAYER)), 1f);
    }

    @Override
    public Identifier getTexture(CreakingEyeEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(CreakingEyeEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.translate(0.1, 0.3f, 0);
        matrixStack.scale(3.0f, 3.0f, 3.0f);
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }
}