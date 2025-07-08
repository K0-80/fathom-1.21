package com.k080.fathom.datagen;

import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.MITHRIL_BLOCK);
        addDrop(ModBlocks.RAW_MITHRIL_BLOCK);

        addDrop(ModBlocks.MITHRIL_ORE, oreDrops(ModBlocks.MITHRIL_ORE, ModItems.RAW_MITHRIL));
        addDrop(ModBlocks.DEEPSLATE_MITHRIL_ORE, multipleOreDrops(ModBlocks.DEEPSLATE_MITHRIL_ORE, ModItems.RAW_MITHRIL, 2, 3));

        addDrop(ModBlocks.DRIFTWOO_LOG);
        addDrop(ModBlocks.STRIPED_DRIFTWOOD_LOG);
        addDrop(ModBlocks.DRIFTWOOD_WOOD);
        addDrop(ModBlocks.STRIPED_DRIFTWOOD_WOOD);
        addDrop(ModBlocks.DRIFTWOOD_PLANK);
        addDrop(ModBlocks.DRIFTWOOD_SAPLING);
        addDrop(ModBlocks.DRIFTWOOD_LEAVES, leavesDrops(ModBlocks.DRIFTWOOD_LEAVES, ModBlocks.DRIFTWOOD_SAPLING, 0.0625f));
    }

    public LootTable.Builder multipleOreDrops(Block drop, Item item, float minDrops, float maxDrops) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return this.dropsWithSilkTouch(drop, this.applyExplosionDecay(drop, ((LeafEntry.Builder<?>)
                ItemEntry.builder(item).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(minDrops, maxDrops))))
                .apply(ApplyBonusLootFunction.oreDrops(impl.getOrThrow(Enchantments.FORTUNE)))));
    }
}
