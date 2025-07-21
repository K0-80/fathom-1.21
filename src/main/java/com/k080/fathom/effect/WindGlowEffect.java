package com.k080.fathom.effect;

import com.k080.fathom.util.WindGlowAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class WindGlowEffect extends StatusEffect {
    public WindGlowEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        if (entity instanceof WindGlowAccessor accessor) {
            accessor.setWindGlow(true);
        }
        super.onApplied(entity, amplifier);
    }
}