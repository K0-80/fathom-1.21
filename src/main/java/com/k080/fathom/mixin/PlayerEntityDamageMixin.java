package com.k080.fathom.mixin;

import com.k080.fathom.item.custom.Mirageitem;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityDamageMixin {

    @Inject(method = "damage", at = @At("HEAD"))
    private void fathom_onDamageLoseShard(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (source.getAttacker() != null && !player.getWorld().isClient()) {
            // Check offhand first
            if (callOnDamaged(player.getOffHandStack(), player)) {
                return; // A shard was lost from the offhand, so we're done.
            }

            // If no shard was lost from offhand, check the main hotbar
            PlayerInventory inventory = player.getInventory();
            for (int i = 0; i < 9; i++) { // Iterate through hotbar slots 0-8
                if (callOnDamaged(inventory.getStack(i), player)) {
                    break; // A shard was lost, stop checking the rest of the hotbar.
                }
            }
        }
    }

    @Unique
    private boolean callOnDamaged(ItemStack stack, PlayerEntity player) {
        if (stack.getItem() instanceof Mirageitem mirageitem) {
            return mirageitem.onDamaged(stack, player);
        }
        return false;
    }
}