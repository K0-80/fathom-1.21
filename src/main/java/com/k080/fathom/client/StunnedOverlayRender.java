package com.k080.fathom.client; // Make sure this is in a .client sub-package

import com.k080.fathom.Fathom;
import com.k080.fathom.effect.ModEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class StunnedOverlayRender implements HudRenderCallback {
    // Create an identifier for our overlay texture
    private static final Identifier STUNNED_OVERLAY_TEXTURE = Identifier.of(Fathom.MOD_ID, "textures/misc/stunned_overlay.png");

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.player.hasStatusEffect(ModEffects.STUNNED)) {

            int width = drawContext.getScaledWindowWidth();
            int height = drawContext.getScaledWindowHeight();

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, STUNNED_OVERLAY_TEXTURE);

            drawContext.drawTexture(STUNNED_OVERLAY_TEXTURE, 0, 0, -90, 0, 0, width, height, width, height);

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f); // Reset color to be fully opaque!
        }
    }
}