package com.k080.fathom.entity;

import com.k080.fathom.Fathom;
import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.entity.block.AmethystResonatorBlockEntity;
import com.k080.fathom.entity.block.BloodCrucibleBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntitys {

    public static final BlockEntityType<BloodCrucibleBlockEntity> BLOOD_CRUCIBLE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Fathom.MOD_ID, "blood_crucible_block_entity"),
            BlockEntityType.Builder.create(BloodCrucibleBlockEntity::new, ModBlocks.BlOOD_CRUCIBLE).build(null));

    public static final BlockEntityType<AmethystResonatorBlockEntity> AMETHYST_RESONATOR_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Fathom.MOD_ID, "amethyst_resonator_block_entity"),
            BlockEntityType.Builder.create(AmethystResonatorBlockEntity::new, ModBlocks.AMETHYST_RESONATOR).build(null));

    public static void registerModEntities() {
        Fathom.LOGGER.info("Registering Block Entity's for " + Fathom.MOD_ID);
    }
}
