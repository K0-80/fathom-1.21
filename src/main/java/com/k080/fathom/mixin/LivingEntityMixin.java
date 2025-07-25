package com.k080.fathom.mixin;

import com.k080.fathom.component.ModComponents;
import com.k080.fathom.enchantment.ModEnchantments;
import com.k080.fathom.entity.block.BloodCrucibleBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
    private void applyCooldownToTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof PlayerEntity player) {  //totem now dose 60 sec cooldown instead of dispearing
            boolean hasTotem = player.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING)
                    || player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING);

            if (hasTotem) {
                if (player.getItemCooldownManager().isCoolingDown(Items.TOTEM_OF_UNDYING)) {
                    cir.setReturnValue(false);
                } else {
                    player.getItemCooldownManager().set(Items.TOTEM_OF_UNDYING, 60 * 20);

                    player.setHealth(1.0F);
                    player.clearStatusEffects();
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
                    player.getWorld().sendEntityStatus(player, EntityStatuses.USE_TOTEM_OF_UNDYING);

                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void applyRendEnchantment(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof PlayerEntity attacker) {
            LivingEntity target = (LivingEntity) (Object) this;
            World world = attacker.getWorld();
            if (world.isClient()) {
                return;
            }

            ItemStack stack = attacker.getMainHandStack();
            int rendLevel = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT)
                    .getEntry(ModEnchantments.REND)
                    .map(entry -> EnchantmentHelper.getLevel(entry, stack))
                    .orElse(0);

            if (rendLevel > 0) {   //rend enchant from hex scythe item
                float chance = (amount / 13.5f) * (rendLevel * 0.1f);
                //attacker.sendMessage(Text.literal(String.format("Rend Chance: %.2f%%", chance * 100)), false);

                if (world.getRandom().nextFloat() < chance) {
                    int currentSouls = stack.getOrDefault(ModComponents.SOULS, 0);
                    stack.set(ModComponents.SOULS, Math.min(10, Math.max(0, currentSouls + 1)));

                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 40, 0));
                    world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_PUFFER_FISH_DEATH, SoundCategory.MASTER, 0.5f, 0.5f);
                    world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_WARDEN_SONIC_CHARGE, SoundCategory.MASTER, 0.34f, 2f);
                    world.playSound(null, target.getBlockPos(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.MASTER, 0.22f, 0.57f);
                    world.playSound(null, target.getBlockPos(), SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.MASTER, 1.56f, 0.6f);

                    if (world instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(ParticleTypes.SOUL, target.getX(), target.getBodyY(0.5), target.getZ(), 8, 0.3, 0.3, 0.3, 0.05);
                    }
                }
            }
        }
    }


    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeathForCrucible(DamageSource source, CallbackInfo ci) {

        LivingEntity killedEntity = (LivingEntity) (Object) this;
        World world = killedEntity.getWorld();

        if (world.isClient()) {
            return;
        }

        Entity attacker = source.getAttacker();
        if (!(attacker instanceof PlayerEntity killer)) {
            return;
        }

        UUID petOwnerUuid = null;
        if (killedEntity instanceof TameableEntity tameableEntity) {
            petOwnerUuid = tameableEntity.getOwnerUuid();
        }

        if (petOwnerUuid == null) {
            return;
        }

        if (!killer.getUuid().equals(petOwnerUuid)) {
            return;
        }

        final int CHUNK_RADIUS = 2;
        ServerWorld serverWorld = (ServerWorld) world;
        int playerChunkX = killer.getChunkPos().x;
        int playerChunkZ = killer.getChunkPos().z;

        for (int cx = playerChunkX - CHUNK_RADIUS; cx <= playerChunkX + CHUNK_RADIUS; cx++) {
            for (int cz = playerChunkZ - CHUNK_RADIUS; cz <= playerChunkZ + CHUNK_RADIUS; cz++) {
                WorldChunk chunk = serverWorld.getChunkManager().getWorldChunk(cx, cz, false);
                if (chunk == null) {
                    continue;
                }

                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (blockEntity instanceof BloodCrucibleBlockEntity crucibleEntity) {
                        if (crucibleEntity.getCurrentState() == BloodCrucibleBlockEntity.RitualState.AWAITING_SACRIFICE) {
                            // paritlce arc
                            Vec3d startPos = killedEntity.getPos();
                            Vec3d endPos = crucibleEntity.getPos().toCenterPos().add(0, 0.3, 0);
                            double distance = startPos.distanceTo(endPos);

                            // wow mhm yes double i = 0 vec3d i understand this
                            for (double i = 0; i < distance; i += 0.2) {
                                double progress = i / distance;
                                Vec3d particlePos = startPos.lerp(endPos, progress);
                                // ah yes ofc i understand this (go to school kids)
                                double arcHeight = Math.sin(progress * Math.PI) * 1.5;

                                serverWorld.spawnParticles(ParticleTypes.SOUL,
                                        particlePos.x, particlePos.y + arcHeight, particlePos.z,
                                        1, 0.1, 0.1, 0.1, 0);
                            }
                            crucibleEntity.acceptSacrifice();
                        }
                    }
                }
            }
        }

    }


}