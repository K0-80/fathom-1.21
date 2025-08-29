package com.k080.fathom.mixin;

import com.k080.fathom.item.ModItems;
import com.k080.fathom.item.custom.ScytheItem;
import com.k080.fathom.item.custom.ShatteredTotemItem;
import com.k080.fathom.particle.ModParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onPlayerDeathInstantlyRepairTotem(DamageSource damageSource, CallbackInfo ci) {
        if (damageSource.getAttacker() instanceof ServerPlayerEntity killer) {
            PlayerInventory killerInventory = killer.getInventory();

            for (int i = 0; i < killerInventory.size(); i++) {
                ItemStack stack = killerInventory.getStack(i);
                if (stack.isOf(ModItems.SHATTERED_TOTEM)) {
                    ShatteredTotemItem.repairTotem(killer, i);
                    break;
                }
            }
        }
    }


    //sweep attack
    @Inject(method = "spawnSweepAttackParticles", at = @At("HEAD"), cancellable = true)
    private void fathom_spawnScytheSweepAttackParticles(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (player.getMainHandStack().getItem() instanceof ScytheItem) {

            double d = -MathHelper.sin(player.getYaw() * ((float)Math.PI / 180.0F));
            double e = MathHelper.cos(player.getYaw() * ((float)Math.PI / 180.0F));

            if (player.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ModParticles.SCYTHE_SWEEP, player.getX() + d, player.getBodyY(0.5), player.getZ() + e, 0, d, 0.0, e, 0.0);
            }
            ci.cancel();
        }
    }

    //crit attack
    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addCritParticles(Lnet/minecraft/entity/Entity;)V"))
    private void fathom_spawnScytheCritParticles(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (player.getMainHandStack().getItem() instanceof ScytheItem) {

            final double distanceMultiplier = 1.2;
            final double verticalOffset = 0;

            double d = -MathHelper.sin(player.getYaw() * ((float)Math.PI / 180.0F));
            double e = MathHelper.cos(player.getYaw() * ((float)Math.PI / 180.0F));

            double particleX = player.getX() + (d * distanceMultiplier);
            double particleY = player.getBodyY(0.5) - verticalOffset;
            double particleZ = player.getZ() + (e * distanceMultiplier);

            if (player.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ModParticles.SCYTHE_CRIT, particleX, particleY, particleZ, 0, d, 0.0, e, 0.0);
            }
        }
    }

}