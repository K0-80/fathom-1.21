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
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.ANCHOR))
                    .displayName(Text.translatable("itemgroup.fathom.fathom_items"))
                    .entries((displayContext, entries) -> {

                        entries.add(ModItems.ANCHOR);
                        entries.add(ModItems.WIND_BLADE);
                        entries.add(ModItems.SCYTHE);
                        entries.add(ModItems.MIRAGE);

                        entries.add(ModBlocks.BlOOD_CRUCIBLE);
                        entries.add(ModBlocks.AMETHYST_RESONATOR);

                    }) .build());

    public static void registerItemGroups() {
        Fathom.LOGGER.info("Registering Item Groups for " + Fathom.MOD_ID);
    }
}
