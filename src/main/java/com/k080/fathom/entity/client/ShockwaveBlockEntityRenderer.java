package com.k080.fathom.entity.client;


import com.k080.fathom.entity.block.ShockwaveBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

        BlockState blockState = entity.getBlockState();
        BlockPos blockPos = entity.getBlockPos();
        World world = entity.getWorld();

        matrices.push();
        float staticYOffset = -1.0f - 0.001f;
        float animatedYOffset = entity.getVerticalOffset(tickDelta);

        matrices.translate(-0.5, staticYOffset + animatedYOffset, -0.5);
        matrices.scale(0.001f, 0.001f, 0.0001f);

        this.blockRenderManager.renderBlock(
                blockState,
                blockPos,
                world,
                matrices,
                vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(blockState)),
                false,
                world.getRandom()
        );

        matrices.pop();
    }

    @Override
    public Identifier getTexture(ShockwaveBlockEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}