package com.k080.fathom.item.trim;

import com.k080.fathom.Fathom;
import com.k080.fathom.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ModArmorTrimPatterns {
    public static final RegistryKey<ArmorTrimPattern> GUISE = RegistryKey.of(RegistryKeys.TRIM_PATTERN,
            Identifier.of(Fathom.MOD_ID, "guise"));

    public static void bootstrap(Registerable<ArmorTrimPattern> context) {
        register(context, ModItems.GUISE_SMITHING_TEMPLATE, GUISE);
    }

    private static void register(Registerable<ArmorTrimPattern> context, Item item, RegistryKey<ArmorTrimPattern> key) {
        ArmorTrimPattern trimPattern = new ArmorTrimPattern(key.getValue(), Registries.ITEM.getEntry(item),
                Text.translatable(Util.createTranslationKey("trim_pattern", key.getValue())), false);

        context.register(key, trimPattern);
    }
}