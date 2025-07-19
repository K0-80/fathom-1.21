package com.k080.fathom.damage;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModDamageTypes {
    public static final RegistryKey<DamageType> GAUNTLET_COMBO = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of("fathom", "gauntlet_combo"));
}