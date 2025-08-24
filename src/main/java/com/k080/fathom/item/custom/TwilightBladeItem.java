package com.k080.fathom.item.custom;

import com.k080.fathom.component.ModComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class TwilightBladeItem extends SwordItem {
    public TwilightBladeItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    public void onChargedHit(ItemStack stack, PlayerEntity player) {
        World world = player.getWorld();
        int lightLevel = world.getLightLevel(player.getBlockPos());

        int currentUmbra = stack.getOrDefault(ModComponents.UMBRA_CHARGE, 0);
        int currentLux = stack.getOrDefault(ModComponents.LUX_CHARGE, 0);

        if (lightLevel <= 5) {
            // Umbra State: Gain 20 Umbra Charge
            int newUmbra = Math.min(100, currentUmbra + 20);
            stack.set(ModComponents.UMBRA_CHARGE, newUmbra);
        } else if (lightLevel >= 10) {
            // Lux State: Gain 20 Lux Charge
            int newLux = Math.min(100, currentLux + 20);
            stack.set(ModComponents.LUX_CHARGE, newLux);
        } else {
            // Twilight State: Gain ~1/3 charge for both (7 charge)
            int newUmbra = Math.min(100, currentUmbra + 7);
            int newLux = Math.min(100, currentLux + 7);
            stack.set(ModComponents.UMBRA_CHARGE, newUmbra);
            stack.set(ModComponents.LUX_CHARGE, newLux);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int umbraCharge = stack.getOrDefault(ModComponents.UMBRA_CHARGE, 0);
        int luxCharge = stack.getOrDefault(ModComponents.LUX_CHARGE, 0);

        tooltip.add(Text.translatable("tooltip.fathom.umbra_charge", umbraCharge).formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("tooltip.fathom.lux_charge", luxCharge).formatted(Formatting.GRAY));

        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient() || !(entity instanceof PlayerEntity player) || !selected) {
            return;
        }

        Random random = world.getRandom();
        int lightLevel = world.getLightLevel(player.getBlockPos());

        if (lightLevel <= 5) {
            // Umbra State: Retain the burst of large shadow cloud particles at feet
            if (random.nextFloat() < 0.5f) {
                int particleCount = 2 + random.nextInt(3);
                for (int i = 0; i < particleCount; i++) {
                    float scale = 3.0f + random.nextFloat() * 1.5f;
                    ParticleEffect particle = random.nextFloat() < 0.7f ?
                            new DustParticleEffect(new Vector3f(0.3f, 0.0f, 0.5f), scale) :
                            new DustParticleEffect(new Vector3f(0.05f, 0.05f, 0.05f), scale);
                    spawnFootParticles(player, particle, -0.02, 0.1);
                }
            }
        } else if (lightLevel >= 10) {
            // Lux State: A constant aura of glistening particles all over the body
            if (random.nextFloat() < 0.85f) { // High chance for a dense effect
                int particleCount = 2 + random.nextInt(2); // Spawn 2-3 particles
                for (int i = 0; i < particleCount; i++) {
                    float scale = 0.6f + random.nextFloat() * 0.3f;
                    ParticleEffect particle = random.nextFloat() < 0.6f ?
                            new DustParticleEffect(new Vector3f(1.0f, 1.0f, 0.5f), scale) :
                            new DustParticleEffect(new Vector3f(1.0f, 1.0f, 1.0f), scale);
                    spawnBodyParticles(player, particle);
                }
            }
        } else {
            // Twilight State: A magical swirl of End Rod particles orbiting the player
            if (random.nextFloat() < 0.75f) {
                spawnSwirlParticles(player);
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    // Spawns particles at the player's feet (for Umbra)
    private void spawnFootParticles(PlayerEntity player, ParticleEffect particleEffect, double yDrift, double ySpread) {
        World world = player.getWorld();
        Random random = world.getRandom();
        double radius = 0.5;
        double offsetX = random.nextDouble() * 2 * radius - radius;
        double offsetZ = random.nextDouble() * 2 * radius - radius;
        double px = player.getX() + offsetX;
        double py = player.getY() + 0.1;
        double pz = player.getZ() + offsetZ;
        double motionY = yDrift + random.nextDouble() * ySpread;
        world.addParticle(particleEffect, px, py, pz, 0, motionY, 0);
    }

    // Spawns particles randomly on the player's body (for Lux)
    private void spawnBodyParticles(PlayerEntity player, ParticleEffect particleEffect) {
        World world = player.getWorld();
        Random random = world.getRandom();
        double width = player.getWidth();
        double height = player.getHeight();

        double px = player.getX() + (random.nextDouble() - 0.5) * width;
        double py = player.getY() + random.nextDouble() * height;
        double pz = player.getZ() + (random.nextDouble() - 0.5) * width;

        world.addParticle(particleEffect, px, py, pz, 0, 0.05, 0);
    }

    // Spawns particles in an orbit around the player (for Twilight)
    private void spawnSwirlParticles(PlayerEntity player) {
        World world = player.getWorld();
        Random random = world.getRandom();

        double radius = 0.8;
        double speed = 0.5;
        double angle = (world.getTime() + random.nextInt(10)) * speed;

        double offsetX = Math.cos(angle) * radius;
        double offsetZ = Math.sin(angle) * radius;

        double px = player.getX() + offsetX;
        double py = player.getY() + (player.getHeight() / 2.0) + (random.nextDouble() - 0.5) * 0.8;
        double pz = player.getZ() + offsetZ;

        world.addParticle(ParticleTypes.END_ROD, px, py, pz, 0, 0, 0);
    }
}