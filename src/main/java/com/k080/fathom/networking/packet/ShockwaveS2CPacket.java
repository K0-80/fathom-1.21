package com.k080.fathom.networking.packet;

import com.k080.fathom.Fathom;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record ShockwaveS2CPacket(BlockPos center, int radius) implements CustomPayload {
    public static final CustomPayload.Id<ShockwaveS2CPacket> ID = new CustomPayload.Id<>(Identifier.of(Fathom.MOD_ID, "shockwave"));
    public static final PacketCodec<RegistryByteBuf, ShockwaveS2CPacket> CODEC = PacketCodec.of(ShockwaveS2CPacket::write, ShockwaveS2CPacket::new);

    private ShockwaveS2CPacket(RegistryByteBuf buf) {
        this(BlockPos.PACKET_CODEC.decode(buf), buf.readVarInt());
    }

    private void write(RegistryByteBuf buf) {
        BlockPos.PACKET_CODEC.encode(buf, this.center);
        buf.writeVarInt(this.radius);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}