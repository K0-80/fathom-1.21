package com.k080.fathom.event;

import com.k080.fathom.Fathom;
import com.k080.fathom.item.custom.Mirageitem;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public class PlayerAttackEvent {
    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient() && player.getStackInHand(hand).getItem() instanceof Mirageitem) {
                Fathom.LOGGER.info("cooldown " + player.getAttackCooldownProgress(0.5f));
                if (player.getAttackCooldownProgress(0.5f) == 1.0f) {
                    ItemStack stack = player.getStackInHand(hand);
                    ((Mirageitem) stack.getItem()).onChargedHit(stack, player);
                }
            }
            return ActionResult.PASS;
        });    }
}