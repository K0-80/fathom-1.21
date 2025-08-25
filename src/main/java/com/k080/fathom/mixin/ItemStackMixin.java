package com.k080.fathom.mixin;

import com.k080.fathom.component.ModComponents;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "inventoryTick", at = @At("TAIL"))
    private void fathom$onInventoryTick(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (world.isClient()) {
            return;
        }

        ItemStack self = (ItemStack) (Object) this;
        ModComponents.MendingTarget component = self.get(ModComponents.MENDING_TARGET);

        if (component != null) {
            if (component.remainingRepair() <= 0 || !self.isDamaged()) {
                self.remove(ModComponents.MENDING_TARGET);
                return;
            }

            long currentTime = world.getTime();
            long lastUpdateTime = component.lastUpdateTick();
            long ticksSinceLastUpdate = currentTime - lastUpdateTime;

            if (ticksSinceLastUpdate >= 20) {
                long repairCycles = ticksSinceLastUpdate / 20;

                int repairAmount = (int) Math.min(repairCycles, Math.min(component.remainingRepair(), self.getDamage()));

                if (repairAmount > 0) {
                    self.setDamage(self.getDamage() - repairAmount);

                    int newRemainingRepair = component.remainingRepair() - repairAmount;
                    long newLastUpdateTick = lastUpdateTime + (repairCycles * 20);
                    self.set(ModComponents.MENDING_TARGET, new ModComponents.MendingTarget(newRemainingRepair, newLastUpdateTick));
                }
            }
        }
    }
}