package com.k080.fathom.networking;

import com.k080.fathom.Fathom;
import com.k080.fathom.networking.packet.ShockwaveS2CPacket;
import com.k080.fathom.util.ShockwaveUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class ModMessages {
    public static void registerS2CPackets() {
        PayloadTypeRegistry.playS2C().register(ShockwaveS2CPacket.ID, ShockwaveS2CPacket.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(ShockwaveS2CPacket.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                ShockwaveUtil.createShockwave(client.world, payload.center(), payload.radius());
            });
        });
    }

    public static void registerC2SPackets() {
    }
}