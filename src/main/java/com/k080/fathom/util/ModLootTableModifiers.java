package com.k080.fathom.util;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;

public class ModLootTableModifiers {

    public static void replaceLootTables() {
        LootTableEvents.REPLACE.register((key, tableBuilder, source, registry) -> {

            // We only want to modify the FISH part of the fishing loot table
            if (LootTables.FISHING_GAMEPLAY.equals(key)) {

                // --- Pool 1: General Overworld Surface (Y > 50) ---
                LootPool.Builder overworldSurfacePool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .conditionally(LocationCheckLootCondition.builder(
                                LocationPredicate.Builder.create()
                                        .dimension(World.OVERWORLD)
                                        .y(NumberRange.DoubleRange.atLeast(50))
                        ))
                        .with(ItemEntry.builder(Items.COD).weight(5))
                        .with(ItemEntry.builder(Items.SALMON).weight(4))
                        .with(ItemEntry.builder(Items.PUFFERFISH).weight(2))
                        .with(ItemEntry.builder(Items.INK_SAC).weight(2));

                // --- Pool 2: Overworld Caves (Y <= 50) ---
                LootPool.Builder overworldCavePool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .conditionally(LocationCheckLootCondition.builder(
                                LocationPredicate.Builder.create()
                                        .dimension(World.OVERWORLD)
                                        .y(NumberRange.DoubleRange.atMost(50))
                        ))
                        .with(ItemEntry.builder(Items.BONE).weight(20))
                        .with(ItemEntry.builder(Items.COAL).weight(10))
                        .with(ItemEntry.builder(Items.RAW_IRON).weight(5));


                // Jungle
                LootPool.Builder overworldJunglePool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .conditionally(LocationCheckLootCondition.builder(
                                LocationPredicate.Builder.create()
                                        .dimension(World.OVERWORLD)
                        ))
                        .with(ItemEntry.builder(Items.TROPICAL_FISH).weight(15))
                        .with(ItemEntry.builder(Items.COCOA_BEANS).weight(8))
                        .with(ItemEntry.builder(Items.VINE).weight(5))
                        .with(ItemEntry.builder(Items.MELON_SLICE).weight(5));


                //  Nether pool
                LootPool.Builder netherPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .conditionally(LocationCheckLootCondition.builder(
                                LocationPredicate.Builder.create().dimension(World.NETHER)
                        ))
                        .with(ItemEntry.builder(Items.MAGMA_CREAM).weight(10))
                        .with(ItemEntry.builder(Items.GHAST_TEAR).weight(1))
                        .with(ItemEntry.builder(Items.QUARTZ).weight(8));

                // End pool
                LootPool.Builder endPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .conditionally(LocationCheckLootCondition.builder(
                                LocationPredicate.Builder.create().dimension(World.END)
                        ))
                        .with(ItemEntry.builder(Items.ENDER_PEARL).weight(10))
                        .with(ItemEntry.builder(Items.CHORUS_FRUIT).weight(8))
                        .with(ItemEntry.builder(Items.END_STONE).weight(5));


                return LootTable.builder()
                        .pool(overworldSurfacePool)
                        .pool(overworldCavePool)
                        .pool(netherPool)
                        .pool(endPool)
                        .build();
            }
            return tableBuilder;
        });
    }
}