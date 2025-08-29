package com.k080.fathom.client;

import com.k080.fathom.Fathom;
import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.client.handler.ScytheSoulEffectHandler;
import com.k080.fathom.client.hud.CreakingStaffHud;
import com.k080.fathom.client.hud.SoulJarHud;
import com.k080.fathom.client.renderer.TrailManager;
import com.k080.fathom.component.ModComponents;
import com.k080.fathom.entity.ModEntities;
import com.k080.fathom.entity.client.*;
import com.k080.fathom.entity.client.feature.AmethystShardsFeatureRenderer;
import com.k080.fathom.entity.client.model.*;
import com.k080.fathom.item.ModItems;
import com.k080.fathom.particle.ModParticles;
import com.k080.fathom.particle.custom.*;
import com.k080.fathom.util.ModModelPredicateProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class FathomModClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(Fathom.MOD_ID);

    @Override
    public void onInitializeClient() {

        ModelPredicateProviderRegistry.register(ModItems.ANCHOR, Identifier.of(Fathom.MOD_ID, "thrown"),
                (stack, world, entity, seed) -> stack.contains(ModComponents.THROWN_ANCHOR_UUID) ? 1.0f : 0.0f);

        EntityRendererRegistry.register(ModEntities.SHOCKWAVE_BLOCK, ShockwaveBlockEntityRenderer::new);

        WorldRenderEvents.AFTER_ENTITIES.register(TrailManager::render);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            TrailManager.tick();
            ScytheSoulEffectHandler.tick(client);
        });

        ModModelPredicateProvider.registerModModels();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BlOOD_CRUCIBLE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.AMETHYST_RESONATOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ANCHOR_BLOCK_ACTIVATED, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ANCHOR_BLOCK_INACTIVE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CREAKING_VINE, RenderLayer.getCutout());

        HudRenderCallback.EVENT.register(new SoulJarHud());
        HudRenderCallback.EVENT.register(new CreakingStaffHud());

        EntityModelLayerRegistry.registerModelLayer(SkeletonFishModel.SKELETON_FISH, SkeletonFishModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.SKELETON_FISH, SkeletonFishRender:: new);

        EntityModelLayerRegistry.registerModelLayer(SpiritEntityModel.SPIRIT, SpiritEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.SPIRIT, SpiritEntityRenderer::new);

        EntityRendererRegistry.register(ModEntities.CREAKING_EYE, CreakingEyeRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(CreakingEyeModel.MODEL_LAYER, CreakingEyeModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.ANCHOR_PROJECTILE, AnchorProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.MIRAGE_MODEL, MirageModelEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ForwardMirageModel.LAYER, ForwardMirageModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(BackwardMirageModel.LAYER, BackwardMirageModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.AMETHYST_SHARD_PROJECTILE, AmethystShardProjectileEntityRenderer::new);
//        EntityRendererRegistry.register(ModEntities.MIRAGE_THROW_ENTITY_ENTITY_TYPE, MirageThrowEntityRender::new);

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof PlayerEntityRenderer) {
                registrationHelper.register(new AmethystShardsFeatureRenderer((PlayerEntityRenderer)entityRenderer, context.getItemRenderer()));
            }
        });


        ParticleFactoryRegistry.getInstance().register(ModParticles.WIND_PARTICLE, WindParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.MARKED_PARTICLE, MarkedParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.ANCHORED_PARTICLE, AnchoredParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.RAPTURE_PARTICLE, RaptureParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.FLOWSTATE_PARTICLE, FlowstateParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SCYTHE_SWEEP, ScytheSweepParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SCYTHE_CRIT, CritSweepParticle.Factory::new);

        //wind blade charge change model
        ModelPredicateProviderRegistry.register(ModItems.WIND_BLADE, Identifier.of(Fathom.MOD_ID, "charge"),
                (stack, world, entity, seed) -> {
                    if (entity == null || !entity.isUsingItem() || entity.getActiveItem() != stack) {return 0.0f;}

                    float chargeTime = stack.getItem().getMaxUseTime(stack, entity);
                    float timeUsed = chargeTime - entity.getItemUseTimeLeft();
                    return timeUsed / chargeTime;
                });
        //creaking staff change model based on watching or not
        ModelPredicateProviderRegistry.register(ModItems.CREAKING_STAFF, Identifier.of("fathom", "watched"),
                (stack, world, entity, seed) -> stack.getOrDefault(ModComponents.IS_WATCHED, false) ? 1.0f : 0.0f);

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            ModComponents.MendingTarget component = stack.get(ModComponents.MENDING_TARGET);
            if (component != null) {
                lines.add(1, Text.translatable("tooltip.fathom.mending_slate.status", component.remainingRepair())
                        .formatted(Formatting.GRAY));
            }
        });
    }
}

