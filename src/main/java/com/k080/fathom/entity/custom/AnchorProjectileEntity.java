package com.k080.fathom.entity.custom;

import com.k080.fathom.Fathom;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.world.World;

public class AnchorProjectileEntity extends WindChargeEntity {
    public AnchorProjectileEntity(EntityType<? extends AbstractWindChargeEntity> entityType, World world) {
        super(entityType, world);
    }
}