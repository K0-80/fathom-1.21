package com.k080.fathom.datagen;

import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

        getOrCreateTagBuilder(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.DRIFTWOO_LOG.asItem())
                .add(ModBlocks.STRIPED_DRIFTWOOD_LOG.asItem())
                .add(ModBlocks.DRIFTWOOD_WOOD.asItem())
                .add(ModBlocks.STRIPED_DRIFTWOOD_WOOD.asItem());
        getOrCreateTagBuilder(ItemTags.PLANKS)
                .add(ModBlocks.DRIFTWOOD_PLANK.asItem());

        getOrCreateTagBuilder(ModTags.Items.DRIFTWOOD_LOGS)
                .add(ModBlocks.DRIFTWOO_LOG.asItem())
                .add(ModBlocks.STRIPED_DRIFTWOOD_LOG.asItem())
                .add(ModBlocks.DRIFTWOOD_WOOD.asItem())
                .add(ModBlocks.STRIPED_DRIFTWOOD_WOOD.asItem());
    }
}
