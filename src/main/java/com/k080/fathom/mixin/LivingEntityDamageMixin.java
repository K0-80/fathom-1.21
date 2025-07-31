package com.k080.fathom.mixin;

import com.k080.fathom.component.ModComponents;
import com.k080.fathom.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDamageMixin {


//    @Inject(method = "damage", at = @At("HEAD")) //show damage dealt in chat
//    private void fathom$logPlayerDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
//        Entity attacker = source.getAttacker();
//        LivingEntity target = (LivingEntity) (Object) this;
//
//        if (attacker instanceof PlayerEntity && !attacker.getWorld().isClient() && amount > 0) {
//            PlayerEntity player = (PlayerEntity) attacker;
//
//            String attackerName = player.getName().getString();
//            String targetName = target.getName().getString();
//
//            Text message = Text.literal(String.format("%s dealt %.2f damage to %s", attackerName, amount, targetName));
//            player.sendMessage(message, false);
//        }
//    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true) //voodoo doll damage cancle
    private void fathom$preventVoodooDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity target = (LivingEntity) (Object) this;
        Entity attacker = source.getAttacker();

        if (target instanceof PlayerEntity targetPlayer && attacker instanceof PlayerEntity attackerPlayer) {
            for (ItemStack stack : attackerPlayer.getInventory().main) {
                if (stack.isOf(ModItems.VOODOO_DOLL)) {
                    ModComponents.SampledPlayerData playerData = stack.get(ModComponents.SAMPLED_PLAYER_DATA);
                    if (playerData != null && playerData.uuid().equals(targetPlayer.getUuid())) {
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
            ItemStack offhandStack = attackerPlayer.getOffHandStack();
            if (offhandStack.isOf(ModItems.VOODOO_DOLL)) {
                ModComponents.SampledPlayerData playerData = offhandStack.get(ModComponents.SAMPLED_PLAYER_DATA);
                if (playerData != null && playerData.uuid().equals(targetPlayer.getUuid())) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "damage", at = @At("HEAD")) //hit sheep with stick for q tip
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;

        if (livingEntity instanceof SheepEntity sheepEntity) {
            if (!sheepEntity.getWorld().isClient && source.getAttacker() instanceof PlayerEntity player) {
                if (player.getMainHandStack().isOf(Items.STICK)) {
                    sheepEntity.playSound(SoundEvents.BLOCK_WOOL_BREAK, 1.0f, 1.0f);
                    if (!player.getAbilities().creativeMode) {
                        player.getMainHandStack().decrement(1);
                    }
                    sheepEntity.dropStack(new ItemStack(ModItems.QTIP));
                }
            }
        }
    }
}