package com.k080.fathom.datagen;

import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.item.ModItems;
import com.k080.fathom.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    private static TagKey<Item> of(String id) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", id));
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

        getOrCreateTagBuilder(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.DRIFTWOOD_LOG.asItem())
                .add(ModBlocks.STRIPED_DRIFTWOOD_LOG.asItem())
                .add(ModBlocks.DRIFTWOOD_WOOD.asItem())
                .add(ModBlocks.STRIPED_DRIFTWOOD_WOOD.asItem());
        getOrCreateTagBuilder(ItemTags.PLANKS)
                .add(ModBlocks.DRIFTWOOD_PLANK.asItem());

        getOrCreateTagBuilder(ModTags.Items.DRIFTWOOD_LOGS)
                .add(ModBlocks.DRIFTWOOD_LOG.asItem())
                .add(ModBlocks.STRIPED_DRIFTWOOD_LOG.asItem())
                .add(ModBlocks.DRIFTWOOD_WOOD.asItem())
                .add(ModBlocks.STRIPED_DRIFTWOOD_WOOD.asItem());

        getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.MITHRIL_HELMET)
                .add(ModItems.MITHRIL_CHESTPLATE)
                .add(ModItems.MITHRIL_LEGGINGS)
                .add(ModItems.MITHRIL_BOOTS);

        getOrCreateTagBuilder(ModTags.Items.ANCHOR_ENCHANTBLE)
                .add(ModItems.ANCHOR);
        getOrCreateTagBuilder(ModTags.Items.WIND_BLADE_ENCHANTBLE)
                .add(ModItems.WIND_BLADE);
        getOrCreateTagBuilder(ModTags.Items.SCYTHE_ENCHANTBLE)
                .add(ModItems.SCYTHE);
        getOrCreateTagBuilder(ModTags.Items.GAUNLET_ENCHANTBLE)
                .add(ModItems.GAUNTLET);

        getOrCreateTagBuilder(ItemTags.DURABILITY_ENCHANTABLE)
                .add(ModItems.ANCHOR)
                .add(ModItems.WIND_BLADE)
                .add(ModItems.GAUNTLET)
                .add(ModItems.SCYTHE);

        getOrCreateTagBuilder(ItemTags.SWORD_ENCHANTABLE)
                .add(ModItems.WIND_BLADE);

        getOrCreateTagBuilder(ItemTags.VANISHING_ENCHANTABLE)
                .add(ModItems.ANCHOR)
                .add(ModItems.WIND_BLADE)
                .add(ModItems.GAUNTLET)
                .add(ModItems.SCYTHE);

        getOrCreateTagBuilder(ModTags.Items.IMPALING_NEW)
                .add(ModItems.ANCHOR)
                .add(Items.TRIDENT);
    }
}
