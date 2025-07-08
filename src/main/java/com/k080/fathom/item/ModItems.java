package com.k080.fathom.item;

import com.k080.fathom.Fathom;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item MITHRIL_INGOT = registerItem("mithril_ingot", new Item(new Item.Settings()));
    public static final Item RAW_MITHRIL = registerItem("raw_mithril", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Fathom.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Fathom.LOGGER.info("Registering items for " + Fathom.MOD_ID);

    }
}
