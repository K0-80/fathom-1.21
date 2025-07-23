package com.k080.fathom.item;

import com.k080.fathom.Fathom;
import com.k080.fathom.entity.ModEntities;
import com.k080.fathom.item.custom.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

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
            new EntityBucketItem(ModEntities.SKELETON_FISH, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH,
                    new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));

    public static final Item ANCHOR = registerItem("anchor",
            new AnchorItem(ModToolMaterials.ANCHOR, new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.ANCHOR, 10, 1f -4f))));

    public static final Item WIND_BLADE = registerItem("wind_blade",
            new WindBladeItem(ModToolMaterials.WINDBLADE, new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.WINDBLADE, 7, 1.6f -4f))));

    public static final Item SCYTHE = registerItem("scythe",
            new ScytheItem(ModToolMaterials.GAUNTLET, new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.SCYTHE, 9, 1.2f -4f))));

    public static final Item MIRAGE = registerItem("mirage",
            new Mirageitem(ModToolMaterials.MIRAGE, new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.MIRAGE, 6, 1.4f -4f))));

    public static final Item GAUNTLET = registerItem("gauntlet",
            new GauntletItem(ModToolMaterials.GAUNTLET, new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.GAUNTLET, 4, 1.8f -4f))));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Fathom.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Fathom.LOGGER.info("Registering items for " + Fathom.MOD_ID);

    }
}
