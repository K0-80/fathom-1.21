package com.k080.fathom.client;

import com.k080.fathom.Fathom;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import com.k080.fathom.util.WindGlowAccessor;

public class WindGlowFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    private static final Identifier WIND_GLOW_TEXTURE = Identifier.of(Fathom.MOD_ID, "textures/effect/wind_glow.png");

    public WindGlowFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!((WindGlowAccessor)entity).getWindGlow()) {
            return;
        }
        float time = entity.age + tickDelta;
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEnergySwirl(WIND_GLOW_TEXTURE, time * 0.005f, 0));
        this.getContextModel().render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 0x80FFFFFF);
    }
}