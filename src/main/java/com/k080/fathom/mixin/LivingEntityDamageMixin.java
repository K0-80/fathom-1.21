package com.k080.fathom.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDamageMixin {

    @Inject(method = "damage", at = @At("HEAD"))
    private void fathom$logPlayerDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = source.getAttacker();
        LivingEntity target = (LivingEntity) (Object) this;

        if (attacker instanceof PlayerEntity && !attacker.getWorld().isClient() && amount > 0) {
            PlayerEntity player = (PlayerEntity) attacker;

            String attackerName = player.getName().getString();
            String targetName = target.getName().getString();

            Text message = Text.literal(String.format("%s dealt %.2f damage to %s", attackerName, amount, targetName));
            player.sendMessage(message, false);
        }
    }
}