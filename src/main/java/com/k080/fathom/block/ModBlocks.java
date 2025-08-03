package com.k080.fathom.block;

import com.k080.fathom.Fathom;
import com.k080.fathom.block.custom.*;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;


public class ModBlocks {

    public static final Block BlOOD_CRUCIBLE =  registerBlock("blood_crucible",
            new BloodCrucibleBlock(AbstractBlock.Settings.create()
                    .nonOpaque().hardness(60.0f).resistance(1200.0f).dropsNothing().sounds(BlockSoundGroup.DEEPSLATE)));

    // rarity menuyally added at bottom
    public static final Block AMETHYST_RESONATOR =  registerBlockWithoutItem("amethyst_resonator",
            new AmethystResonatorBlock(AbstractBlock.Settings.create()
                    .nonOpaque().hardness(9.0f).resistance(1200.0f).strength(1.5F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool()));

    public static final Block ANCHOR_BLOCK_INACTIVE = registerBlock("anchor_block_inactive",
            new AnchorBlock(AbstractBlock.Settings.create().strength(-1.0f, 3600000.0f).nonOpaque()));

    public static final Block ANCHOR_BLOCK_ACTIVATED = registerBlockWithoutItem("anchor_block_activated",
            new AnchorBlockActivated(AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).nonOpaque()));


    public static final Block CREAKING_VINE = registerBlock("creaking_vine",
            new CreakingVineBlock(AbstractBlock.Settings.copy(Blocks.GLOW_LICHEN)
                    .luminance(state -> 0)
                    .mapColor(MapColor.ORANGE)
                    .noCollision()
                    .strength(1f)
                    .sounds(BlockSoundGroup.CAVE_VINES)
                    .pistonBehavior(PistonBehavior.DESTROY)
                    .ticksRandomly()));



    // Helper for blocks with a standard item
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block, new Item.Settings());
        return Registry.register(Registries.BLOCK, Identifier.of(Fathom.MOD_ID, name), block);
    }

    // Helper for blocks without auto item registiotn
    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(Fathom.MOD_ID, name), block);
    }

    // Helper to register a BlockItem with specific settings
    private static Item registerBlockItem(String name, Block block, Item.Settings settings) {
        return Registry.register(Registries.ITEM, Identifier.of(Fathom.MOD_ID, name),
                new BlockItem(block, settings));
    }

    public static void registerModBlocks() {
        Fathom.LOGGER.info("Registering Mod Blocks for " + Fathom.MOD_ID);

        registerBlockItem("amethyst_resonator", AMETHYST_RESONATOR, new Item.Settings().rarity(Rarity.RARE));
    }
}