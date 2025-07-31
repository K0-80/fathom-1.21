package com.k080.fathom.util;

import com.k080.fathom.component.ModComponents;
import com.k080.fathom.item.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class CauldronInteractions {
    public static void tryCraftVoodooDoll(World world, BlockPos pos) {
        if (world.isClient()) {
            return;
        }

        List<ItemEntity> itemsInCauldron = world.getEntitiesByClass(ItemEntity.class, new Box(pos), Entity::isAlive);

        int netherWartCount = 0;
        int hayBaleCount = 0;
        ItemEntity qTipEntity = null;
        List<ItemEntity> ingredients = new ArrayList<>();

        for (ItemEntity itemEntity : itemsInCauldron) {
            ItemStack stack = itemEntity.getStack();
            Item item = stack.getItem();

            if (item.equals(Items.NETHER_WART)) {
                netherWartCount += stack.getCount();
                ingredients.add(itemEntity);
            } else if (item.equals(Items.HAY_BLOCK)) {
                hayBaleCount += stack.getCount();
                ingredients.add(itemEntity);
            } else if (item.equals(ModItems.QTIP)) {
                if (qTipEntity != null || stack.getCount() != 1 || !stack.contains(ModComponents.SAMPLED_PLAYER_DATA)) {
                    return;
                }
                qTipEntity = itemEntity;
                ingredients.add(itemEntity);
            } else {
                return;
            }
        }

        if (ingredients.size() != itemsInCauldron.size()) {
            return;
        }

        if (netherWartCount == 4 && hayBaleCount == 4 && qTipEntity != null) {
            ModComponents.SampledPlayerData playerData = qTipEntity.getStack().get(ModComponents.SAMPLED_PLAYER_DATA);
            if (playerData == null) {
                return;
            }

            for (ItemEntity ingredient : ingredients) {
                ingredient.discard();
            }

            world.setBlockState(pos, Blocks.CAULDRON.getDefaultState(), 3);

            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
            lightning.setPosition(pos.toCenterPos());
            lightning.setCosmetic(true);
            world.spawnEntity(lightning);

            ItemStack voodooDollStack = new ItemStack(ModItems.VOODOO_DOLL);
            voodooDollStack.set(ModComponents.SAMPLED_PLAYER_DATA, playerData);

            ItemEntity voodooDollEntity = new ItemEntity(world,
                    pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5,
                    voodooDollStack);
            voodooDollEntity.setVelocity(Vec3d.ZERO);
            voodooDollEntity.setPickupDelay(20);
            voodooDollEntity.setNoGravity(true);
            world.spawnEntity(voodooDollEntity);
        }
    }
}