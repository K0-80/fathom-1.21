package com.k080.fathom.datagen;

import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.MITHRIL_BLOCK)
                .add(ModBlocks.RAW_MITHRIL_BLOCK)
                .add(ModBlocks.MITHRIL_ORE)
                .add(ModBlocks.DEEPSLATE_MITHRIL_ORE);

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.MITHRIL_BLOCK)
                .add(ModBlocks.RAW_MITHRIL_BLOCK)
                .add(ModBlocks.MITHRIL_ORE)
                .add(ModBlocks.DEEPSLATE_MITHRIL_ORE);

        getOrCreateTagBuilder(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.DRIFTWOO_LOG)
                .add(ModBlocks.STRIPED_DRIFTWOOD_LOG)
                .add(ModBlocks.DRIFTWOOD_WOOD)
                .add(ModBlocks.STRIPED_DRIFTWOOD_WOOD);
    }
}
