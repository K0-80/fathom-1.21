package com.k080.fathom.mixin;

import com.k080.fathom.util.CauldronInteractions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    @Unique
    private BlockPos fathom$prevBlockPos;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void fathom$onTick(CallbackInfo ci) {
        if (this.getWorld().isClient()) {
            return;
        }

        BlockPos currentPos = this.getBlockPos();
        if (fathom$prevBlockPos == null) {
            fathom$prevBlockPos = currentPos;
        }

        if (!currentPos.equals(fathom$prevBlockPos)) {
            BlockState state = this.getWorld().getBlockState(currentPos);
            if (state.isOf(Blocks.WATER_CAULDRON)) {
                CauldronInteractions.tryCraftVoodooDoll(this.getWorld(), currentPos);
            }
            fathom$prevBlockPos = currentPos;
        }
    }
}