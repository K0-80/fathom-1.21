package com.k080.fathom.item;

import com.k080.fathom.Fathom;
import com.k080.fathom.component.ModComponents;
import com.k080.fathom.entity.ModEntities;
import com.k080.fathom.item.book.BookPages;
import com.k080.fathom.item.custom.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.Set;

public class ModItems {

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
            new ScytheItem(ModToolMaterials.SCYTHE, new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.SCYTHE, 9, 1.2f -4f))));

    public static final Item MIRAGE = registerItem("mirage",
            new Mirageitem(ModToolMaterials.MIRAGE, new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.MIRAGE, 6, 1.4f -4f))));


    public static final Item PICTURE_BOOK = registerItem("picture_book",
            new PictureBookItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)
                    // The book starts with page 1 already unlocked.
                    //.component(ModComponents.UNLOCKED_PAGES, Set.of(BookPages.PAGE_1.id()))
            )
    );
    public static final Item TORN_PAGE_1 = registerTornPage("torn_page_1", BookPages.PAGE_1.id());
    public static final Item TORN_PAGE_2 = registerTornPage("torn_page_2", BookPages.PAGE_2.id());
    public static final Item TORN_PAGE_3 = registerTornPage("torn_page_3", BookPages.PAGE_3.id());
    public static final Item TORN_PAGE_4 = registerTornPage("torn_page_4", BookPages.PAGE_4.id());
    private static Item registerTornPage(String name, Identifier pageId) {
        return registerItem(name, new TornPageItem(new Item.Settings()
                .component(ModComponents.TORN_PAGE_ID, pageId).rarity(Rarity.UNCOMMON)
        ));
    }
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Fathom.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Fathom.LOGGER.info("Registering items for " + Fathom.MOD_ID);

    }
}
