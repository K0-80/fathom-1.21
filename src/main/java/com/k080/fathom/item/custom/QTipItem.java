package com.k080.fathom.item.custom;

import com.k080.fathom.component.ModComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Optional;

public class QTipItem extends Item {
    public QTipItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) { //only se 1 qtip at a time (for now)
        if (target instanceof PlayerEntity playerTarget && attacker instanceof PlayerEntity playerAttacker) {
            if (stack.get(ModComponents.SAMPLED_PLAYER_DATA) == null) {
                ItemStack sampledStack = new ItemStack(this, 1);
                ModComponents.SampledPlayerData playerData = new ModComponents.SampledPlayerData(playerTarget.getUuid(), playerTarget.getName().getString());
                sampledStack.set(ModComponents.SAMPLED_PLAYER_DATA, playerData);
                playerAttacker.getInventory().offerOrDrop(sampledStack);
                stack.decrement(1);
                return true;
            }
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        Optional.ofNullable(stack.get(ModComponents.SAMPLED_PLAYER_DATA)).ifPresent(playerData ->
                tooltip.add(Text.translatable("item.fathom.qtip.tooltip.sampled_player", playerData.name()).formatted(Formatting.GRAY))
        );
        super.appendTooltip(stack, context, tooltip, type);
    }
}