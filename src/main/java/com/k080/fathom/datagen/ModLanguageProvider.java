package com.k080.fathom.datagen;

import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.entity.ModEntities;
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
        translationBuilder.add(ModItems.TORN_PAGE_1, "Torn Page"); //starting lore
        translationBuilder.add(ModItems.TORN_PAGE_2, "Soggy Page"); //anchor
        translationBuilder.add(ModItems.TORN_PAGE_3, "Scorched Page"); //scythe
        translationBuilder.add(ModItems.TORN_PAGE_4, "Glistering Page"); //mirage
        translationBuilder.add(ModItems.TORN_PAGE_5, "Weightless Page"); //wind blade
        // lore
        translationBuilder.add("item.fathom.picture_book.lore", "It seems to be missing many pages...");
        translationBuilder.add("tooltip.fathom.torn_page.hint", "Right-click to add to a Codex");
        translationBuilder.add("text.fathom.page_added", "Added %s to Codex");

        // Item Group
        translationBuilder.add("itemgroup.fathom.fathom_items", "Fathom");

        // Blocks
        translationBuilder.add("block.fathom.blood_crucible", "Blood Crucible");
        translationBuilder.add("block.fathom.amethyst_resonator", "Amethyst Resonator");
        translationBuilder.add(ModBlocks.ANCHOR_BLOCK_INACTIVE, "Dormant Anchor");
        translationBuilder.add(ModBlocks.ANCHOR_BLOCK_ACTIVATED, "Activated Anchor");

        //Items
        translationBuilder.add("item.fathom.guardian_heart", "Guardian's Heart");
        translationBuilder.add(ModItems.QTIP, "Q-Tip");
        translationBuilder.add("item.fathom.qtip.tooltip.sampled_player", "Sample from: %s");

        // Effects
        translationBuilder.add("effect.fathom.stunned", "Stunned");
        translationBuilder.add("effect.fathom.mithril_veil", "Mithril Veil");
        translationBuilder.add("effect.fathom.wind_glow", "Marked");
        translationBuilder.add("effect.fathom.anchored", "Anchored");

        // Death Msg
        translationBuilder.add("death.attack.scythe_covenant", "%1$s made a pact they couldn't afford");
        translationBuilder.add("death.attack.mirage_shatter", "%1$s was shattered by %2$s's mirage");
        translationBuilder.add("death.attack.anchor_throw", "%1$s was dragged into the depths by %2$s");

        // Entities
        translationBuilder.add("entity.fathom.skeleton_fish", "Skeleton Fish");
        translationBuilder.add("item.fathom.skeleton_fish_bucket", "Bucket of Skeleton Fish");
        translationBuilder.add(ModEntities.SPIRIT, "Spirit");
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