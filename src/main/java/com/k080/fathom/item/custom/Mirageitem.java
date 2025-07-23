package com.k080.fathom.item.custom;

import com.k080.fathom.component.ModDataComponentTypes;
import com.k080.fathom.entity.ModEntities;
import com.k080.fathom.entity.custom.PlayerCloneEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;


import java.util.List;
import java.util.UUID;

public class Mirageitem extends SwordItem {

    public Mirageitem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient()) {
            ServerWorld serverWorld = (ServerWorld) world;
            UUID cloneUuid = stack.get(ModDataComponentTypes.CLONE_UUID);

            if (cloneUuid != null) {
                Entity clone = serverWorld.getEntity(cloneUuid);

                if (clone instanceof PlayerCloneEntity && clone.isAlive()) {
                    user.teleport(clone.getX(), clone.getY(), clone.getZ(), false);
                    clone.discard();
                    world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }

                stack.remove(ModDataComponentTypes.CLONE_UUID);

            } else {
                PlayerCloneEntity clone = new PlayerCloneEntity(ModEntities.PLAYER_CLONE, world);
                clone.copyFrom(user);
                clone.refreshPositionAndAngles(user.getX(), user.getY(), user.getZ(), user.getYaw(), user.getPitch());
                world.spawnEntity(clone);

                stack.set(ModDataComponentTypes.CLONE_UUID, clone.getUuid());
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }

            user.getItemCooldownManager().set(this, 20); // 1-second cooldown
            return TypedActionResult.success(stack);
        }

        return TypedActionResult.pass(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        UUID cloneUuid = stack.get(ModDataComponentTypes.CLONE_UUID);

        if (cloneUuid != null) {
            tooltip.add(Text.literal("Clone Active").formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.literal("No Clone").formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient() && entity instanceof PlayerEntity && world.getTime() % 20 == 0) {
            UUID cloneUuid = stack.get(ModDataComponentTypes.CLONE_UUID);
            if (cloneUuid != null) {
                Entity clone = ((ServerWorld) world).getEntity(cloneUuid);
                if (clone == null || !clone.isAlive()) {
                    stack.remove(ModDataComponentTypes.CLONE_UUID);
                    ((PlayerEntity) entity).sendMessage(Text.literal("Your mirage has faded.").formatted(Formatting.GRAY), true);
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}