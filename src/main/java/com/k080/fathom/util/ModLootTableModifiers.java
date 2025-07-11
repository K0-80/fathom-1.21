package com.k080.fathom.util;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.world.World;

public class ModLootTableModifiers {

    public static void replaceLootTables() {
        LootTableEvents.REPLACE.register((key, tableBuilder, source, registry) -> {

            if(LootTables.FISHING_FISH_GAMEPLAY.equals(key)) {

                LootPool.Builder riverPoolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(ItemEntry.builder(Items.COD).weight(5))
                        .with(ItemEntry.builder(Items.SALMON).weight(5))
                        .with(ItemEntry.builder(Items.PUFFERFISH).weight(2))
                        .with(ItemEntry.builder(Items.INK_SAC).weight(2))
                        .with(
                                ItemEntry.builder(Items.MAGMA_CREAM).weight(80)
                                        .conditionally(LocationCheckLootCondition.builder(
                                                LocationPredicate.Builder.createDimension(World.NETHER)
                                        )))
                        .with(
                                ItemEntry.builder(Items.ENDER_PEARL).weight(80)
                                        .conditionally(LocationCheckLootCondition.builder(
                                                LocationPredicate.Builder.createDimension(World.END)
                                        )));


                return LootTable.builder().pool(riverPoolBuilder).build();
            }

            return tableBuilder;
        });
    }
}