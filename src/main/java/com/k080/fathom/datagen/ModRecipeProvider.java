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


        //ANCHOR WEAPON
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.ANCHOR)
                .pattern("IID")
                .pattern(" HI")
                .pattern("D I")
                .input('H', Items.HEART_OF_THE_SEA)
                .input('I', Items.IRON_INGOT)
                .input('D', Items.STICK)
                .criterion(hasItem(Items.HEART_OF_THE_SEA), conditionsFromItem(Items.HEART_OF_THE_SEA))
                .offerTo(recipeExporter, RecipeProvider.getRecipeName(ModItems.ANCHOR));

        //RESONATOR BLOCK
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.AMETHYST_RESONATOR)
                .pattern("GSG")
                .pattern("S S")
                .pattern("GSG")
                .input('G', Items.AMETHYST_SHARD)
                .input('S', Items.ECHO_SHARD)
                .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(recipeExporter, RecipeProvider.getRecipeName(ModBlocks.AMETHYST_RESONATOR));
    }
}
