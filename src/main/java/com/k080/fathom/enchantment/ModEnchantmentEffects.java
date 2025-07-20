package com.k080.fathom.enchantment;
import com.k080.fathom.Fathom;
import com.k080.fathom.enchantment.custom.MaelStromEnchantmentEffect;
import com.mojang.serialization.MapCodec;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEnchantmentEffects {
    public static final MapCodec<? extends EnchantmentEntityEffect> MAELSTROM =
            registerEntityEffect("maelstrom", MaelStromEnchantmentEffect.CODEC);


    private static MapCodec<? extends EnchantmentEntityEffect> registerEntityEffect(String name,
                                                                                    MapCodec<? extends EnchantmentEntityEffect> codec) {
        return Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Identifier.of(Fathom.MOD_ID, name), codec);
    }

    public static void registerEnchantmentEffects() {
        Fathom.LOGGER.info("Registering Mod Enchantment Effects for " + Fathom.MOD_ID);
    }
}
