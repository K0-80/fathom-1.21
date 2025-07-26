package com.k080.fathom.block;

import com.k080.fathom.Fathom;
import com.k080.fathom.block.custom.AmethystResonatorBlock;
import com.k080.fathom.block.custom.BloodCrucibleBlock;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;


public class ModBlocks {

    public static final Block BlOOD_CRUCIBLE =  registerBlock("blood_crucible",
            new BloodCrucibleBlock(AbstractBlock.Settings.create()
                    .nonOpaque().hardness(60.0f).resistance(1200.0f).dropsNothing().sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block AMETHYST_RESONATOR =  registerBlock("amethyst_resonator",
            new AmethystResonatorBlock(AbstractBlock.Settings.create()
                    .nonOpaque().hardness(9.0f).resistance(1200.0f).strength(1.5F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool()));


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
