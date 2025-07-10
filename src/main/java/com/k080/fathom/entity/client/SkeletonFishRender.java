package com.k080.fathom.entity.client;

import com.k080.fathom.Fathom;
import com.k080.fathom.entity.custom.SkeletonFishEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class SkeletonFishRender extends MobEntityRenderer<SkeletonFishEntity, SkeletonFishModel<SkeletonFishEntity>> {
    public SkeletonFishRender(EntityRendererFactory.Context context) {
        super(context, new SkeletonFishModel<>(context.getPart(SkeletonFishModel.SKELETON_FISH)), 0.3f);
    }

    @Override
    public Identifier getTexture(SkeletonFishEntity entity) {
        return Identifier.of(Fathom.MOD_ID, "textures/entity/fish/skeleton_fish.png");
    }
}
