package com.k080.fathom.datagen;

import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.item.ModItems;
import com.k080.fathom.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {

        List<ItemConvertible> MITHRIL_SMELTABLES = List.of(ModItems.RAW_MITHRIL, ModBlocks.MITHRIL_ORE, ModBlocks.DEEPSLATE_MITHRIL_ORE);
        offerSmelting(recipeExporter, MITHRIL_SMELTABLES, RecipeCategory.MISC, ModItems.MITHRIL_INGOT, 0.5f, 200, "mithril");
        offerBlasting(recipeExporter, MITHRIL_SMELTABLES, RecipeCategory.MISC, ModItems.MITHRIL_INGOT, 0.5f, 100, "mithril");

        offerReversibleCompactingRecipes(recipeExporter, RecipeCategory.BUILDING_BLOCKS, ModItems.MITHRIL_INGOT, RecipeCategory.DECORATIONS, ModBlocks.MITHRIL_BLOCK);
        offerReversibleCompactingRecipes(recipeExporter, RecipeCategory.BUILDING_BLOCKS, ModItems.RAW_MITHRIL, RecipeCategory.DECORATIONS, ModBlocks.RAW_MITHRIL_BLOCK);

        offerPlanksRecipe(recipeExporter, ModBlocks.DRIFTWOOD_PLANK, ModTags.Items.DRIFTWOOD_LOGS, 4);

        // Mithril Armor
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.MITHRIL_HELMET)
                .pattern("III")
                .pattern("I I")
                .input('I', ModItems.MITHRIL_INGOT)
                .criterion(hasItem(ModItems.MITHRIL_INGOT), conditionsFromItem(ModItems.MITHRIL_INGOT))
                .offerTo(recipeExporter, RecipeProvider.getRecipeName(ModItems.MITHRIL_HELMET));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.MITHRIL_CHESTPLATE)
                .pattern("I I")
                .pattern("III")
                .pattern("III")
                .input('I', ModItems.MITHRIL_INGOT)
                .criterion(hasItem(ModItems.MITHRIL_INGOT), conditionsFromItem(ModItems.MITHRIL_INGOT))
                .offerTo(recipeExporter, RecipeProvider.getRecipeName(ModItems.MITHRIL_CHESTPLATE));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.MITHRIL_LEGGINGS)
                .pattern("III")
                .pattern("I I")
                .pattern("I I")
                .input('I', ModItems.MITHRIL_INGOT)
                .criterion(hasItem(ModItems.MITHRIL_INGOT), conditionsFromItem(ModItems.MITHRIL_INGOT))
                .offerTo(recipeExporter, RecipeProvider.getRecipeName(ModItems.MITHRIL_LEGGINGS));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.MITHRIL_BOOTS)
                .pattern("I I")
                .pattern("I I")
                .input('I', ModItems.MITHRIL_INGOT)
                .criterion(hasItem(ModItems.MITHRIL_INGOT), conditionsFromItem(ModItems.MITHRIL_INGOT))
                .offerTo(recipeExporter, RecipeProvider.getRecipeName(ModItems.MITHRIL_BOOTS));

        //ANCHOR WEAPON
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.ANCHOR)
                .pattern("IID")
                .pattern(" HI")
                .pattern("D I")
                .input('H', Items.HEART_OF_THE_SEA)
                .input('I', Items.IRON_INGOT)
                .input('D', ModBlocks.DRIFTWOOD_LOG)
                .criterion(hasItem(Items.HEART_OF_THE_SEA), conditionsFromItem(Items.HEART_OF_THE_SEA))
                .offerTo(recipeExporter, RecipeProvider.getRecipeName(ModItems.ANCHOR));
    }
}
