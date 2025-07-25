package com.k080.fathom.entity.custom;

import com.k080.fathom.block.custom.BloodCrucibleBlock;
import com.k080.fathom.entity.ModBlockEntitys;
import com.k080.fathom.item.ModItems; // Added import for ModItems
import com.k080.fathom.util.ImplementedInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity; // Added import for ItemEntity
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class BloodCrucibleBlockEntity extends BlockEntity implements ImplementedInventory {

    // Enum to manage the ritual's state machine
    public enum RitualState {
        IDLE,                // Waiting for a Nether Star
        AWAITING_SACRIFICE,  // Waiting for a pet to be killed
        AWAITING_MATERIALS,  // Ready to accept the 3 items
        SPAWNING_ITEM,
        COMPLETE             // Ritual finished, waiting to reset
    }

    private static final int SPAWN_ANIMATION_TICKS = 40;

    private RitualState currentState = RitualState.IDLE;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private int resetTimer = 0;
    private int spawnItemTimer = 0;

    public BloodCrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntitys.BLOOD_CRUCIBLE_BLOCK_ENTITY, pos, state);
    }

    // --- NBT & SYNC (Simplified) ---
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putString("ritual.state", this.currentState.name());
        nbt.putInt("ritual.resetTimer", this.resetTimer);
        nbt.putInt("ritual.spawnItemTimer", this.spawnItemTimer);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        try {
            this.currentState = RitualState.valueOf(nbt.getString("ritual.state"));
        } catch (IllegalArgumentException e) {
            this.currentState = RitualState.IDLE;
        }
        this.resetTimer = nbt.getInt("ritual.resetTimer");
        this.spawnItemTimer = nbt.getInt("ritual.spawnItemTimer");
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (this.currentState != RitualState.AWAITING_MATERIALS) {
            return false;
        }

        if (!stack.isOf(Items.NETHERITE_INGOT) && !stack.isOf(Items.ENDER_EYE)) {
            return false;
        }

        int netheriteCount = 0;
        int eyeCount = 0;
        for (ItemStack s : getItems()) {
            if (s.isOf(Items.NETHERITE_INGOT)) netheriteCount++;
            else if (s.isOf(Items.ENDER_EYE)) eyeCount++;
        }

        if (stack.isOf(Items.NETHERITE_INGOT) && netheriteCount < 2) {
            return true;
        }
        if (stack.isOf(Items.ENDER_EYE) && eyeCount < 1) {
            return true;
        }

        return false;
    }


    @Nullable @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    private void sync() {
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
            markDirty();
        }
    }

    // --- RITUAL LOGIC ---

    // Step 1: IDLE -> AWAITING_SACRIFICE
    public void startRitual(PlayerEntity player) {
        if (this.currentState == RitualState.IDLE && player.getMainHandStack().isOf(Items.NETHER_STAR)) {
            if (!player.getAbilities().creativeMode) {
                player.getMainHandStack().decrement(1);
            }
            this.currentState = RitualState.AWAITING_SACRIFICE;
            world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.BLOCKS, 1.0f, 1.6f);
            world.playSound(null, pos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.5f, 0.3f);
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 30, 0.3, 0.2, 0.3, 0.05);
            }
            world.setBlockState(pos, getCachedState().with(BloodCrucibleBlock.RITUAL_STAGE, 1), Block.NOTIFY_ALL);
            sync();
        }
    }

    // Step 2: AWAITING_SACRIFICE -> AWAITING_MATERIALS (in Mixin)
    public void acceptSacrifice() {
        if (this.currentState == RitualState.AWAITING_SACRIFICE) {
            this.currentState = RitualState.AWAITING_MATERIALS;
            world.playSound(null, pos, SoundEvents.ENTITY_PUFFER_FISH_DEATH, SoundCategory.MASTER, 1f, 0.5f);
            world.playSound(null, pos, SoundEvents.ENTITY_WARDEN_SONIC_CHARGE, SoundCategory.MASTER, 0.9f, 2f);
            world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.MASTER, 0.5f, 0.57f);
            world.playSound(null, pos, SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.MASTER, 1.56f, 1.2f);
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.SOUL, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 25, 0.5, 0.2, 0.5, 0.1);
            }
            world.setBlockState(pos, getCachedState().with(BloodCrucibleBlock.RITUAL_STAGE, 2), Block.NOTIFY_ALL);
            sync();
        }
    }

    // Step 3: AWAITING_MATERIALS -> COMPLETE
    public void tryInsertMaterial(ItemStack playerStack) {
        if (this.currentState != RitualState.AWAITING_MATERIALS) return;

        // only allow 1 eye of ender + 2 neth ingots and nothjing else
        if (!playerStack.isOf(Items.NETHERITE_INGOT) && !playerStack.isOf(Items.ENDER_EYE)) {
            return;
        }
        int netheriteCount = 0;
        int eyeCount = 0;
        for (ItemStack stackInSlot : this.inventory) {
            if (stackInSlot.isOf(Items.NETHERITE_INGOT)) netheriteCount++;
            else if (stackInSlot.isOf(Items.ENDER_EYE)) eyeCount++;
        }
        if (playerStack.isOf(Items.NETHERITE_INGOT) && netheriteCount >= 2) {
            return;
        }
        if (playerStack.isOf(Items.ENDER_EYE) && eyeCount >= 1) {
            return;
        }

        for (int i = 0; i < size(); i++) {
            if (getStack(i).isEmpty()) {
                setStack(i, playerStack.split(1));
                world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1.2f, 1.7f);
                world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.BLOCKS, 1.0f, 0.3f);
                world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED, SoundCategory.BLOCKS, 1.0f, 0.7f);

                if (world instanceof ServerWorld serverWorld) {
                    BlockStateParticleEffect bloodEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.getDefaultState());

                    serverWorld.spawnParticles(bloodEffect, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 10, 0.4, 0.2, 0.4, 0.1);
                    serverWorld.spawnParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 15, 0.5, 0.5, 0.5, 0.1);
                }
                checkCompletion();
                return;
            }
        }
    }

    // Step 4: FINSHED Y
    private void checkCompletion() {
        int netheriteCount = 0;
        int eyeCount = 0;
        for (ItemStack stack : this.inventory) {
            if (stack.isOf(Items.NETHERITE_INGOT)) netheriteCount++;
            else if (stack.isOf(Items.ENDER_EYE)) eyeCount++;
        }

        if (netheriteCount == 2 && eyeCount == 1) {
            this.currentState = RitualState.SPAWNING_ITEM;
            this.clear();
            this.spawnItemTimer = SPAWN_ANIMATION_TICKS;
            world.playSound(null, pos, SoundEvents.ENTITY_WITCH_CELEBRATE, SoundCategory.BLOCKS, 1f, 0.1f);
            world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.BLOCKS, 2f, 0.5f);
            world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1f, 0.3f);

            sync();
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, BloodCrucibleBlockEntity be) {
        if (world.isClient()) return;

        if (be.currentState == RitualState.SPAWNING_ITEM) {
            if (be.spawnItemTimer > 0) {
                be.spawnItemTimer--;

                if (world instanceof ServerWorld serverWorld) {

                    // find correct hight
                    double startY = pos.getY() + 1.1;
                    double totalRiseHeight = 1; // How high the particle stream will go
                    double progress = 1.0 - (be.spawnItemTimer / (double) SPAWN_ANIMATION_TICKS);
                    double currentY = startY + (totalRiseHeight * progress);

                    // Spawn the soul fire flame at the current height
                    serverWorld.spawnParticles(ParticleTypes.ENCHANT,
                            pos.getX() + 0.5, currentY, pos.getZ() + 0.5,
                            1, 0.3, 0.2, 0.3, 0.0);

                    // Create and spawn the "blood" particles at the current height
                    BlockStateParticleEffect bloodEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.getDefaultState());
                    serverWorld.spawnParticles(bloodEffect,
                            pos.getX() + 0.5, currentY, pos.getZ() + 0.5,
                            5, 0.3, 0.2, 0.3, 0.1);
                }
            } else {
                // Timer finished, spawn the item and move to COMPLETE state
                ItemStack scytheStack = new ItemStack(ModItems.SCYTHE);
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.3, pos.getZ() + 0.5, scytheStack);

                itemEntity.setNoGravity(true);
                itemEntity.setNeverDespawn();
                itemEntity.setPickupDelay(40);
                itemEntity.setVelocity(0, 0.02, 0);
                world.spawnEntity(itemEntity);

                be.currentState = RitualState.COMPLETE;
                be.resetTimer = 200; // block  reset timer
                be.sync();
            }
        }
        // Handle the final reset after completion
        else if (be.currentState == RitualState.COMPLETE) {
            if (be.resetTimer > 0) {
                be.resetTimer--;
            } else {
                be.currentState = RitualState.IDLE;
                world.setBlockState(pos, state.with(BloodCrucibleBlock.RITUAL_STAGE, 0), Block.NOTIFY_ALL);
                be.sync();
            }
        }
    }

    public RitualState getCurrentState() {
        return currentState;
    }
}

//gah i spent way too long commenting the code but was kinda worth it i know where everything is for the first time in my life