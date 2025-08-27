package com.k080.fathom.component;

import com.k080.fathom.Fathom;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;

import java.util.*;
import java.util.function.UnaryOperator;

public class ModComponents {

    //anchor
    public static final ComponentType<Optional<UUID>> THROWN_ANCHOR_UUID = register("thrown_anchor_uuid", builder -> builder
            .codec(Codecs.optional(Uuids.CODEC))
            .packetCodec(PacketCodecs.optional(Uuids.PACKET_CODEC))
    );

    //hex
    public static final ComponentType<Integer> SOULS =
            register("souls", builder -> builder.codec(Codec.INT));


    //mirrage
    public static final ComponentType<Integer> SHARDS =
            register("shards", builder -> builder.codec(Codec.INT));

    //twilight
    public static final ComponentType<Integer> UMBRA_CHARGE  =
            register("umbra_charge", builder -> builder.codec(Codec.INT));
    public static final ComponentType<Integer> LUX_CHARGE  =
            register("lux_charge", builder -> builder.codec(Codec.INT));

    //guide book thing
    public static final ComponentType<Set<Identifier>> UNLOCKED_PAGES =
            register("unlocked_pages", builder -> builder
                    .codec(Identifier.CODEC.listOf().xmap(HashSet::new, ArrayList::new))
                    .packetCodec(PacketCodecs.collection(HashSet::new, Identifier.PACKET_CODEC)));

    public static final ComponentType<Identifier> TORN_PAGE_ID =
            register("torn_page_id", builder -> builder
                    .codec(Identifier.CODEC)
                    .packetCodec(Identifier.PACKET_CODEC));


    //dna sample
    public record SampledPlayerData(UUID uuid, String name) {
        public static final Codec<SampledPlayerData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Uuids.CODEC.fieldOf("uuid").forGetter(SampledPlayerData::uuid),
                        Codec.STRING.fieldOf("name").forGetter(SampledPlayerData::name)
                ).apply(instance, SampledPlayerData::new)
        );
    }
    public static final ComponentType<SampledPlayerData> SAMPLED_PLAYER_DATA =
            register("sampled_player_data", builder -> builder
                    .codec(SampledPlayerData.CODEC)
                    .packetCodec(PacketCodecs.codec(SampledPlayerData.CODEC)));

    // shatterded totem
    public static final ComponentType<Integer> REPAIR_TIME = register("repair_time",
            builder -> builder.codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT)
    );

    //mending slate
    public record MendingTarget(int remainingRepair, long lastUpdateTick) {
        public static final Codec<MendingTarget> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("remaining_repair").forGetter(MendingTarget::remainingRepair),
                        Codec.LONG.fieldOf("last_update_tick").forGetter(MendingTarget::lastUpdateTick)
                ).apply(instance, MendingTarget::new)
        );
    }
    public static final ComponentType<MendingTarget> MENDING_TARGET = register("mending_target", builder -> builder
            .codec(MendingTarget.CODEC)
            .packetCodec(PacketCodecs.codec(MendingTarget.CODEC))
            .cache()
    );

    //creaking staff
    public static final ComponentType<Boolean> IS_CHARGED = register("is_charged",
            builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));
    public static final ComponentType<Boolean> IS_WATCHED = register("is_watched",
            builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));


    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Fathom.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());
    }


    public static void registerDataComponentTypes() {
        Fathom.LOGGER.info("Registering Data Component Types for " + Fathom.MOD_ID);
    }
}