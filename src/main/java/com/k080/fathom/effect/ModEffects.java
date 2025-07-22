package com.k080.fathom.effect;

import com.k080.fathom.Fathom;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects {

    public static final RegistryEntry<StatusEffect> STUNNED = registerStatusEffect("stunned",
            new StunnedEffect(StatusEffectCategory.HARMFUL, 0xfcf003));
    public static final RegistryEntry<StatusEffect> MITHRIL_VEIL = registerStatusEffect("mithril_veil",
            new StunnedEffect(StatusEffectCategory.BENEFICIAL, 0x48e2f0));
    public static final RegistryEntry<StatusEffect> WIND_GLOW = registerStatusEffect("wind_glow",
            new WindGlowEffect (StatusEffectCategory.NEUTRAL, 0x10cda5));
    public static final RegistryEntry<StatusEffect> ANCHORED = registerStatusEffect("anchored",
            new AnchoredEffect (StatusEffectCategory.HARMFUL, 0x393253));

    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(Fathom.MOD_ID, name), statusEffect);
    }

    public static void registerEffects() {
        Fathom.LOGGER.info( "Registering Mod Effects for: " + Fathom.MOD_ID);
    }
}
