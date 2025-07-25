package com.k080.fathom.datagen;

import com.k080.fathom.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLanguageProvider extends FabricLanguageProvider {
    public ModLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        // Picture Book Items
        translationBuilder.add(ModItems.PICTURE_BOOK, "Ancient Codex");
        translationBuilder.add(ModItems.TORN_PAGE_1, "Torn Page");
        translationBuilder.add(ModItems.TORN_PAGE_2, "Soggy Page");
        translationBuilder.add(ModItems.TORN_PAGE_3, "Scorched Page");
        translationBuilder.add(ModItems.TORN_PAGE_4, "Glistering Page");
        // torn page lore
        translationBuilder.add("item.fathom.picture_book.lore", "It seems to be missing many pages...");

        translationBuilder.add("tooltip.fathom.torn_page.hint", "Right-click to add to a Codex in your inventory.");
        translationBuilder.add("tooltip.fathom.torn_page.page_1", "Where the world touches the stars... the wind whispers...");
        translationBuilder.add("tooltip.fathom.torn_page.page_2", "In sunken halls... hidden in a forgotten chest");
        translationBuilder.add("tooltip.fathom.torn_page.page_3", "in the valley of the damned... what will you pay for power?");
        translationBuilder.add("tooltip.fathom.torn_page.page_4", "...where stone sings in the hollow dark.");

        // Item Group
        translationBuilder.add("itemgroup.fathom.fathom_items", "Fathom");

        // Blocks
        translationBuilder.add("block.fathom.blood_crucible", "Blood Crucible");
        translationBuilder.add("block.fathom.amethyst_resonator", "Amethyst Resonator");

        // Effects
        translationBuilder.add("effect.fathom.stunned", "Stunned");
        translationBuilder.add("effect.fathom.mithril_veil", "Mithril Veil");
        translationBuilder.add("effect.fathom.wind_glow", "Marked");
        translationBuilder.add("effect.fathom.anchored", "Anchored");

        // Entities
        translationBuilder.add("entity.fathom.skeleton_fish", "Skeleton Fish");
        translationBuilder.add("item.fathom.skeleton_fish_bucket", "Bucket of Skeleton Fish");
        translationBuilder.add("entity.fathom.player_clone", "Clone");

        // Anchor Item & Enchantments
        translationBuilder.add("item.fathom.anchor", "Fathom");
        translationBuilder.add("enchantment.fathom.maelstrom", "Maelstrom");
        translationBuilder.add("enchantment.fathom.resonance", "Resonance");
        translationBuilder.add("enchantment.fathom.momentum", "Momentum");

        // Wind Blade Item & Enchantments
        translationBuilder.add("item.fathom.wind_blade", "Zephyr");
        translationBuilder.add("enchantment.fathom.alacrity", "Alacrity");
        translationBuilder.add("enchantment.fathom.gale_force", "Gale Force");
        translationBuilder.add("enchantment.fathom.gaze", "Gaze");

        // Scythe Item & Enchantments
        translationBuilder.add("item.fathom.scythe", "Hex");
        translationBuilder.add("enchantment.fathom.rend", "Rend");
        translationBuilder.add("enchantment.fathom.sanguine_covenant", "Sanguine Covenant");
        translationBuilder.add("enchantment.fathom.flowstate", "Flowstate");

        // Mirage Item & Enchantments
        translationBuilder.add("item.fathom.mirage", "Mirage");
        translationBuilder.add("enchantment.fathom.shatter", "Shatter");
        translationBuilder.add("enchantment.fathom.tether", "Tether");
        translationBuilder.add("enchantment.fathom.phase_shift", "Phase-shift");

        // Gauntlet Item
        translationBuilder.add("item.fathom.gauntlet", "Earthshaker Gauntlet");
    }
}