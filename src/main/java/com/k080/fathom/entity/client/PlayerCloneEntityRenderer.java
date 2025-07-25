package com.k080.fathom.entity.client;

import com.k080.fathom.entity.custom.PlayerCloneEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class PlayerCloneEntityRenderer extends EntityRenderer<PlayerCloneEntity> {

    private final BipedEntityRenderer<PlayerCloneEntity, PlayerEntityModel<PlayerCloneEntity>> wideRenderer;
    private final BipedEntityRenderer<PlayerCloneEntity, PlayerEntityModel<PlayerCloneEntity>> slimRenderer;

    public PlayerCloneEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.5f;
        this.wideRenderer = new InternalRenderer(ctx, false);
        this.slimRenderer = new InternalRenderer(ctx, true);
    }

    @Override
    public Identifier getTexture(PlayerCloneEntity entity) {
        return DefaultSkinHelper.getSkinTextures(entity.getOwnerProfile()).texture();
    }

    @Override
    public void render(PlayerCloneEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light) {
        var skinTextures = DefaultSkinHelper.getSkinTextures(entity.getOwnerProfile());
        boolean isSlim = skinTextures.model() == SkinTextures.Model.SLIM;

        BipedEntityRenderer<PlayerCloneEntity, PlayerEntityModel<PlayerCloneEntity>> renderer = isSlim ? this.slimRenderer : this.wideRenderer;

        matrixStack.push();
        matrixStack.scale(0.95f, 0.95f, 0.95f);
        renderer.render(entity, entityYaw, partialTicks, matrixStack, vertexConsumers, light);
        matrixStack.pop();
    }

    private static class InternalRenderer extends BipedEntityRenderer<PlayerCloneEntity, PlayerEntityModel<PlayerCloneEntity>> {
        public InternalRenderer(EntityRendererFactory.Context ctx, boolean slim) {
            super(ctx, new PlayerEntityModel<>(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), slim), 0.5f);
            this.addFeature(new ArmorFeatureRenderer<>(this,
                    new BipedEntityModel<>(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM_INNER_ARMOR : EntityModelLayers.PLAYER_INNER_ARMOR)),
                    new BipedEntityModel<>(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR : EntityModelLayers.PLAYER_OUTER_ARMOR)),
                    ctx.getModelManager()));
            this.addFeature(new HeldItemFeatureRenderer<>(this, ctx.getHeldItemRenderer()));
        }

        @Override
        public Identifier getTexture(PlayerCloneEntity entity) {
            return DefaultSkinHelper.getSkinTextures(entity.getOwnerProfile()).texture();
        }
    }
}