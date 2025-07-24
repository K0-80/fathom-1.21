package com.k080.fathom.entity;

import com.k080.fathom.Fathom;
import com.k080.fathom.entity.custom.AnchorProjectileEntity;
import com.k080.fathom.entity.custom.PlayerCloneEntity;
import com.k080.fathom.entity.custom.SkeletonFishEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
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
            EntityType.Builder.create(AnchorProjectileEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.5F, 1F)
                    .maxTrackingRange(8).build());

    public static final EntityType<PlayerCloneEntity> PLAYER_CLONE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Fathom.MOD_ID, "player_clone"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, PlayerCloneEntity::new)
                    .dimensions(EntityDimensions.fixed(0.7f, 1.9f)).build()
    );


    public static void registerModEntities() {
        Fathom.LOGGER.info("Registering Entity's for " + Fathom.MOD_ID);
        FabricDefaultAttributeRegistry.register(ModEntities.SKELETON_FISH, SkeletonFishEntity.createSkeletonFishAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.PLAYER_CLONE, PlayerCloneEntity.createPlayerCloneAttributes());
    }
}