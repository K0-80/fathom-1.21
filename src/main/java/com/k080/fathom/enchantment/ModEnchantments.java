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

    public static final RegistryKey<Enchantment> MAELSTROM =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Fathom.MOD_ID, "maelstrom"));
    public static final RegistryKey<Enchantment> MOMENTUM =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Fathom.MOD_ID, "momentum"));
    public static final RegistryKey<Enchantment> RESONANCE =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Fathom.MOD_ID, "resonance"));


    public static void bootstrap(Registerable<Enchantment> registerable) {
        var enchantments = registerable.getRegistryLookup(RegistryKeys.ENCHANTMENT);
        var items = registerable.getRegistryLookup(RegistryKeys.ITEM);

        register(registerable, MAELSTROM, Enchantment.builder(Enchantment.definition(
                        items.getOrThrow(ModTags.Items.ANCHOR_ENCHANTBLE),
                        2,
                        3,
                        Enchantment.leveledCost(8, 9),
                        Enchantment.leveledCost(30, 9),
                        4,
                        AttributeModifierSlot.HAND))
                .exclusiveSet(enchantments.getOrThrow(ModTags.Enchantments.ANCHOR_EXCLUSIVE))
        );

        register(registerable, MOMENTUM, Enchantment.builder(Enchantment.definition(
                items.getOrThrow(ModTags.Items.ANCHOR_ENCHANTBLE),
                5,
                5,
                Enchantment.leveledCost(1, 8),
                Enchantment.leveledCost(31, 8),
                2,
                AttributeModifierSlot.HAND))
        );

        register(registerable, RESONANCE, Enchantment.builder(Enchantment.definition(
                        items.getOrThrow(ModTags.Items.ANCHOR_ENCHANTBLE),
                        2,
                        3,
                        Enchantment.leveledCost(8, 9),
                        Enchantment.leveledCost(30, 9),
                        4,
                        AttributeModifierSlot.HAND))
                .exclusiveSet(enchantments.getOrThrow(ModTags.Enchantments.ANCHOR_EXCLUSIVE))
        );
    }


    private static void register(Registerable<Enchantment> registry, RegistryKey<Enchantment> key, Enchantment.Builder builder) {
        registry.register(key, builder.build(key.getValue()));
    }
}