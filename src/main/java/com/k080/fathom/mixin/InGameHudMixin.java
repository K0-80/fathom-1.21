package com.k080.fathom.mixin;

import com.k080.fathom.item.custom.CreakingStaffItem;
import com.k080.fathom.item.custom.ScytheItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Unique
    private boolean fathom$shouldHideExperience() {
        if (this.client.player == null) {
            return false;
        }
        ItemStack mainHandStack = this.client.player.getMainHandStack();
        ItemStack offHandStack = this.client.player.getOffHandStack();
        return mainHandStack.getItem() instanceof ScytheItem || offHandStack.getItem() instanceof ScytheItem ||
                 offHandStack.getItem() instanceof CreakingStaffItem;
    }

    @Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
    private void fathom$cancelRenderExperienceLevel(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (fathom$shouldHideExperience()) {
            ci.cancel();
        }
    }
}