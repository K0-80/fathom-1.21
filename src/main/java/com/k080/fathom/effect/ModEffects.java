package com.k080.fathom.effect;

import com.k080.fathom.Fathom;
import net.fabricmc.loader.impl.transformer.FabricTransformer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects {

    public static final RegistryEntry<StatusEffect> STUNNED = registeryStatusEffect("stunned",
            new StunnedEffect(StatusEffectCategory.HARMFUL, 0xfcf003));

    private static RegistryEntry<StatusEffect> registeryStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(Fathom.MOD_ID, name), statusEffect);
    }

    public static void registerEffects() {
        Fathom.LOGGER.info( "Registering Mod Effects for: " + Fathom.MOD_ID);
    }
}
