package com.k080.fathom.client.handler;

import com.k080.fathom.client.renderer.TrailManager;
import com.k080.fathom.component.ModComponents;
import com.k080.fathom.item.custom.ScytheItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.Random;

public class ScytheSoulEffectHandler {

    private static final Random random = new Random();

    public static void tick(MinecraftClient client) {
        ClientWorld world = client.world;
        if (world == null) return;
        for (PlayerEntity player : world.getPlayers()) {
            handleScytheForPlayer(player);
        }
    }

    private static void handleScytheForPlayer(PlayerEntity player) {
        ItemStack mainHandStack = player.getMainHandStack();
        if (!(mainHandStack.getItem() instanceof ScytheItem)) return;

        int souls = mainHandStack.getOrDefault(ModComponents.SOULS, 0);
        if (souls <= 0) return;

        float spawnChance = souls / 40f;

        if (random.nextFloat() < spawnChance) {
            TrailManager.addTrail(createSoulTrail(player));
        }
    }

    private static TrailManager.Trail createSoulTrail(PlayerEntity player) {
        Vector3f color = new Vector3f(0.6f, 0.0f, 0.1f);
        double offsetX = (random.nextDouble() - 0.5) * player.getWidth();
        double offsetY = random.nextDouble() * player.getHeight() * 0.9;
        double offsetZ = (random.nextDouble() - 0.5) * player.getWidth();
        Vec3d randomOffset = new Vec3d(offsetX, offsetY, offsetZ);

        float baseWidth = 0.08f + random.nextFloat() * 0.07f;
        int maxLength = 6 + random.nextInt(7);

        return new TrailManager.Trail(player, randomOffset, color, baseWidth, 30, maxLength);
    }
}