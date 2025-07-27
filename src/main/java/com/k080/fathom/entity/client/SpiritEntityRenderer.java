package com.k080.fathom.entity.client;

import com.k080.fathom.Fathom;
import com.k080.fathom.entity.custom.SpiritEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class SpiritEntityRenderer extends MobEntityRenderer<SpiritEntity, SpiritEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(Fathom.MOD_ID, "textures/entity/spirit.png");

    public SpiritEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SpiritEntityModel(context.getPart(SpiritEntityModel.SPIRIT)), 0.5f);
    }

    @Override
    public Identifier getTexture(SpiritEntity entity) {
        return TEXTURE;
    }

    @Override
    public boolean shouldRender(SpiritEntity entity, Frustum frustum, double x, double y, double z) {
        double renderDistance = 192.0;
        if (entity.squaredDistanceTo(x, y, z) > renderDistance * renderDistance) {
            return false;
        }

        if (entity.ignoreCameraFrustum) {
            return true;
        }
        return isVisible(entity);
    }
}