package com.k080.fathom.entity.client;

import com.k080.fathom.entity.custom.AmethystShardProjectileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class AmethystShardProjectileEntityRenderer extends EntityRenderer<AmethystShardProjectileEntity> {

    private final ItemRenderer itemRenderer;
    private static final ItemStack AMETHYST_SHARD_STACK = new ItemStack(Items.AMETHYST_SHARD);
    private static final float SCALE = 0.6f;

    public AmethystShardProjectileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(AmethystShardProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        matrices.scale(SCALE, SCALE, SCALE);

        float entityYaw = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw());
        float entityPitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - entityYaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-entityPitch));

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45.0f));


        this.itemRenderer.renderItem(
                AMETHYST_SHARD_STACK,
                ModelTransformationMode.FIXED,
                light,
                OverlayTexture.DEFAULT_UV,
                matrices,
                vertexConsumers,
                entity.getWorld(),
                entity.getId()
        );

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(AmethystShardProjectileEntity entity) {
        // Since we are rendering a model, this should point to the atlas that contains the item texture
        return net.minecraft.client.texture.SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}