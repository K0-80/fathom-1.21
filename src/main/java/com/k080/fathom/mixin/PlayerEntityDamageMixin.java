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
            if (callOnDamaged(player.getOffHandStack(), player)) {
                return;
            }

            PlayerInventory inventory = player.getInventory();
            for (int i = 0; i < 9; i++) {
                if (callOnDamaged(inventory.getStack(i), player)) {
                    break;
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