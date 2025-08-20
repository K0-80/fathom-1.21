//package com.k080.fathom.entity.client;
//
//import com.k080.fathom.entity.custom.MirageThrowEntity;
//import com.k080.fathom.item.ModItems;
//import net.minecraft.client.render.OverlayTexture;
//import net.minecraft.client.render.VertexConsumerProvider;
//import net.minecraft.client.render.entity.EntityRenderer;
//import net.minecraft.client.render.entity.EntityRendererFactory;
//import net.minecraft.client.render.item.ItemRenderer;
//import net.minecraft.client.render.model.json.ModelTransformationMode;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.math.RotationAxis;
//
//public class MirageThrowEntityRender extends EntityRenderer<MirageThrowEntity> {
//    private final ItemRenderer itemRenderer;
//
//    public MirageThrowEntityRender(EntityRendererFactory.Context context) {
//        super(context);
//        this.itemRenderer = context.getItemRenderer();
//    }
//
//    @Override
//    public void render(MirageThrowEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
//        matrices.push();
//
//        matrices.translate(0.0, 0.0, 0.0);
//        matrices.scale(0.6f, 0.6f, 0.6f);
//        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getYaw()));
//        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-entity.getPitch() + 90));
//
//        this.itemRenderer.renderItem(
//                entity.getStack(),
//                ModelTransformationMode.FIXED,
//                light,
//                OverlayTexture.DEFAULT_UV,
//                matrices,
//                vertexConsumers,
//                entity.getWorld(),
//                entity.getId()
//        );
//
//        matrices.pop();
//        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
//    }
//
//
//
//    @Override
//    public Identifier getTexture(MirageThrowEntity entity) {
//        return null;
//    }
//}
