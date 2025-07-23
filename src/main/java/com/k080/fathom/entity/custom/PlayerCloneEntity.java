package com.k080.fathom.entity.custom;

import com.mojang.authlib.GameProfile;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;

import java.util.Optional;
import java.util.UUID;

public class PlayerCloneEntity extends PathAwareEntity {

    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(PlayerCloneEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    private GameProfile serverOwnerProfile;

    public PlayerCloneEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.initGoals();
    }

    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(6, new LookAtEntityGoal(this, LivingEntity.class, 8.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(OWNER_UUID, Optional.empty());
    }

    public static DefaultAttributeContainer.Builder createPlayerCloneAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3);
    }

    public GameProfile getOwnerProfile() {
        if (this.getWorld().isClient()) {
            return this.getDataTracker().get(OWNER_UUID)
                    .map(uuid -> new GameProfile(uuid, "player_clone")) // Use a non-null placeholder name
                    .orElse(new GameProfile(UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7"), "Steve")); // Default fallback
        }
        if (this.serverOwnerProfile == null) {
            return new GameProfile(UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7"), "Steve");
        }
        return this.serverOwnerProfile;
    }


    public void copyFrom(PlayerEntity player) {
        this.serverOwnerProfile = player.getGameProfile();
        this.getDataTracker().set(OWNER_UUID, Optional.of(player.getUuid()));

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.equipStack(slot, player.getEquippedStack(slot).copy());
        }
        this.setHealth(player.getHealth());
    }



    @Override
    protected void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {
    }
}