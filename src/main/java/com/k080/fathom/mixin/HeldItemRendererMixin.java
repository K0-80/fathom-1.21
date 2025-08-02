package com.k080.fathom.mixin;

import com.k080.fathom.component.ModComponents;
import com.k080.fathom.item.ModItems;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Redirect(method = "updateHeldItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;areEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    private boolean fathom$cancelAnimationForCreakingStaff(ItemStack oldStack, ItemStack newStack) {
        if (oldStack.isOf(ModItems.CREAKING_STAFF) && newStack.isOf(ModItems.CREAKING_STAFF)) {
            ItemStack oldCopy = oldStack.copy();
            ItemStack newCopy = newStack.copy();

            // Remove the components that change frequently and should not trigger a re-equip animation.
            oldCopy.remove(ModComponents.IS_WATCHED);
            newCopy.remove(ModComponents.IS_WATCHED);
            oldCopy.remove(ModComponents.IS_CHARGED);
            newCopy.remove(ModComponents.IS_CHARGED);

            // If the stacks are identical after removing the volatile components,
            // return true to pretend they are equal, suppressing the animation.
            if (ItemStack.areEqual(oldCopy, newCopy)) {
                return true;
            }
        }
        // For all other items, or for significant changes to the staff, use the default comparison.
        return ItemStack.areEqual(oldStack, newStack);
    }
}