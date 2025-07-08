package com.k080.fathom.block;

import com.k080.fathom.Fathom;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block MITHRIL_BLOCK =  registerBlock("mithril_block", new Block(AbstractBlock.Settings.create().requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)));
    public static final Block RAW_MITHRIL_BLOCK = registerBlock("raw_mithril_block", new Block(AbstractBlock.Settings.create().requiresTool().strength(5.0F, 6.0F)));
    public static final Block MITHRIL_ORE= registerBlock("mithril_ore", new Block(AbstractBlock.Settings.create().strength(4.5F, 3.0F).sounds(BlockSoundGroup.STONE)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Fathom.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Fathom.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        Fathom.LOGGER.info("Registering Mod Blocks for" + Fathom.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(ModBlocks.MITHRIL_BLOCK);
            entries.add(ModBlocks.RAW_MITHRIL_BLOCK);
            entries.add(ModBlocks.MITHRIL_ORE);
        });

    }
}
