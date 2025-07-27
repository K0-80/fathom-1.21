package com.k080.fathom.entity.block;

import com.k080.fathom.block.ModBlocks;
import com.k080.fathom.entity.ModBlockEntitys;
import com.k080.fathom.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AnchorBlockEntity extends BlockEntity {
    private ItemStack heartOfTheSea = ItemStack.EMPTY;
    private ItemStack guardianHeart = ItemStack.EMPTY;

    private int activationTicks = 0;
    private boolean isActivating = false;
    private static final double AURA_RADIUS = 8.0; //particle thing dosnt scale with this

    public AnchorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntitys.ANCHOR_BLOCK_ENTITY, pos, state);
    }

    public boolean insertHeartOfTheSea(ItemStack stack) {
        if (this.heartOfTheSea.isEmpty() && stack.isOf(Items.HEART_OF_THE_SEA)) {
            this.heartOfTheSea = stack.split(1);
            checkActivation();
            markDirty();
            return true;
        }
        return false;
    }

    public boolean insertGuardianHeart(ItemStack stack) {
        if (this.guardianHeart.isEmpty() && stack.isOf(ModItems.GUARDIAN_HEART)) {
            this.guardianHeart = stack.split(1);
            checkActivation();
            markDirty();
            return true;
        }
        return false;
    }

    private void checkActivation() {
        if (!this.heartOfTheSea.isEmpty() && !this.guardianHeart.isEmpty()) {
            this.isActivating = true;
            this.activationTicks = 0;
            if (world != null)
                world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1.0F, 0.5F);
        }
    }


    public static void tick(World world, BlockPos pos, BlockState state, AnchorBlockEntity be) {
        if (be.isActivating) {
            if (!world.isClient) {
                tickActivation(world, pos, be);
            }
        } else {
            tickAura(world, pos);
        }
    }


    private static void tickAura(World world, BlockPos pos) {
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SQUID_INK, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 8, 8, 8, 0.01);
        }

            Box areaOfEffect = new Box(pos).expand(AURA_RADIUS);
            List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(PlayerEntity.class, areaOfEffect, p -> !p.isCreative() && !p.isSpectator());

            if (nearbyPlayers.isEmpty()) {
                return;
            }

            boolean applyLongCooldownEffects = world.getTime() % 40L == 0; // Every 2 seconds

            for (PlayerEntity player : nearbyPlayers) {
                player.addVelocity(0, -0.03, 0);

                if (applyLongCooldownEffects) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 0, false, false, true));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100, 0, false, false, true));
                    player.sendMessage(Text.literal("A presence pulls you down...").formatted(Formatting.GRAY), true);
                }
            }
    }

    private static void tickActivation(World world, BlockPos pos, AnchorBlockEntity be) {
        be.activationTicks++;

        // Phase 1: Delay (first 40 ticks)
        if (be.activationTicks < 40) return;

        // Phase 2: Particle gathering (40 to 140 ticks)
        if (be.activationTicks < 140) { //btw i 100% understand all of this yes yes
            double progress = (be.activationTicks - 40) / 100.0; // 0.0 to 1.0
            double currentRadius = 3.0 * (1.0 - progress);

            for (int i = 0; i < 2; i++) {
                double angle = world.random.nextDouble() * 2.0 * Math.PI;
                double spawnRadius = currentRadius + (world.random.nextDouble() - 0.5) * 0.5;
                double offsetX = spawnRadius * Math.cos(angle);
                double offsetZ = spawnRadius * Math.sin(angle);

                double ySine = MathHelper.sin((float) (progress * Math.PI)) * 1.5;
                double particleY = pos.getY() + 0.5 + ySine + (world.random.nextDouble() - 0.5) * 0.8;
                double particleX = pos.getX() + 0.5 + offsetX;
                double particleZ = pos.getZ() + 0.5 + offsetZ;

                double targetY = pos.getY() + 1.0;
                double vecX = (pos.getX() + 0.5) - particleX;
                double vecY = targetY - particleY;
                double vecZ = (pos.getZ() + 0.5) - particleZ;

                double length = Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);
                if (length > 0) {
                    double velocityScale = 0.15;
                    double velocityX = (vecX / length) * velocityScale;
                    double velocityY = (vecY / length) * velocityScale;
                    double velocityZ = (vecZ / length) * velocityScale;
                    ((ServerWorld) world).spawnParticles(ParticleTypes.NAUTILUS, particleX, particleY, particleZ, 0, velocityX, velocityY, velocityZ, 0.5);
                }
            }
        }

        // Phase 3: Explosion (at 140 ticks)
        if (be.activationTicks == 140) {
            world.playSound(null, pos, SoundEvents.BLOCK_ANCIENT_DEBRIS_BREAK, SoundCategory.BLOCKS, 1.7F, 0.3F);
            world.playSound(null, pos, SoundEvents.BLOCK_CHAIN_BREAK, SoundCategory.BLOCKS, 1.4F, 0.5F);

            BlockStateParticleEffect breakEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DEEPSLATE.getDefaultState());
            ((ServerWorld) world).spawnParticles(breakEffect, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.6, 0.6, 0.6, 0.1);
        }

        // Phase 4: Transformation (at 160 ticks)
        if (be.activationTicks >= 160) {
            BlockState currentState = world.getBlockState(pos);
            boolean wasWaterlogged = currentState.get(Properties.WATERLOGGED);
            world.playSound(null, pos, SoundEvents.BLOCK_CONDUIT_ACTIVATE, SoundCategory.BLOCKS, 2F, 0.3F);
            world.setBlockState(pos, ModBlocks.ANCHOR_BLOCK_ACTIVATED.getDefaultState().with(Properties.WATERLOGGED, wasWaterlogged), Block.NOTIFY_ALL);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!heartOfTheSea.isEmpty()) {
            nbt.put("heartOfTheSea", heartOfTheSea.encode(registryLookup));
        }
        if (!guardianHeart.isEmpty()) {
            nbt.put("guardianHeart", guardianHeart.encode(registryLookup));
        }
        nbt.putBoolean("isActivating", isActivating);
        nbt.putInt("activationTicks", activationTicks);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.heartOfTheSea = ItemStack.EMPTY;
        if(nbt.contains("heartOfTheSea")) {
            ItemStack.fromNbt(registryLookup, nbt.get("heartOfTheSea")).ifPresent(stack -> this.heartOfTheSea = stack);
        }
        this.guardianHeart = ItemStack.EMPTY;
        if(nbt.contains("guardianHeart")) {
            ItemStack.fromNbt(registryLookup, nbt.get("guardianHeart")).ifPresent(stack -> this.guardianHeart = stack);
        }
        this.isActivating = nbt.getBoolean("isActivating");
        this.activationTicks = nbt.getInt("activationTicks");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}