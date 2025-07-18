package com.k080.fathom.client;

import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.entity.ModEntities;
import com.k080.fathom.entity.client.SkeletonFishModel;
import com.k080.fathom.entity.client.SkeletonFishRender;
import com.k080.fathom.entity.client.AnchorProjectileRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.RenderLayer;

public class FathomModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DRIFTWOOD_SAPLING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DRIFTWOOD_LEAVES, RenderLayer.getCutout());

        HudRenderCallback.EVENT.register(new StunnedOverlayRender());

        EntityModelLayerRegistry.registerModelLayer(SkeletonFishModel.SKELETON_FISH, SkeletonFishModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.SKELETON_FISH, SkeletonFishRender:: new);

        EntityRendererRegistry.register(ModEntities.ANCHOR_PROJECTILE, AnchorProjectileRenderer::new);
    }
}
