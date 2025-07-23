package com.k080.fathom.entity.client;

import com.k080.fathom.entity.custom.PlayerCloneEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.model.BipedEntityModel;

public class PlayerCloneEntityRenderer extends BipedEntityRenderer<PlayerCloneEntity, PlayerEntityModel<PlayerCloneEntity>> {

    public PlayerCloneEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PlayerEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER), false), 0.5f);
        this.addFeature(new ArmorFeatureRenderer<>(this, new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)), new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)), ctx.getModelManager()));
        this.addFeature(new HeldItemFeatureRenderer<>(this, ctx.getHeldItemRenderer()));
    }

    @Override
    public Identifier getTexture(PlayerCloneEntity entity) {
        return DefaultSkinHelper.getSkinTextures(entity.getOwnerProfile()).texture();
    }
    @Override
    public void render(PlayerCloneEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light) {
        matrixStack.push();
        matrixStack.scale(0.9f, 0.9f, 0.9f);
        super.render(entity, entityYaw, partialTicks, matrixStack, vertexConsumers, light);
        matrixStack.pop();
    }
}