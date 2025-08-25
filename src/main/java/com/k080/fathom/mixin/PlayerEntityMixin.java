package com.k080.fathom.mixin;

import com.k080.fathom.item.ModItems;
import com.k080.fathom.item.custom.ShatteredTotemItem;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
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
}