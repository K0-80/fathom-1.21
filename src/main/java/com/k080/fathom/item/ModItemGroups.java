package com.k080.fathom.item;

import com.k080.fathom.Fathom;
import com.k080.fathom.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup FATHOM_ITEMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Fathom.MOD_ID, "fathom_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.MITHRIL_INGOT))
                    .displayName(Text.translatable("itemgroup.fathom.fathom_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.MITHRIL_INGOT);
                        entries.add(ModItems.RAW_MITHRIL);

                        entries.add(ModBlocks.MITHRIL_BLOCK);
                        entries.add(ModBlocks.RAW_MITHRIL_BLOCK);
                        entries.add(ModBlocks.MITHRIL_ORE);
                        entries.add(ModBlocks.DEEPSLATE_MITHRIL_ORE);

                        entries.add(ModBlocks.DRIFTWOOD_LOG);
                        entries.add(ModBlocks.STRIPED_DRIFTWOOD_LOG);
                        entries.add(ModBlocks.DRIFTWOOD_WOOD);
                        entries.add(ModBlocks.STRIPED_DRIFTWOOD_WOOD);
                        entries.add(ModBlocks.DRIFTWOOD_PLANK);
                        entries.add(ModBlocks.DRIFTWOOD_LEAVES);
                        entries.add(ModBlocks.DRIFTWOOD_SAPLING);

                        entries.add(ModItems.MITHRIL_HELMET);
                        entries.add(ModItems.MITHRIL_CHESTPLATE);
                        entries.add(ModItems.MITHRIL_LEGGINGS);
                        entries.add(ModItems.MITHRIL_BOOTS);

                        entries.add(ModItems.SKELETON_FISH_BUCKET);

                        entries.add(ModItems.ANCHOR);

                    }) .build());

    public static void registerItemGroups() {
        Fathom.LOGGER.info("Registering Item Groups for " + Fathom.MOD_ID);
    }
}
