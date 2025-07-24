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

        getOrCreateTagBuilder(ModTags.Items.ANCHOR_ENCHANTBLE)
                .add(ModItems.ANCHOR);
        getOrCreateTagBuilder(ModTags.Items.WIND_BLADE_ENCHANTBLE)
                .add(ModItems.WIND_BLADE);
        getOrCreateTagBuilder(ModTags.Items.SCYTHE_ENCHANTBLE)
                .add(ModItems.SCYTHE);
        getOrCreateTagBuilder(ModTags.Items.MIRAGE_ENCHANTBLE)
                .add(ModItems.MIRAGE);

        getOrCreateTagBuilder(ItemTags.DURABILITY_ENCHANTABLE)
                .add(ModItems.ANCHOR)
                .add(ModItems.WIND_BLADE)
                .add(ModItems.SCYTHE)
                .add(ModItems.MIRAGE);

        getOrCreateTagBuilder(ItemTags.SWORD_ENCHANTABLE)
                .add(ModItems.WIND_BLADE);

        getOrCreateTagBuilder(ItemTags.VANISHING_ENCHANTABLE)
                .add(ModItems.ANCHOR)
                .add(ModItems.WIND_BLADE)
                .add(ModItems.SCYTHE)
                .add(ModItems.MIRAGE);

        getOrCreateTagBuilder(ModTags.Items.IMPALING_NEW)
                .add(ModItems.ANCHOR)
                .add(Items.TRIDENT);
    }
}
