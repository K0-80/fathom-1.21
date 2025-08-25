package com.k080.fathom.damage;

import com.k080.fathom.Fathom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ModDamageTypes {

    //scythe self damage
    public static final RegistryKey<DamageType> SCYTHE_COVENANT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Fathom.MOD_ID, "scythe_covenant"));
    //mirrage clone shatter enbchantment
    public static final RegistryKey<DamageType> MIRAGE_SHATTER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Fathom.MOD_ID, "mirage_shatter"));
    //anchor thrown projectile
    public static final RegistryKey<DamageType> ANCHOR_THROW = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Fathom.MOD_ID, "anchor_throw"));


    /**
     * Creates a DamageSource from a RegistryKey.
     * @param world The world object.
     * @param key The RegistryKey for the DamageType.
     * @param source The direct source of the damage (e.g., a projectile).
     * @param attacker The indirect source of the damage (e.g., the player who threw the projectile).
     * @return A new DamageSource instance.
     */
    public static DamageSource of(World world, RegistryKey<DamageType> key, @Nullable Entity source, @Nullable Entity attacker) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), source, attacker);
    }

    public static void register() {
        Fathom.LOGGER.info("Registering Damage Types for " + Fathom.MOD_ID);
    }
}