package com.k080.fathom.client;

import com.k080.fathom.Fathom;
import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.entity.ModEntities;
import com.k080.fathom.entity.client.*;
import com.k080.fathom.item.ModItems;
import com.k080.fathom.particle.ModParticles;
import com.k080.fathom.particle.custom.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.client.render.entity.EntityRenderers; // This is the import you need


public class FathomModClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(Fathom.MOD_ID);

    @Override
    public void onInitializeClient() {

        EntityModelLayerRegistry.registerModelLayer(SkeletonFishModel.SKELETON_FISH, SkeletonFishModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.SKELETON_FISH, SkeletonFishRender:: new);

        EntityRendererRegistry.register(ModEntities.ANCHOR_PROJECTILE, AnchorProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.PLAYER_CLONE, PlayerCloneEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.MIRAGE_THROW_ENTITY_ENTITY_TYPE, MirageThrowEntityRender::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.WIND_PARTICLE, WindParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.MARKED_PARTICLE, MarkedParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.ANCHORED_PARTICLE, AnchoredParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.RAPTURE_PARTICLE, RaptureParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.FLOWSTATE_PARTICLE, FlowstateParticle.Factory::new);

        ModelPredicateProviderRegistry.register(ModItems.WIND_BLADE, Identifier.of(Fathom.MOD_ID, "charge"),
                (stack, world, entity, seed) -> {
                    if (entity == null || !entity.isUsingItem() || entity.getActiveItem() != stack) {return 0.0f;}

                    float chargeTime = stack.getItem().getMaxUseTime(stack, entity);
                    float timeUsed = chargeTime - entity.getItemUseTimeLeft();
                    return timeUsed / chargeTime;
                });
    }
}
