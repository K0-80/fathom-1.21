package com.k080.fathom.entity.client;


import com.k080.fathom.entity.client.ShockwaveBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ShockwaveBlockEntityRenderer extends EntityRenderer<ShockwaveBlockEntity> {
    private final BlockRenderManager blockRenderManager;

    public ShockwaveBlockEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public void render(ShockwaveBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        if (entity.age == 0) {
            return;
        }

        matrices.push();
        float staticYOffset = -1.0f + 0.01f;
        float animatedYOffset = entity.getVerticalOffset(tickDelta);

//        matrices.scale(0.99f, 0.99f, 0.99f);
        matrices.translate(-0.5, staticYOffset + animatedYOffset, -0.5);

        this.blockRenderManager.renderBlockAsEntity(
                entity.getBlockState(),
                matrices,
                vertexConsumers,
                light,
                OverlayTexture.DEFAULT_UV
        );

        matrices.pop();
    }

    @Override
    public Identifier getTexture(ShockwaveBlockEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
