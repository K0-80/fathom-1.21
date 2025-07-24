package com.k080.fathom.entity.custom;

import com.k080.fathom.Fathom;
import com.k080.fathom.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Set;
import java.util.UUID;

public class MirageThrowEntity extends PersistentProjectileEntity {
    private UUID cloneUuid;
    private int tetherDuration = -1;
    private UUID tetheredEntityUuid;

    public MirageThrowEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setCloneUuid(UUID cloneUuid) {
        this.cloneUuid = cloneUuid;
    }

    public void setTetherDuration(int tetherDuration) {
        this.tetherDuration = tetherDuration;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tetheredEntityUuid != null && !this.getWorld().isClient()) {
            ServerWorld serverWorld = (ServerWorld) this.getWorld();
            Entity clone = serverWorld.getEntity(this.cloneUuid);
            Entity tethered = serverWorld.getEntity(this.tetheredEntityUuid);

            if (clone == null || tethered == null || !tethered.isAlive() || shouldBreakTether(clone, tethered)) {
                cancelTether(tethered);
                return;
            }

            // Particles
            if (this.age % 5 == 0) {
                Vec3d startPos = clone.getPos().add(0, clone.getHeight() / 2, 0);
                Vec3d endPos = tethered.getPos().add(0, tethered.getHeight() / 2, 0);
                Vec3d vector = endPos.subtract(startPos);
                double length = vector.length();
                Vec3d direction = vector.normalize();

                for (double i = 0; i < length; i += 0.3) { // Spawn a particle every 0.3 blocks
                    Vec3d particlePos = startPos.add(direction.multiply(i));
                    serverWorld.spawnParticles(ParticleTypes.ENCHANT, particlePos.x, particlePos.y, particlePos.z, 1, 0, 0, 0, 0);
                }
            }

            // Tether expiration
            this.tetherDuration--;
            if (this.tetherDuration <= 0) {
                if (tethered instanceof LivingEntity) {
                    Fathom.LOGGER.info(" ETHER ENCHANT: Teleporting " + tethered.getName().getString() + " to clone.");
                    tethered.teleport(serverWorld, clone.getX(), clone.getY(), clone.getZ(), Set.of(), tethered.getYaw(), tethered.getPitch());
                    getWorld().playSound(null, tethered.getX(), tethered.getY(), tethered.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, tethered.getSoundCategory(), 1.0F, 1.0F);

                    if (clone instanceof PlayerCloneEntity playerClone) {
                        playerClone.cleanupAndDiscard(true);
                    }
                }
                this.discard();
            }
        }

        if (this.inGroundTime > 100) {
            this.discard();
        }
    }

    private boolean shouldBreakTether(Entity clone, Entity tethered) {
        if (clone.getPos().distanceTo(tethered.getPos()) > 25.0) {
            return true;
        }

        RaycastContext context = new RaycastContext(
                clone.getEyePos(),
                tethered.getEyePos(),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                this
        );
        return this.getWorld().raycast(context).getType() == HitResult.Type.BLOCK;
    }

    private void cancelTether(Entity tethered) {
        if (tethered != null) {
            this.getWorld().playSound(null, tethered.getX(), tethered.getY(), tethered.getZ(), SoundEvents.BLOCK_GLASS_BREAK, tethered.getSoundCategory(), 1.0F, 1.2F);
        }
        this.discard();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();
        Fathom.LOGGER.info("Projectile hit: " + hitEntity.getName().getString());

        if (this.getWorld().isClient() || hitEntity.equals(getOwner()) || this.tetheredEntityUuid != null || !(hitEntity instanceof LivingEntity)) {
            this.discard();
            return;
        }

        Fathom.LOGGER.info("Tethering to " + hitEntity.getName().getString());
        this.playSound(this.getHitSound(), 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
        this.tetheredEntityUuid = hitEntity.getUuid();
        this.setVelocity(Vec3d.ZERO);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("CloneUuid")) {
            this.cloneUuid = nbt.getUuid("CloneUuid");
        }
        if (nbt.containsUuid("TetheredEntityUuid")) {
            this.tetheredEntityUuid = nbt.getUuid("TetheredEntityUuid");
        }
        this.tetherDuration = nbt.getInt("TetherDuration");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.cloneUuid != null) {
            nbt.putUuid("CloneUuid", this.cloneUuid);
        }
        if (this.tetheredEntityUuid != null) {
            nbt.putUuid("TetheredEntityUuid", this.tetheredEntityUuid);
        }
        nbt.putInt("TetherDuration", this.tetherDuration);
    }

    public ItemStack getStack() {
        return new ItemStack(ModItems.MIRAGE);
    }

    @Override
    protected ItemStack asItemStack() {
        return this.getStack();
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.MIRAGE);
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK;
    }
}