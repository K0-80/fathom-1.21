package com.k080.fathom.util;

import com.k080.fathom.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;

public class ModLootTableModifiers {

    private static final Identifier ELDER_GUARDIAN_ID = Identifier.of("minecraft", "entities/elder_guardian");

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, wrapperLookup) -> {
            if (source.isBuiltin() && ELDER_GUARDIAN_ID.equals(key.getValue())) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .conditionally(RandomChanceLootCondition.builder(0.5f))
                        .with(ItemEntry.builder(ModItems.GUARDIAN_HEART));

                tableBuilder.pool(poolBuilder);
            }
        });
    }
}