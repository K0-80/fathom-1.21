package com.k080.fathom.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GauntletItem extends SwordItem {

    public record ComboData(int comboStep, long lastHitTick) {}
    public static final Map<UUID, ComboData> PLAYER_COMBO_DATA = new HashMap<>();

    private static final int COMBO_RESET_TIME_TICKS = 20;

    public GauntletItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial,  settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient() || !(entity instanceof PlayerEntity player)) {
            super.inventoryTick(stack, world, entity, slot, selected);
            return;
        }

        ComboData comboData = PLAYER_COMBO_DATA.get(player.getUuid());
        if (comboData != null) {
            long timeSinceLastHit = world.getTime() - comboData.lastHitTick();
            if (timeSinceLastHit > COMBO_RESET_TIME_TICKS) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 0.5f, 0.5f);
                PLAYER_COMBO_DATA.remove(player.getUuid());
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}