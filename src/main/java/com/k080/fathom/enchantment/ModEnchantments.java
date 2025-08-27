package com.k080.fathom.enchantment;

import com.k080.fathom.Fathom;
import com.k080.fathom.util.ModTags;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.registry.Registerable;

public class ModEnchantments {

    public static final RegistryKey<Enchantment> HEAVE =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Fathom.MOD_ID, "heave"));
    public static final RegistryKey<Enchantment> UNDERTOW =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Fathom.MOD_ID, "undertow"));
    public static final RegistryKey<Enchantment> CRUSHING_DEPTH =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Fathom.MOD_ID, "crushing_depth"));

    public static final RegistryKey<Enchantment> ALACRITY =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Fathom.MOD_ID, "alacrity"));
    public static final RegistryKey<Enchantment> GALE_FORCE =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Fathom.MOD_ID, "gale_force"));
    public static final RegistryKey<Enchantment> GAZE =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Fathom.MOD_ID, "gaze"));

    public static final RegistryKey<Enchantment> REND =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Fathom.MOD_ID, "rend"));
    public static final RegistryKey<Enchantment> SANGUINE_COVENANT =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Fathom.MOD_ID, "sanguine_covenant"));
    public static final RegistryKey<Enchantment> FLOWSTATE =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Fathom.MOD_ID, "flowstate"));

    public static final RegistryKey<Enchantment> SHATTER =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Fathom.MOD_ID, "shatter"));
    public static final RegistryKey<Enchantment> PHASE_SHIFT =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Fathom.MOD_ID, "phase_shift"));
    public static final RegistryKey<Enchantment> TETHER =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Fathom.MOD_ID, "tether"));

    public static void bootstrap(Registerable<Enchantment> registerable) {
        var enchantments = registerable.getRegistryLookup(RegistryKeys.ENCHANTMENT);
        var items = registerable.getRegistryLookup(RegistryKeys.ITEM);

        // --- Anchor Enchantments ---
        register(registerable, HEAVE, Enchantment.builder(Enchantment.definition(
                        items.getOrThrow(ModTags.Items.ANCHOR_ENCHANTBLE),
                        2,
                        5,
                        Enchantment.leveledCost(15, 9),
                        Enchantment.leveledCost(45, 9),
                        4,
                        AttributeModifierSlot.HAND))
                .exclusiveSet(enchantments.getOrThrow(ModTags.Enchantments.ANCHOR_EXCLUSIVE))
        );
        register(registerable, CRUSHING_DEPTH, Enchantment.builder(Enchantment.definition(
                items.getOrThrow(ModTags.Items.ANCHOR_ENCHANTBLE),
                6,
                3,
                Enchantment.leveledCost(1, 10),
                Enchantment.leveledCost(21, 10),
                2,
                AttributeModifierSlot.HAND))
        );
        register(registerable, UNDERTOW, Enchantment.builder(Enchantment.definition(
                        items.getOrThrow(ModTags.Items.ANCHOR_ENCHANTBLE),
                        2, // pretty rare
                        1, // rare on books
                        Enchantment.leveledCost(15, 9),
                        Enchantment.leveledCost(45, 9),
                        4,
                        AttributeModifierSlot.HAND))
                .exclusiveSet(enchantments.getOrThrow(ModTags.Enchantments.ANCHOR_EXCLUSIVE))
        );

        // --- Wind Blade Enchantments ---
        register(registerable, ALACRITY, Enchantment.builder(Enchantment.definition(
                items.getOrThrow(ModTags.Items.WIND_BLADE_ENCHANTBLE),
                5, // rarer than sharpness
                5,
                Enchantment.leveledCost(5, 8),
                Enchantment.leveledCost(25, 8),
                2,
                AttributeModifierSlot.HAND))
        );
        register(registerable, GALE_FORCE, Enchantment.builder(Enchantment.definition(
                items.getOrThrow(ModTags.Items.WIND_BLADE_ENCHANTBLE),
                1, // very rare
                1,
                Enchantment.leveledCost(25, 10),
                Enchantment.leveledCost(50, 10),
                4,
                AttributeModifierSlot.HAND))
        );
        register(registerable, GAZE, Enchantment.builder(Enchantment.definition(
                items.getOrThrow(ModTags.Items.WIND_BLADE_ENCHANTBLE),
                8, // average/common
                5,
                Enchantment.leveledCost(1, 0),
                Enchantment.leveledCost(21, 0),
                1,
                AttributeModifierSlot.HAND))
        );

        // --- Scythe Enchantments ---
        register(registerable, REND, Enchantment.builder(Enchantment.definition(
                items.getOrThrow(ModTags.Items.SCYTHE_ENCHANTBLE),
                8, // average/common
                5,
                Enchantment.leveledCost(5, 8),
                Enchantment.leveledCost(25, 8),
                2,
                AttributeModifierSlot.HAND))
        );
        register(registerable, SANGUINE_COVENANT, Enchantment.builder(Enchantment.definition(
                items.getOrThrow(ModTags.Items.SCYTHE_ENCHANTBLE),
                1, // super rare
                1,
                Enchantment.leveledCost(25, 12),
                Enchantment.leveledCost(75, 12),
                4,
                AttributeModifierSlot.HAND))
        );
        register(registerable, FLOWSTATE, Enchantment.builder(Enchantment.definition(
                items.getOrThrow(ModTags.Items.SCYTHE_ENCHANTBLE),
                2, // decently rare
                2,
                Enchantment.leveledCost(15, 10),
                Enchantment.leveledCost(40, 10),
                2,
                AttributeModifierSlot.HAND))
        );

        // --- Mirage Enchantments ---
        register(registerable, SHATTER, Enchantment.builder(Enchantment.definition(
                items.getOrThrow(ModTags.Items.MIRAGE_ENCHANTBLE),
                8, // average/common
                5,
                Enchantment.leveledCost(5, 8),
                Enchantment.leveledCost(25, 8),
                2,
                AttributeModifierSlot.HAND))
        );
        register(registerable, PHASE_SHIFT, Enchantment.builder(Enchantment.definition(
                items.getOrThrow(ModTags.Items.MIRAGE_ENCHANTBLE),
                2, // decently rare
                2,
                Enchantment.leveledCost(18, 9),
                Enchantment.leveledCost(48, 9),
                4,
                AttributeModifierSlot.HAND))
        );
        register(registerable, TETHER, Enchantment.builder(Enchantment.definition(
                items.getOrThrow(ModTags.Items.MIRAGE_ENCHANTBLE),
                2, // decently rare
                2,
                Enchantment.leveledCost(15, 10),
                Enchantment.leveledCost(40, 10),
                2,
                AttributeModifierSlot.HAND))
        );
    }

    private static void register(Registerable<Enchantment> registry, RegistryKey<Enchantment> key, Enchantment.Builder builder) {
        registry.register(key, builder.build(key.getValue()));
    }
}