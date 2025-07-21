package com.k080.fathom.mixin;

import com.k080.fathom.Fathom;
import com.k080.fathom.effect.ModEffects;
import com.k080.fathom.util.WindGlowAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements WindGlowAccessor {

    @Shadow
    public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @Unique
    private static final TrackedData<Boolean> IS_WIND_GLOWING =
            DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(IS_WIND_GLOWING, false);
    }

    @Inject(method = "onStatusEffectRemoved", at = @At("TAIL"))
    private void onEffectRemoved(StatusEffectInstance effect, CallbackInfo ci) {
        if (effect.getEffectType() == ModEffects.WIND_GLOW) {
            this.setWindGlow(false);
        }
    }

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

    @Override
    public boolean getWindGlow() {
        return ((LivingEntity) (Object) this).getDataTracker().get(IS_WIND_GLOWING);
    }

    @Override
    public void setWindGlow(boolean glowing) {
        ((LivingEntity) (Object) this).getDataTracker().set(IS_WIND_GLOWING, glowing);
    }
}