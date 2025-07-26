package com.k080.fathom.item.custom;

import com.k080.fathom.client.screen.PictureBookScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class PictureBookItem extends Item {
    public PictureBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack bookStack = player.getStackInHand(hand);
        if (world.isClient) {
            openBookScreen(bookStack);
        }
        return TypedActionResult.success(bookStack, world.isClient());
    }

    @Environment(EnvType.CLIENT)
    private void openBookScreen(ItemStack stack) {
        MinecraftClient.getInstance().setScreen(new PictureBookScreen(stack));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.fathom.picture_book.lore").formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);
    }
}