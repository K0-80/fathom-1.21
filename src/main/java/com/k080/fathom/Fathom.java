package com.k080.fathom;

import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.item.ModItemGroups;
import com.k080.fathom.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fathom implements ModInitializer {
	public static final String MOD_ID = "fathom";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
	}
}