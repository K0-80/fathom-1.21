package com.k080.fathom.util;

import com.k080.fathom.Fathom;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportCommand {
    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1398043983342604329/LDnn3aw8nUOfKBVyaIoVUVN9kNX4KlNbQvyEtkiDgLD5IRg-PLGI_Q7FTSBEYTNDCblB";
// hi ok so can you not nuke my webhook its a waste of time for you and me this is gonna get deleted on release anyway
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                CommandManager.literal("report")
                        .then(CommandManager.argument("reason", StringArgumentType.greedyString())
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayer();
                                    String reason = StringArgumentType.getString(context, "reason");
                                    assert player != null;
                                    String playerName = player.getName().getString();

                                    sendReport(playerName, reason);

                                    source.sendFeedback(() -> Text.literal("Report sent successfully."), false);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
        ));
    }

    private static void sendReport(String playerName, String reason) {
        new Thread(() -> {
            try {
                URL url = new URL(WEBHOOK_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Minecraft-Report-Mod");
                connection.setDoOutput(true);

                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").format(new Date());

                String jsonPayload = String.format(
                        "{\"embeds\":[{\"title\":\"New Report\",\"color\":15158332,\"fields\":[{\"name\":\"Player\",\"value\":\"%s\",\"inline\":true},{\"name\":\"Timestamp\",\"value\":\"%s\",\"inline\":true}],\"description\":\"%s\"}]}",
                        escapeJson(playerName),
                        escapeJson(timestamp),
                        escapeJson(reason)
                );

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                connection.getResponseCode();
                connection.disconnect();

            } catch (Exception e) {
                Fathom.LOGGER.error("Failed to send report webhook", e);
            }
        }).start();
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}