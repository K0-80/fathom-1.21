package com.k080.fathom.item;

import com.k080.fathom.Fathom;
import com.k080.fathom.entity.ModEntitys;
import com.k080.fathom.item.custom.ModArmorItem;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.EntityBucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item MITHRIL_INGOT = registerItem("mithril_ingot", new Item(new Item.Settings()));
    public static final Item RAW_MITHRIL = registerItem("raw_mithril", new Item(new Item.Settings()));

    public static final Item MITHRIL_HELMET = registerItem("mithril_helmet",
            new ModArmorItem(ModArmorMaterials.MITHRIL_ARMOUR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings()
                    .maxDamage(ArmorItem.Type.HELMET.getMaxDamage(15))));
    public static final Item MITHRIL_CHESTPLATE = registerItem("mithril_chestplate",
            new ArmorItem(ModArmorMaterials.MITHRIL_ARMOUR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings()
                    .maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(15))));
    public static final Item MITHRIL_LEGGINGS = registerItem("mithril_leggings",
            new ArmorItem(ModArmorMaterials.MITHRIL_ARMOUR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings()
                    .maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(15))));
    public static final Item MITHRIL_BOOTS = registerItem("mithril_boots",
            new ArmorItem(ModArmorMaterials.MITHRIL_ARMOUR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings()
                    .maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(15))));



    public static final Item SKELETON_FISH_BUCKET = registerItem("skeleton_fish_bucket",
            new EntityBucketItem(ModEntitys.SKELETON_FISH, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH,
                    new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));



    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Fathom.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Fathom.LOGGER.info("Registering items for " + Fathom.MOD_ID);

    }
}
