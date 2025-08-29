package com.k080.fathom.particle;

import com.k080.fathom.Fathom;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {

    public static final SimpleParticleType WIND_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType MARKED_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType ANCHORED_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType RAPTURE_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType FLOWSTATE_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType SCYTHE_SWEEP = FabricParticleTypes.simple();
    public static final SimpleParticleType SCYTHE_CRIT = FabricParticleTypes.simple();



    public static void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Fathom.MOD_ID, "wind_particle"), WIND_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Fathom.MOD_ID, "marked_particle"), MARKED_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Fathom.MOD_ID, "anchored_particle"), ANCHORED_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Fathom.MOD_ID, "rapture_particle"), RAPTURE_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Fathom.MOD_ID, "flowstate_particle"), FLOWSTATE_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Fathom.MOD_ID, "scythe_sweep"), SCYTHE_SWEEP);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Fathom.MOD_ID, "scythe_crit"), SCYTHE_CRIT);



    }
}
