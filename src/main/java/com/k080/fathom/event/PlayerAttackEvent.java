package com.k080.fathom.event;

import com.k080.fathom.Fathom;
import com.k080.fathom.item.custom.Mirageitem;
import com.k080.fathom.item.custom.TwilightBladeItem;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public class PlayerAttackEvent {
    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient() && player.getStackInHand(hand).getItem() instanceof Mirageitem) {
                if (player.getAttackCooldownProgress(0.5f) == 1.0f) {
                    ItemStack stack = player.getStackInHand(hand);
                    ((Mirageitem) stack.getItem()).onChargedHit(stack, player);
                }
            }
            if (!world.isClient() && player.getStackInHand(hand).getItem() instanceof TwilightBladeItem) {
                if (player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                    ItemStack stack = player.getStackInHand(hand);
                    ((TwilightBladeItem) stack.getItem()).onChargedHit(stack, player);
                }
            }
            return ActionResult.PASS;
        });
    }
}