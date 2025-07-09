package com.k080.fathom.mixin;

import com.k080.fathom.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @Inject(method = "getMovementSpeed*", at = @At("RETURN"), cancellable = true)
    private void onGetMovementSpeed(CallbackInfoReturnable<Float> cir) {
        if (this.hasStatusEffect(ModEffects.STUNNED)) {
            cir.setReturnValue(0.0f);
        }
    }
    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void onJump(CallbackInfo ci) {
        if (this.hasStatusEffect(ModEffects.STUNNED)) {
            ci.cancel();
        }
    }

}