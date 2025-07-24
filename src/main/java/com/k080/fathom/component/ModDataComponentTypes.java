package com.k080.fathom.component;

import com.k080.fathom.Fathom;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.PacketCodecs;
import java.util.UUID;

import java.util.UUID;
import java.util.function.UnaryOperator;

public class ModDataComponentTypes {

    public static final ComponentType<Integer> SOULS =
            register("souls", builder -> builder.codec(Codec.INT));


    public static final ComponentType<UUID> CLONE_UUID =
            register("clone_uuid", builder -> builder
                    .codec(Codec.STRING.xmap(UUID::fromString, UUID::toString))
                    .packetCodec(PacketCodecs.STRING.xmap(UUID::fromString, UUID::toString)));


    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Fathom.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());
    }


    public static void registerDataComponentTypes() {
        Fathom.LOGGER.info("Registering Data Component Types for " + Fathom.MOD_ID);
    }
}