package com.k080.fathom;

import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.component.ModDataComponentTypes;
import com.k080.fathom.effect.ModEffects;
import com.k080.fathom.entity.ModEntitys;
import com.k080.fathom.entity.custom.SkeletonFishEntity;
import com.k080.fathom.item.ModItemGroups;
import com.k080.fathom.item.ModItems;
import com.k080.fathom.util.FishConversionUtil;
import com.k080.fathom.world.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
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

		ModEntitys.registerModEntities();

		ModDataComponentTypes.registerDataComponentTypes();

		ModEffects.registerEffects();

		ModWorldGeneration.generateModWorldGen();

		StrippableBlockRegistry.register(ModBlocks.DRIFTWOOD_LOG, ModBlocks.STRIPED_DRIFTWOOD_LOG);
		StrippableBlockRegistry.register(ModBlocks.DRIFTWOOD_WOOD, ModBlocks.STRIPED_DRIFTWOOD_WOOD);

		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.DRIFTWOOD_LOG, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.DRIFTWOOD_WOOD, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPED_DRIFTWOOD_WOOD, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPED_DRIFTWOOD_LOG, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.DRIFTWOOD_LEAVES, 30, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.DRIFTWOOD_PLANK,5,  20);

		FabricDefaultAttributeRegistry.register(ModEntitys.SKELETON_FISH, SkeletonFishEntity.createSkeletonFishAttributes());

		registerFishConversions();

	}
	private static void registerFishConversions() {
		FishConversionUtil.register(Items.COD, EntityType.COD);
		FishConversionUtil.register(Items.SALMON, EntityType.SALMON);
		FishConversionUtil.register(Items.TROPICAL_FISH, EntityType.TROPICAL_FISH);
		FishConversionUtil.register(Items.PUFFERFISH, EntityType.PUFFERFISH);

		// FishConversion.register(ModItems.GHOST_FIN, ModEntities.GHOST_FISH);
	}
}