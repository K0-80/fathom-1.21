package com.k080.fathom.mixin;

import com.k080.fathom.effect.ModEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if ((Object)this instanceof LivingEntity livingEntity) {
            if (livingEntity.hasStatusEffect(ModEffects.WIND_GLOW)) {
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 2, 0, true, false));
            }
        }
    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void onGetTeamColorValue(CallbackInfoReturnable<Integer> cir) {
        if ((Object)this instanceof LivingEntity livingEntity) {
            if (livingEntity.hasStatusEffect(ModEffects.WIND_GLOW)) {
                cir.setReturnValue(0x10cda5);
            }
        }
    }
}