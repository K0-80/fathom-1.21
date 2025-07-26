package com.k080.fathom.entity.block;

import com.k080.fathom.entity.ModBlockEntitys;
import com.k080.fathom.entity.ModEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class AmethystResonatorBlockEntity extends BlockEntity {

    private boolean eventActive = false;
    private boolean charged = false;
    private boolean displayingSequence = false;

    private int currentRound = 0;
    private int playerInputIndex = 0;
    private int sequenceDisplayIndex = 0;
    private int ticksSinceDisplay = 0;

    private List<BlockPos> sequence = new ArrayList<>();
    private List<BlockPos> validBuddingAmethysts = new ArrayList<>();

    private static final int REQUIRED_AMETHYST_BLOCKS = 40; //40
    private static final int REQUIRED_BUDDING_AMETHYSTS = 20; //20
    private static final int SCAN_RADIUS = 10;
    private static final int[] ROUND_SIZES = {0, 1, 2, 3, 5, 10}; // Index 0 is unused
    //private static final int[] ROUND_SIZES = {0, 1, 1, 1, 1, 1}; //for testing


    public AmethystResonatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntitys.AMETHYST_RESONATOR_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putBoolean("charged", this.charged);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.charged = nbt.getBoolean("charged");
    }

    public static void tick(World world, BlockPos pos, BlockState state, AmethystResonatorBlockEntity be) {
        if (world instanceof ServerWorld serverWorld) {
            if (be.isCharged()) {
            serverWorld.spawnParticles(ParticleTypes.REVERSE_PORTAL, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0.6, 0.6, 0.6, 0.01);
            serverWorld.spawnParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0.6, 0.6, 0.6, 0.01);
            }

            if (be.eventActive && be.displayingSequence) {
            be.ticksSinceDisplay++;
            if (be.ticksSinceDisplay >= 20) { // 1 second delay
                be.ticksSinceDisplay = 0;

                if (be.sequenceDisplayIndex < be.sequence.size()) {
                    BlockPos targetPos = be.sequence.get(be.sequenceDisplayIndex);
                    serverWorld.spawnParticles(ParticleTypes.END_ROD,
                            targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5,
                            20, 0.5, 0.5, 0.5, 0.1);
                    world.playSound(null, targetPos, SoundEvents.BLOCK_AMETHYST_CLUSTER_HIT, SoundCategory.BLOCKS, 1.0f, 1.5f + world.random.nextFloat() * 0.5f);
                    be.sequenceDisplayIndex++;
                } else {
                    // Sequence display finished, player can now break blocks
                    be.displayingSequence = false;
                    world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 0.5f, 2.0f);
                }
            }
        }
        }
    }

    public void startEvent(PlayerEntity player) {
        if (this.world == null || this.world.isClient()) return;

        if (scanAndValidateInitialStructure()) {
            this.eventActive = true;
            this.currentRound = 1;
            player.sendMessage(Text.literal("The resonator hums to life!"), true);
            startNextRound();
        } else {
            player.sendMessage(Text.literal("The resonator remains dormant."), true);
        }
    }

    private boolean scanAndValidateInitialStructure() {
        this.validBuddingAmethysts.clear();
        int amethystBlocks = 0;

        for (BlockPos p : BlockPos.iterate(pos.add(-SCAN_RADIUS, -SCAN_RADIUS, -SCAN_RADIUS), pos.add(SCAN_RADIUS, SCAN_RADIUS, SCAN_RADIUS))) {
            BlockState state = world.getBlockState(p);
            if (state.isOf(Blocks.AMETHYST_BLOCK)) {
                amethystBlocks++;
            } else if (state.isOf(Blocks.BUDDING_AMETHYST)) {
                this.validBuddingAmethysts.add(p.toImmutable());
            }
        }

        return amethystBlocks >= REQUIRED_AMETHYST_BLOCKS && this.validBuddingAmethysts.size() >= REQUIRED_BUDDING_AMETHYSTS;
    }

    private void startNextRound() {
        if (currentRound > 5) {
            completeEvent();
            return;
        }

        this.playerInputIndex = 0;
        this.sequence.clear();
        int sequenceSize = ROUND_SIZES[currentRound];
        if (this.validBuddingAmethysts.size() < sequenceSize) {
            failEvent();
            return;
        }

        List<BlockPos> availableBlocks = new ArrayList<>(this.validBuddingAmethysts);
        Collections.shuffle(availableBlocks, new Random());

        this.sequence = availableBlocks.stream().limit(sequenceSize).collect(Collectors.toList());

        this.displayingSequence = true;
        this.sequenceDisplayIndex = 0;
        this.ticksSinceDisplay = 0;
        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1.0f, 1.5f);
    }

    public void onBuddingAmethystBroken(BlockPos brokenPos) {
        if (!this.eventActive || this.displayingSequence || this.world == null) return;
        if (playerInputIndex >= sequence.size()) return;

        BlockPos expectedPos = this.sequence.get(playerInputIndex);
        if (expectedPos.equals(brokenPos)) {
            playerInputIndex++;
            this.validBuddingAmethysts.remove(brokenPos);

            world.playSound(null, brokenPos, SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, SoundCategory.BLOCKS, 1.0f, 2.0f);
            ((ServerWorld) world).spawnParticles(ParticleTypes.HAPPY_VILLAGER, brokenPos.getX() + 0.5, brokenPos.getY() + 0.5, brokenPos.getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0.1);

            if (playerInputIndex >= sequence.size()) {
                this.currentRound++;
                startNextRound();
            }
        } else {
            failEvent();
        }
    }

    private void failEvent() {
        this.eventActive = false;
        this.currentRound = 0;
        this.playerInputIndex = 0;
        this.sequence.clear();
        this.validBuddingAmethysts.clear();
        this.displayingSequence = false;

        if(world != null) {
            world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            ((ServerWorld) world).spawnParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 50, 0.6, 0.6, 0.6, 0.01);
        }
    }

    private void completeEvent() {
        this.eventActive = false;
        this.charged = true;
        this.markDirty();
        if(world != null) {
            world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1f, 1.8f);
            world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.BLOCKS, 1.3f, 0.2f);
            world.playSound(null, pos, SoundEvents.ENTITY_BREEZE_CHARGE, SoundCategory.BLOCKS, 1f, 0.2f);

            ((ServerWorld) world).spawnParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 20, 0.5, 1.0, 0.5, 0.5);
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
        }
    }



    public boolean isEventActive() { return this.eventActive; }
    public boolean isCharged() { return this.charged; }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {return createNbt(registryLookup);}

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }
}