package com.k080.fathom.mixin;

import com.k080.fathom.Fathom;
import com.k080.fathom.util.FishConversionUtil;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.ArrayList;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin {

    @ModifyVariable(method = "use", at = @At(value = "STORE"), ordinal = 0)

    private List<ItemStack> convertFishToEntities(List<ItemStack> originalLoot) {
        Fathom.LOGGER.info("Fishing Loot Intercepted By: " + Fathom.MOD_ID);

        FishingBobberEntity bobber = (FishingBobberEntity)(Object)this;
        List<ItemStack> remainingLoot = new ArrayList<>();

        for (ItemStack stack : originalLoot) {
            boolean wasConverted = FishConversionUtil.tryConvert(stack, bobber);
            if (!wasConverted) {
                remainingLoot.add(stack);
            } else {
                Fathom.LOGGER.info(Fathom.MOD_ID + "Handled custom fish conversion for: {}", stack.getItem().toString());
            }
        }
        return remainingLoot;
    }
}