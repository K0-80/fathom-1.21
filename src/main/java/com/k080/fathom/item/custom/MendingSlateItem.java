package com.k080.fathom.item.custom;

import com.k080.fathom.component.ModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.item.tooltip.TooltipType;

import java.util.List;

public class MendingSlateItem extends Item {
    public MendingSlateItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onClicked(ItemStack slateStack, ItemStack targetStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType != ClickType.RIGHT || targetStack.isEmpty()) {
            return false;
        }

        if (targetStack.isDamageable() && targetStack.isDamaged() && !targetStack.contains(ModComponents.MENDING_TARGET)) {
            if (!player.getWorld().isClient) {
                targetStack.set(ModComponents.MENDING_TARGET, new ModComponents.MendingTarget(500, player.getWorld().getTime()));
                slateStack.decrement(1);
                player.incrementStat(Stats.USED.getOrCreateStat(this));
            }

            player.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);
            return true;
        }

        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.fathom.mending_slate.description").formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);
    }
}