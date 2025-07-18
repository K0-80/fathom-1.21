package com.k080.fathom.item.custom;

import com.k080.fathom.entity.ModEntities;
import com.k080.fathom.entity.custom.AnchorProjectileEntity;
import com.k080.fathom.item.ModToolMaterials;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class AnchorItem extends SwordItem {
    public AnchorItem(ModToolMaterials anchor, Settings settings) {
        super(ModToolMaterials.ANCHOR, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 0));
        World world = target.getWorld();
        world.playSound(null, target.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 0.2f, 0.9f);
        world.playSound(null, target.getBlockPos(), SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.PLAYERS, 2.0f, 1.0f);
        world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED, SoundCategory.PLAYERS, 0.5f, 1.2f);
        return super.postHit(stack, target, attacker);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!world.isClient) {
            AnchorProjectileEntity anchorProjectile = new AnchorProjectileEntity(ModEntities.ANCHOR_PROJECTILE, world);
            anchorProjectile.setOwner(user);
            anchorProjectile.setPosition(user.getEyePos().getX(), user.getEyePos().getY(), user.getEyePos().getZ());
            anchorProjectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
            world.spawnEntity(anchorProjectile);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        user.getItemCooldownManager().set(this, 20);
        return TypedActionResult.success(itemStack, world.isClient());
    }

}