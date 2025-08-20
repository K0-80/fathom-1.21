package com.k080.fathom.entity.client.feature;

import com.k080.fathom.component.ModComponents;
import com.k080.fathom.item.custom.Mirageitem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class AmethystShardsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private static final ItemStack AMETHYST_SHARD_STACK = new ItemStack(Items.AMETHYST_SHARD);
    private static final float ORBIT_RADIUS = 1f;
    private static final float ROTATION_SPEED = 0.04f;
    private static final float BOB_AMPLITUDE = 0.12f;
    private static final float BOB_FREQUENCY = 0.15f;
    private static final float RANDOMNESS_AMPLITUDE_RADIUS = 0.08f;
    private static final float RANDOMNESS_AMPLITUDE_Y = 0.06f;

    private final ItemRenderer itemRenderer;

    public AmethystShardsFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context, ItemRenderer itemRenderer) {
        super(context);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        int shardCount = 0;
        Inventory inventory = player.getInventory();

        for (int i = 0; i < 9; i++) { // Hotbar slots
            shardCount = getShardCount(inventory.getStack(i), shardCount);
        }
        shardCount = getShardCount(player.getOffHandStack(), shardCount);


        if (shardCount <= 0) {
            return;
        }

        float bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, player.prevBodyYaw, player.bodyYaw);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-bodyYaw));


        float baseAngle = (player.age + tickDelta) * ROTATION_SPEED;

        for (int i = 0; i < shardCount; i++) {
            matrices.push();

            float angleOffset = (float) (i * 2 * Math.PI / shardCount);
            float totalAngle = baseAngle + angleOffset;
            float orbitX = MathHelper.cos(totalAngle) * ORBIT_RADIUS;
            float orbitZ = MathHelper.sin(totalAngle) * ORBIT_RADIUS;

            float bobOffset = MathHelper.sin(((player.age + tickDelta) * BOB_FREQUENCY) + (angleOffset * 1.5f)) * BOB_AMPLITUDE;
            matrices.translate(orbitX, 0.5f + bobOffset, orbitZ);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(-totalAngle - MathHelper.HALF_PI));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            matrices.scale(0.55f, 0.55f, 0.55f);
            itemRenderer.renderItem(AMETHYST_SHARD_STACK, ModelTransformationMode.FIXED, light, 0, matrices, vertexConsumers, player.getWorld(), player.getId());

            matrices.pop();
        }
    }

    private int getShardCount(ItemStack stack, int currentMax) {
        if (stack.getItem() instanceof Mirageitem) {
            int count = stack.getOrDefault(ModComponents.SHARDS, 0);
            return Math.max(count, currentMax);
        }
        return currentMax;
    }
}