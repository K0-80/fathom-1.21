package com.k080.fathom.client.hud;

import com.k080.fathom.Fathom;
import com.k080.fathom.component.ModComponents;
import com.k080.fathom.item.ModItems;
import com.k080.fathom.item.custom.ScytheItem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class CreakingStaffHud implements HudRenderCallback {
    private static final Identifier CREAKING_STAFF_HUD_TEXTURE = Identifier.of(Fathom.MOD_ID, "textures/gui/creaking_staff_hud.png");

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.options.getPerspective().isFirstPerson()) {
            PlayerEntity player = client.player;

            // Check if the Soul Jar HUD should render. If so, don't render this one.
            ItemStack mainHandStack = player.getMainHandStack();
            ItemStack offhandStack = player.getOffHandStack();
            if (mainHandStack.getItem() instanceof ScytheItem || offhandStack.getItem() instanceof ScytheItem) {
                return;
            }

            if (offhandStack.isOf(ModItems.CREAKING_STAFF)) {
                int screenWidth = drawContext.getScaledWindowWidth();
                int screenHeight = drawContext.getScaledWindowHeight();

                int x = screenWidth / 2 - 9;
                int y = screenHeight - 49;

                boolean isBeingWatched = offhandStack.getOrDefault(ModComponents.IS_WATCHED, false);
                boolean isCharged = offhandStack.getOrDefault(ModComponents.IS_CHARGED, false);

                int v_offset = isBeingWatched ? 18 : 0;

                // Render base texture (watched or not watched)
                drawContext.drawTexture(CREAKING_STAFF_HUD_TEXTURE, x, y, 0, v_offset, 18, 18, 18, 54);

                // Render fully charged overlay if charge is true
                if (isCharged) {
                    drawContext.drawTexture(CREAKING_STAFF_HUD_TEXTURE, x, y, 0, 36, 18, 18, 18, 54);
                }
            }
        }
    }
}