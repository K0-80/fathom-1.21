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
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.registry.tag.BlockTags;

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

        //for cusstom enchants
        getOrCreateTagBuilder(ModTags.Items.ANCHOR_ENCHANTBLE)
                .add(ModItems.ANCHOR);
        getOrCreateTagBuilder(ModTags.Items.WIND_BLADE_ENCHANTBLE)
                .add(ModItems.WIND_BLADE);
        getOrCreateTagBuilder(ModTags.Items.SCYTHE_ENCHANTBLE)
                .add(ModItems.SCYTHE);
        getOrCreateTagBuilder(ModTags.Items.MIRAGE_ENCHANTBLE)
                .add(ModItems.MIRAGE);

        //normal minecraft tags
        getOrCreateTagBuilder(ItemTags.DURABILITY_ENCHANTABLE)
                .add(ModItems.ANCHOR, ModItems.WIND_BLADE, ModItems.SCYTHE, ModItems.MIRAGE);
        getOrCreateTagBuilder(ItemTags.VANISHING_ENCHANTABLE)
                .add(ModItems.ANCHOR, ModItems.WIND_BLADE, ModItems.SCYTHE, ModItems.MIRAGE);

        //all sword enchants
        getOrCreateTagBuilder(ItemTags.SWORDS)
                .add(ModItems.WIND_BLADE, ModItems.SCYTHE, ModItems.MIRAGE);

        getOrCreateTagBuilder(ModTags.Items.IMPALING_NEW)
                .add(ModItems.ANCHOR, Items.TRIDENT);
        getOrCreateTagBuilder(ModTags.Items.LOOTING_NEW)
                .add(ModItems.ANCHOR, Items.TRIDENT,
                        Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.GOLDEN_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE,
                        Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
    }
}
