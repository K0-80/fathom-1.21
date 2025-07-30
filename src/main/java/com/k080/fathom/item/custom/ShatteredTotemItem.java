package com.k080.fathom.item.custom;

import com.k080.fathom.component.ModComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class ShatteredTotemItem extends Item {
    public static final int REPAIR_DURATION_TICKS =  5 * 60 * 20; // 5min x 60s x 20 tick

    public ShatteredTotemItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient() && entity instanceof PlayerEntity player) {
            if (world.getTime() % 20 == 0) {
                int repairTime = stack.getOrDefault(ModComponents.REPAIR_TIME, 0);

                if (repairTime < REPAIR_DURATION_TICKS) {
                    repairTime+= 20;
                    stack.set(ModComponents.REPAIR_TIME, repairTime);
                } else {
                    player.getInventory().setStack(slot, new ItemStack(Items.TOTEM_OF_UNDYING));
                    player.sendMessage(Text.translatable("item.fathom.shattered_totem.repaired").formatted(Formatting.GOLD), true);
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0f, 1.2f);
                }
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    public static void repairTotem(PlayerEntity player, int slot) {
        if (player.getWorld().isClient()) return;

        player.getInventory().setStack(slot, new ItemStack(Items.TOTEM_OF_UNDYING));

        // Action bar message
        player.sendMessage(Text.translatable("item.fathom.shattered_totem.repaired"), true);

        // Sound effect
        player.getWorld().playSound(
                null, // All players can hear
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                SoundCategory.PLAYERS,
                1.0f,
                1.0f
        );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.fathom.shattered_totem.tooltip.description").formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);
    }
}