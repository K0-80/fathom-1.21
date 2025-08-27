package com.k080.fathom.entity;

import com.k080.fathom.Fathom;
import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.entity.client.ShockwaveBlockEntity;
import com.k080.fathom.entity.custom.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<SkeletonFishEntity> SKELETON_FISH = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(Fathom.MOD_ID, "skeleton_fish"),
            EntityType.Builder.create(SkeletonFishEntity::new, SpawnGroup.WATER_AMBIENT)
                    .dimensions(0.5F, 0.3F).eyeHeight(0.195f).maxTrackingRange(4).build());

    public static final EntityType<AnchorProjectileEntity> ANCHOR_PROJECTILE = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(Fathom.MOD_ID, "anchor"),
            EntityType.Builder.create(AnchorProjectileEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F).maxTrackingRange(8).build());

    public static final EntityType<MirageModelEntity> MIRAGE_MODEL = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Fathom.MOD_ID, "mirage_model"),
            EntityType.Builder.create(MirageModelEntity::new, SpawnGroup.MISC)
                    .dimensions(0.6f, 1.8f).build());

    public static final EntityType<AmethystShardProjectileEntity> AMETHYST_SHARD_PROJECTILE = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(Fathom.MOD_ID, "amethyst_shard_projectile"),
            EntityType.Builder.<AmethystShardProjectileEntity>create(AmethystShardProjectileEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(10).build());

//    public static final EntityType<MirageThrowEntity> MIRAGE_THROW_ENTITY_ENTITY_TYPE = Registry.register(Registries.ENTITY_TYPE,
//            Identifier.of(Fathom.MOD_ID, "mirage_throw"),
//            EntityType.Builder.create(MirageThrowEntity::new, SpawnGroup.MISC)
//                    .dimensions(0.25F, 0.25F).maxTrackingRange(6).build());

    public static final EntityType<SpiritEntity> SPIRIT  = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(Fathom.MOD_ID, "spirit"),
            EntityType.Builder.create(SpiritEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f).maxTrackingRange(8).trackingTickInterval(2).build());

    public static final EntityType<CreakingEyeEntity> CREAKING_EYE  = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(Fathom.MOD_ID, "creaking_eye"),
            EntityType.Builder.create(CreakingEyeEntity::new, SpawnGroup.MISC)
                    .dimensions(1.5f, 1.5f).maxTrackingRange(8).trackingTickInterval(2).build());


    public static final EntityType<ShockwaveBlockEntity> SHOCKWAVE_BLOCK = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(Fathom.MOD_ID, "shockwave_block"),
            EntityType.Builder.create(ShockwaveBlockEntity::new, SpawnGroup.MISC)
                    .dimensions(0.1f, 0.1f).disableSaving().makeFireImmune().build()
    );

    public static void registerModEntities() {
        Fathom.LOGGER.info("Registering Entity's for " + Fathom.MOD_ID);
        FabricDefaultAttributeRegistry.register(ModEntities.SKELETON_FISH, SkeletonFishEntity.createSkeletonFishAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.SPIRIT, SpiritEntity.createSpiritAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.CREAKING_EYE, CreakingEyeEntity.createCreakingEyeAttributes());

    }
}