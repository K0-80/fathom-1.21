package com.k080.fathom.client;

import com.k080.fathom.Fathom;
import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.client.hud.CreakingStaffHud;
import com.k080.fathom.client.hud.SoulJarHud;
import com.k080.fathom.component.ModComponents;
import com.k080.fathom.entity.ModEntities;
import com.k080.fathom.entity.client.*;
import com.k080.fathom.item.ModItems;
import com.k080.fathom.particle.ModParticles;
import com.k080.fathom.particle.custom.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FathomModClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(Fathom.MOD_ID);

    @Override
    public void onInitializeClient() {

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BlOOD_CRUCIBLE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.AMETHYST_RESONATOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ANCHOR_BLOCK_ACTIVATED, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ANCHOR_BLOCK_INACTIVE, RenderLayer.getCutout());

        HudRenderCallback.EVENT.register(new SoulJarHud());
        HudRenderCallback.EVENT.register(new CreakingStaffHud());

        EntityModelLayerRegistry.registerModelLayer(SkeletonFishModel.SKELETON_FISH, SkeletonFishModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.SKELETON_FISH, SkeletonFishRender:: new);

        EntityModelLayerRegistry.registerModelLayer(SpiritEntityModel.SPIRIT, SpiritEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.SPIRIT, SpiritEntityRenderer::new);

        EntityRendererRegistry.register(ModEntities.ANCHOR_PROJECTILE, AnchorProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.PLAYER_CLONE, PlayerCloneEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.MIRAGE_THROW_ENTITY_ENTITY_TYPE, MirageThrowEntityRender::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.WIND_PARTICLE, WindParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.MARKED_PARTICLE, MarkedParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.ANCHORED_PARTICLE, AnchoredParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.RAPTURE_PARTICLE, RaptureParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.FLOWSTATE_PARTICLE, FlowstateParticle.Factory::new);

        ModelPredicateProviderRegistry.register(ModItems.WIND_BLADE, Identifier.of(Fathom.MOD_ID, "charge"),  //wind blade charge change model
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
