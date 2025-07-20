package com.k080.fathom.block;

import com.k080.fathom.Fathom;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;


public class ModBlocks {

    public static final Block MITHRIL_BLOCK =  registerBlock("mithril_block",
            new Block(AbstractBlock.Settings.create().requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).requiresTool()));
    public static final Block RAW_MITHRIL_BLOCK = registerBlock("raw_mithril_block",
            new Block(AbstractBlock.Settings.create().requiresTool().strength(5.0F, 6.0F).requiresTool()));

    public static final Block MITHRIL_ORE = registerBlock("mithril_ore", new ExperienceDroppingBlock(UniformIntProvider.create(2,5),
            AbstractBlock.Settings.create().strength(3F, 2F).requiresTool().sounds(BlockSoundGroup.STONE).requiresTool()));

    public static final Block DEEPSLATE_MITHRIL_ORE = registerBlock("deepslate_mithril_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(3,6),
                    AbstractBlock.Settings.create().strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE).requiresTool()));

    public static final Block DRIFTWOOD_LOG = registerBlock("driftwood_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block STRIPED_DRIFTWOOD_LOG = registerBlock("striped_driftwood_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final Block DRIFTWOOD_WOOD = registerBlock("driftwood_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD)));
    public static final Block STRIPED_DRIFTWOOD_WOOD = registerBlock("striped_driftwood_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_WOOD)));

    public static final Block DRIFTWOOD_PLANK = registerBlock("driftwood_plank",
            new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));
    public static final Block DRIFTWOOD_LEAVES = registerBlock("driftwood_leaves",
            new Block(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES)));
    public static final Block DRIFTWOOD_SAPLING = registerBlock("driftwood_sapling",
            new Block(AbstractBlock.Settings.copy(Blocks.OAK_SAPLING)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Fathom.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Fathom.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        Fathom.LOGGER.info("Registering Mod Blocks for " + Fathom.MOD_ID);


    }
}
