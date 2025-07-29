package com.k080.fathom.damage;

import com.k080.fathom.Fathom;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModDamageTypes {

    //scythe self damage
    public static final RegistryKey<DamageType> SCYTHE_COVENANT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Fathom.MOD_ID, "scythe_covenant"));
    //mirrage clone shatter enbchantment
    public static final RegistryKey<DamageType> MIRAGE_SHATTER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Fathom.MOD_ID, "mirage_shatter"));
    //anchor thrown projectile
    public static final RegistryKey<DamageType> ANCHOR_THROW = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Fathom.MOD_ID, "anchor_throw"));

    public static void register() {
        Fathom.LOGGER.info("Registering Damage Types for " + Fathom.MOD_ID);
    }
}