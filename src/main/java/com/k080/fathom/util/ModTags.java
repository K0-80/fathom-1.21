package com.k080.fathom.util;

import com.k080.fathom.Fathom;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Blocks  {


        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(Fathom.MOD_ID, name));
        }
    }


    public static class Items {

        public static final  TagKey<Item> IMPALING_NEW = createTag("impaling_new");
        public static final  TagKey<Item> LOOTING_NEW = createTag("looting_new");

        public static final  TagKey<Item> ANCHOR_ENCHANTBLE = createTag("anchor_enchantble");
        public static final  TagKey<Item> WIND_BLADE_ENCHANTBLE = createTag("wind_blade_enchantble");
        public static final  TagKey<Item> SCYTHE_ENCHANTBLE = createTag("scythe_enchantble");
        public static final  TagKey<Item> MIRAGE_ENCHANTBLE = createTag("mirage_enchantble");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(Fathom.MOD_ID, name));
        }
    }

    public static class Enchantments {
        public static final TagKey<Enchantment> ANCHOR_EXCLUSIVE =
                TagKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Fathom.MOD_ID, "anchor_exclusive"));
    }

}
