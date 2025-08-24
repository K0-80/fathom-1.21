package com.k080.fathom.util;

import com.k080.fathom.Fathom;
import com.k080.fathom.item.ModItems;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class ModModelPredicateProvider {
    public static void registerModModels() {
        ModelPredicateProviderRegistry.register(ModItems.TWILIGHT_BLADE, Identifier.of(Fathom.MOD_ID, "light_state"),
                (stack, world, entity, seed) -> {
                    if (entity == null || world == null) {
                        return 0.5f;
                    }

                    int lightLevel = world.getLightLevel(entity.getBlockPos());

                    if (lightLevel <= 5) {
                        return 0.0f; // Umbra
                    } else if (lightLevel >= 10) {
                        return 1.0f; // Lux
                    } else {
                        return 0.5f; // Twilight
                    }
                });
    }
}