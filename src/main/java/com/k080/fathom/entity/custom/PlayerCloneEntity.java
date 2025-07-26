package com.k080.fathom.entity.custom;

import com.k080.fathom.component.ModComponents;
import com.k080.fathom.item.custom.Mirageitem;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class PlayerCloneEntity extends PathAwareEntity {

    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(PlayerCloneEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<String> PLAYER_MODEL = DataTracker.registerData(PlayerCloneEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> SHATTER_LEVEL = DataTracker.registerData(PlayerCloneEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private GameProfile serverOwnerProfile;

    @Nullable
    private PlayerEntity owner;

    public PlayerCloneEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.initGoals();
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(OWNER_UUID, Optional.empty());
        builder.add(PLAYER_MODEL, "default");
        builder.add(SHATTER_LEVEL, 0);
    }

    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(6, new LookAtEntityGoal(this, LivingEntity.class, 8.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));
    }


    public static DefaultAttributeContainer.Builder createPlayerCloneAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3);
    }

    public GameProfile getOwnerProfile() {
        if (this.getWorld().isClient()) {
            Optional<UUID> uuid = this.getDataTracker().get(OWNER_UUID);
            String name = this.getDataTracker().get(PLAYER_MODEL);

            return uuid.map(u -> new GameProfile(u, name))
                    .orElse(new GameProfile(UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7"), "Steve"));
        }
        if (this.serverOwnerProfile == null) {
            return new GameProfile(UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7"), "Steve");
        }
        return this.serverOwnerProfile;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            boolean shouldDespawn = this.age > 20 * 15 || (this.owner != null && (this.owner.isRemoved() || this.owner.isDead()));
            if (shouldDespawn) {
                this.cleanupAndDiscard(true);
            }
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.getWorld().isClient() || this.isRemoved()) {
            return true;
        }
        if (source.getAttacker() != null && source.getAttacker().equals(this.owner)) {  //cant be attacked by onwer to make it shatter more balanced
            return false;
        }
        if (!(source.getAttacker() instanceof PlayerEntity)) {
            return false;
        }
        cleanupAndDiscard(true);
        return true;
    }


    public void copyFrom(PlayerEntity player) {
        this.owner = player;
        this.serverOwnerProfile = player.getGameProfile();
        this.getDataTracker().set(OWNER_UUID, Optional.of(player.getUuid()));
        this.getDataTracker().set(PLAYER_MODEL, player.getGameProfile().getName());

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.equipStack(slot, player.getEquippedStack(slot).copy());
        }
        this.setHealth(player.getHealth());
    }



    void cleanupAndDiscard(boolean playEffects) {
        if (this.getWorld().isClient() || this.isRemoved()) {
            return;
        }

        shatterOnTeleport();

        if (playEffects) {
            ServerWorld world = (ServerWorld) this.getWorld();{
                world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, this.getSoundCategory(), 1.0f, 0.6f);
                world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, this.getSoundCategory(), 0.8f, 0.6f);
            }
            world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.AMETHYST_BLOCK.getDefaultState()),
                    this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5),
                    30, 0.3, 0.5, 0.3, 0.0);
        }

        if (this.owner != null) {
            for (int i = 0; i < this.owner.getInventory().size(); ++i) {
                ItemStack itemStack = this.owner.getInventory().getStack(i);
                if (itemStack.getItem() instanceof Mirageitem) {
                    UUID cloneUuid = itemStack.get(ModComponents.CLONE_UUID);
                    if (this.getUuid().equals(cloneUuid)) {
                        itemStack.remove(ModComponents.CLONE_UUID);
                        if (playEffects) {
                            this.owner.sendMessage(Text.literal("Your mirage has faded.").formatted(Formatting.GRAY), true);
                        }
                        break;
                    }
                }
            }
        }
        this.discard();
    }

    public void shatterOnTeleport() {
        if (this.getWorld().isClient() || this.isRemoved()) {
            return;
        }

        int shatterLevel = this.getShatterLevel();
        if (shatterLevel > 0) {
            ServerWorld world = (ServerWorld) this.getWorld();

            world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_GLASS_BREAK, this.getSoundCategory(), 0.8f, 0.3f);
            world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, this.getSoundCategory(), 0.4f, 2f);
            world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_HURT_FREEZE, this.getSoundCategory(), 0.8f, 0.3f);

            world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.GLASS.getDefaultState()),
                    this.getX(), this.getBodyY(0.5), this.getZ(),
                    150, 4.0, 2.0, 4.0, 0.2);

            float damage = shatterLevel * 1f;
            world.getOtherEntities(this, this.getBoundingBox().expand(4.0), entity -> entity instanceof LivingEntity && entity != this.owner)
                    .forEach(entity -> entity.damage(this.getDamageSources().magic(), damage));
        }
    }

    @Override
    public void onDamaged(DamageSource damageSource) {
    }
    @Override
    protected void playHurtSound(DamageSource source) {
    }
    @Override
    protected void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {
    }

    public void setShatterLevel(int level) { this.getDataTracker().set(SHATTER_LEVEL, level); }
    public int getShatterLevel() { return this.getDataTracker().get(SHATTER_LEVEL); }
}