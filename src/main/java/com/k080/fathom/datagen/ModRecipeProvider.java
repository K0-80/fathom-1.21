package com.k080.fathom.datagen;

import com.k080.fathom.Fathom;
import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {

        //guise armour trim
        offerSmithingTemplateCopyingRecipe(recipeExporter, ModItems.GUISE_SMITHING_TEMPLATE, Items.PHANTOM_MEMBRANE);
        offerSmithingTrimRecipe(recipeExporter, ModItems.GUISE_SMITHING_TEMPLATE, Identifier.of(Fathom.MOD_ID, "guise"));

        //RESONATOR BLOCK
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.AMETHYST_RESONATOR)
                .pattern("GSG")
                .pattern("S S")
                .pattern("GSG")
                .input('G', Items.AMETHYST_SHARD)
                .input('S', Items.ECHO_SHARD)
                .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(recipeExporter, RecipeProvider.getRecipeName(ModBlocks.AMETHYST_RESONATOR));

        //mending  slate
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.MENDING_SLATE, 4)
                .pattern("GSG")
                .pattern("SDS")
                .pattern("GSG")
                .input('S', Items.COBBLESTONE)
                .input('G', Items.LAPIS_LAZULI)
                .input('D', Items.DIAMOND)
                .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                .offerTo(recipeExporter, RecipeProvider.getRecipeName(ModItems.MENDING_SLATE));
    }
}