package com.k080.fathom.index;

import com.k080.fathom.Fathom;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;


public class ModSounds {

    public static final SoundEvent ENTITY_ANCHOR_THROW = registerSoundEvent("entity.anchor.throw");
    public static final SoundEvent ENTITY_ANCHOR_HIT_GROUND = registerSoundEvent("entity.anchor.hit_ground");
    public static final SoundEvent ENTITY_ANCHOR_HIT_MOB = registerSoundEvent("entity.anchor.hit_mob");
    public static final SoundEvent ENTITY_ANCHOR_PICKUP = registerSoundEvent("entity.anchor.pickup");
    public static final SoundEvent ENTITY_ANCHOR_FLYING = registerSoundEvent("entity.anchor.flying");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(Fathom.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        Fathom.LOGGER.info("Registering Sounds for " + Fathom.MOD_ID);
    }
}
