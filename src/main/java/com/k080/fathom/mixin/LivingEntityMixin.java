package com.k080.fathom.mixin;

import com.k080.fathom.component.ModDataComponentTypes;
import com.k080.fathom.effect.ModEffects;
import com.k080.fathom.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @Inject(method = "getMovementSpeed*", at = @At("RETURN"), cancellable = true)
    private void onGetMovementSpeed(CallbackInfoReturnable<Float> cir) {
        if (this.hasStatusEffect(ModEffects.STUNNED)) {
            cir.setReturnValue(0.0f);
        }
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void onJump(CallbackInfo ci) {
        if (this.hasStatusEffect(ModEffects.STUNNED)) {
            ci.cancel();
        }
    }

    @Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
    private void applyCooldownToTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof PlayerEntity player) {
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

                    cir.setReturnValue(true);                }
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

            if (rendLevel > 0) {
                float chance = (amount / 13.5f) * (rendLevel * 0.1f);
                //attacker.sendMessage(Text.literal(String.format("Rend Chance: %.2f%%", chance * 100)), false);

                if (world.getRandom().nextFloat() < chance) {
                    int currentSouls = stack.getOrDefault(ModDataComponentTypes.SOULS, 0);
                    stack.set(ModDataComponentTypes.SOULS, Math.min(10, Math.max(0, currentSouls + 1)));

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

}