package com.k080.fathom.item.custom;

import com.k080.fathom.component.ModComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Optional;

public class VoodooDollItem extends Item{
    public VoodooDollItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        Optional.ofNullable(stack.get(ModComponents.SAMPLED_PLAYER_DATA)).ifPresent(playerData ->
                tooltip.add(Text.translatable("item.fathom.voodoo_doll.tooltip.bound_to", playerData.name()).formatted(Formatting.GRAY))
        );
        super.appendTooltip(stack, context, tooltip, type);
    }
}