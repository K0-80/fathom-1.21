package com.k080.fathom.command;

import com.k080.fathom.networking.packet.ShockwaveS2CPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ShockwaveCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("shockwave")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> run(context.getSource(), context.getSource().getPlayer(), 5))
                .then(CommandManager.argument("radius", IntegerArgumentType.integer(1, 16))
                        .executes(context -> run(context.getSource(), context.getSource().getPlayer(), IntegerArgumentType.getInteger(context, "radius")))
                        .then(CommandManager.argument("target", EntityArgumentType.player())
                                .executes(context -> run(context.getSource(), EntityArgumentType.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "radius")))
                        )
                )
        );
    }

    private static int run(ServerCommandSource source, ServerPlayerEntity player, int radius) {
        if (player != null) {
            BlockPos center = player.getBlockPos();
            ServerPlayNetworking.send(player, new ShockwaveS2CPacket(center, radius));
        }
        return 1;
    }
}