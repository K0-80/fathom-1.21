package com.k080.fathom.entity.client;

import com.k080.fathom.entity.custom.AnchorProjectileEntity;
import com.k080.fathom.item.ModItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class AnchorProjectileRenderer extends EntityRenderer<AnchorProjectileEntity> {
    private final ItemRenderer itemRenderer;
    private static final ItemStack ANCHOR_STACK = new ItemStack(ModItems.ANCHOR);

    public AnchorProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(AnchorProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        matrices.translate(0.0, 0.5, 0.0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-entity.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(135f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));


        this.itemRenderer.renderItem(ANCHOR_STACK, ModelTransformationMode.FIXED, light, 0, matrices, vertexConsumers, entity.getWorld(), entity.getId());
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(AnchorProjectileEntity entity) {
        return null;
    }
}