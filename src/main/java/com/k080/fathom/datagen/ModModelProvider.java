package com.k080.fathom.datagen;

import com.k080.fathom.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;

public class ModModelProvider extends FabricModelProvider    {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }
    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

        itemModelGenerator.register(ModItems.SKELETON_FISH_BUCKET, Models.GENERATED);

        itemModelGenerator.register(ModItems.TORN_PAGE_1, Models.GENERATED);
        itemModelGenerator.register(ModItems.TORN_PAGE_2, Models.GENERATED);
        itemModelGenerator.register(ModItems.TORN_PAGE_3, Models.GENERATED);
        itemModelGenerator.register(ModItems.TORN_PAGE_4, Models.GENERATED);
        itemModelGenerator.register(ModItems.TORN_PAGE_5, Models.GENERATED);

        itemModelGenerator.register(ModItems.GUARDIAN_HEART, Models.GENERATED);
        itemModelGenerator.register(ModItems.QTIP, Models.GENERATED);


    }
}
