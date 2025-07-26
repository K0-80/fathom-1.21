package com.k080.fathom.item.custom;

import com.k080.fathom.component.ModComponents;
import com.k080.fathom.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TornPageItem extends Item {
    public TornPageItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack pageStack = player.getStackInHand(hand);
        Identifier pageIdToAdd = pageStack.get(ModComponents.TORN_PAGE_ID);

        if (pageIdToAdd == null) {
            return TypedActionResult.fail(pageStack);
        }

        // Search the player's inventory for the picture book
        Inventory playerInventory = player.getInventory();
        for (int i = 0; i < playerInventory.size(); i++) {
            ItemStack potentialBookStack = playerInventory.getStack(i);

            if (potentialBookStack.isOf(ModItems.PICTURE_BOOK)) {
                Set<Identifier> currentPages = potentialBookStack.getOrDefault(ModComponents.UNLOCKED_PAGES, Set.of());

                // If the book already has this page, skip to the next book (if they have multiple)
                if (currentPages.contains(pageIdToAdd)) {
                    continue;
                }

                // Add the page to the first book found that is missing it
                Set<Identifier> newPages = new HashSet<>(currentPages);
                newPages.add(pageIdToAdd);
                potentialBookStack.set(ModComponents.UNLOCKED_PAGES, newPages);

                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1.0F, 1.0F);

                if (!player.getAbilities().creativeMode) {
                    pageStack.decrement(1);
                }

                return TypedActionResult.success(pageStack, world.isClient());
            }
        }

        // Return fail if no book was found or all books already had the page
        return TypedActionResult.fail(pageStack);
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        Identifier pageId = stack.get(ModComponents.TORN_PAGE_ID);
        if (pageId != null) {
            tooltip.add(Text.translatable("tooltip.fathom.torn_page." + pageId.getPath()).formatted(Formatting.DARK_GRAY));
        }
        tooltip.add(Text.translatable("tooltip.fathom.torn_page.hint").formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);
    }
}