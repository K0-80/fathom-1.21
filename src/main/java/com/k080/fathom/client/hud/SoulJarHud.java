package com.k080.fathom.client.hud;

import com.k080.fathom.component.ModComponents;
import com.k080.fathom.item.custom.ScytheItem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class SoulJarHud implements HudRenderCallback {
    private static final Identifier SOUL_JAR_HUD_TEXTURE = Identifier.of("fathom", "textures/gui/soul_jar_hud.png");

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) { //tbh its a blood jar but whatever
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.player.isSpectator()) {
            return;
        }

        ItemStack mainHandStack = client.player.getMainHandStack();
        ItemStack offHandStack = client.player.getOffHandStack();
        ItemStack scytheStack = null;

        if (mainHandStack.getItem() instanceof ScytheItem) {
            scytheStack = mainHandStack;
        } else if (offHandStack.getItem() instanceof ScytheItem) {
            scytheStack = offHandStack;
        }

        if (scytheStack != null) {
            int soulCount = scytheStack.getOrDefault(ModComponents.SOULS, 0);
            int maxSouls = 10;

            int x = drawContext.getScaledWindowWidth() / 2 - 9;
            int y = drawContext.getScaledWindowHeight() - 49;

            int width = 18;
            int containerHeight = 18;
            int textureWidth = 18;
            int textureHeight = 36;
            drawContext.drawTexture(SOUL_JAR_HUD_TEXTURE, x, y, 0, 0, width, containerHeight, textureWidth, textureHeight);

            if (soulCount > 0) {
                final int FILL_TEXTURE_START_V = 23; // The V coordinate where the liquid pixels begin.
                final int FILL_TEXTURE_HEIGHT = 12;  // The total height of the liquid pixels (from V=23 to V=34).

                final int FILL_SCREEN_OFFSET_Y = 5;
                int filledPixelsHeight = (int) Math.ceil(FILL_TEXTURE_HEIGHT * ((float) soulCount / maxSouls));

                if (filledPixelsHeight > 0) {
                    int screenY = (y + FILL_SCREEN_OFFSET_Y + FILL_TEXTURE_HEIGHT) - filledPixelsHeight;
                    int textureV = (FILL_TEXTURE_START_V + FILL_TEXTURE_HEIGHT) - filledPixelsHeight;

                    drawContext.drawTexture(
                            SOUL_JAR_HUD_TEXTURE,
                            x,
                            screenY,
                            0,          // U coordinate
                            textureV,   // V coordinate
                            width,
                            filledPixelsHeight,
                            textureWidth,
                            textureHeight
                    );
                }
            }
        }
    }
}