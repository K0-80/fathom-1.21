package com.k080.fathom;

import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.component.ModComponents;
import com.k080.fathom.effect.ModEffects;
import com.k080.fathom.entity.ModBlockEntitys;
import com.k080.fathom.entity.ModEntities;
import com.k080.fathom.event.BlockBreakHandler;
import com.k080.fathom.item.ModItemGroups;
import com.k080.fathom.item.ModItems;
import com.k080.fathom.particle.ModParticles;
import com.k080.fathom.util.ModLootTableModifiers;
import com.k080.fathom.util.ReportCommand;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fathom implements ModInitializer {
	public static final String MOD_ID = "fathom";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ReportCommand.register();

		ModItemGroups.registerItemGroups();

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();

		ModEntities.registerModEntities();
		ModBlockEntitys.registerModEntities();

		ModEffects.registerEffects();

//		ModWorldGeneration.generateModWorldGen();

		ModLootTableModifiers.replaceLootTables();

		ModParticles.registerParticles();

		ModComponents.registerDataComponentTypes();

		BlockBreakHandler.register();
	}
}