package com.k080.fathom.entity.client;

import com.k080.fathom.component.ModComponents;
import com.k080.fathom.entity.custom.AnchorProjectileEntity;
import com.k080.fathom.item.ModItems;
import com.k080.fathom.item.custom.AnchorItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.UUID;

public class AnchorProjectileRenderer extends EntityRenderer<AnchorProjectileEntity> {
    private final ItemRenderer itemRenderer;
    private static final ItemStack ANCHOR_STACK = new ItemStack(ModItems.ANCHOR);
    private static final Identifier CHAIN_TEXTURE = Identifier.of("fathom", "textures/entity/projectiles/anchor_chain.png");

    private boolean wasReturning = false;
    private float transitionStartYaw;
    private float transitionStartPitch;
    private int transitionStartTick = -1;
    private static final int TRANSITION_DURATION_TICKS = 3;

    public AnchorProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(AnchorProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0.0, 0.15, 0.0);

        boolean isCurrentlyReturning = entity.isReturning();

        if (isCurrentlyReturning && !this.wasReturning) {
            this.transitionStartTick = entity.age;
            this.transitionStartYaw = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw());
            this.transitionStartPitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch());
        }
        this.wasReturning = isCurrentlyReturning;


        float renderYaw;
        float renderPitch;

        if (isCurrentlyReturning) {
            float targetYaw;
            float targetPitch;
            Entity owner = entity.getOwner();
            if (owner instanceof PlayerEntity player) {
                Hand hand = getAnchorHand(player, entity);
                if (hand != null) {
                    Vec3d handPos = getHandPosition(player, hand, tickDelta);
                    Vec3d interpolatedEntityPos = new Vec3d(
                            MathHelper.lerp(tickDelta, entity.prevX, entity.getX()),
                            MathHelper.lerp(tickDelta, entity.prevY, entity.getY()),
                            MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ())
                    );
                    Vec3d pointAwayDir = interpolatedEntityPos.subtract(handPos);

                    targetYaw = (float) (MathHelper.atan2(pointAwayDir.x, pointAwayDir.z) * 180.0 / Math.PI);
                    targetPitch = (float) (MathHelper.atan2(pointAwayDir.y, pointAwayDir.horizontalLength()) * 180.0 / Math.PI);
                } else {
                    targetYaw = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) + 180.0f;
                    targetPitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch());
                }
            } else {
                targetYaw = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) + 180.0f;
                targetPitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch());
            }

            if (this.transitionStartTick != -1) {
                float timeSinceStart = (entity.age - this.transitionStartTick) + tickDelta;
                if (timeSinceStart < TRANSITION_DURATION_TICKS) {
                    float progress = MathHelper.clamp(timeSinceStart / TRANSITION_DURATION_TICKS, 0.0f, 1.0f);
                    renderYaw = MathHelper.lerpAngleDegrees(progress, this.transitionStartYaw, targetYaw);
                    renderPitch = MathHelper.lerp(progress, this.transitionStartPitch, targetPitch);
                } else {
                    renderYaw = targetYaw;
                    renderPitch = targetPitch;
                    this.transitionStartTick = -1;
                }
            } else {
                renderYaw = targetYaw;
                renderPitch = targetPitch;
            }
        } else {
            renderYaw = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw());
            renderPitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch());
            this.transitionStartTick = -1;
        }

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(renderYaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-renderPitch));

        matrices.translate(0.0, 0.0, -0.35);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(135f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));
        this.itemRenderer.renderItem(ANCHOR_STACK, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), entity.getId());
        matrices.pop();

        renderChain(entity, tickDelta, matrices, vertexConsumers, light);

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    private void renderChain(AnchorProjectileEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Entity owner = entity.getOwner();
        if (!(owner instanceof PlayerEntity player) || player.isInvisible()) {
            return;
        }

        Hand hand = getAnchorHand(player, entity);
        if (hand == null) {
            return;
        }

        Vec3d handPos = getHandPosition(player, hand, tickDelta);
        Vec3d interpolatedEntityPos = new Vec3d(
                MathHelper.lerp(tickDelta, entity.prevX, entity.getX()),
                MathHelper.lerp(tickDelta, entity.prevY, entity.getY()),
                MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ())
        );

        Vec3d startPosRelative = handPos.subtract(interpolatedEntityPos);
        Vec3d endPosRelative = new Vec3d(0.0, entity.getHeight() / 2.0, 0.0);

        drawChain(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(CHAIN_TEXTURE)), startPosRelative, endPosRelative, light);
    }

    private Hand getAnchorHand(PlayerEntity player, AnchorProjectileEntity anchor) {
        UUID anchorUuid = anchor.getUuid();

        ItemStack mainHandStack = player.getMainHandStack();
        if (mainHandStack.getItem() instanceof AnchorItem) {
            Optional<UUID> mainHandUuid = mainHandStack.get(ModComponents.THROWN_ANCHOR_UUID);
            if (mainHandUuid != null && mainHandUuid.isPresent() && mainHandUuid.get().equals(anchorUuid)) {
                return Hand.MAIN_HAND;
            }
        }

        ItemStack offHandStack = player.getOffHandStack();
        if (offHandStack.getItem() instanceof AnchorItem) {
            Optional<UUID> offHandUuid = offHandStack.get(ModComponents.THROWN_ANCHOR_UUID);
            if (offHandUuid != null && offHandUuid.isPresent() && offHandUuid.get().equals(anchorUuid)) {
                return Hand.OFF_HAND;
            }
        }
        return null;
    }

    private Vec3d getHandPosition(PlayerEntity player, Hand hand, float tickDelta) {
        int side = ((player.getMainArm() == Arm.RIGHT) == (hand == Hand.MAIN_HAND)) ? -1 : 1;

        if (this.dispatcher.targetedEntity == player && MinecraftClient.getInstance().options.getPerspective().isFirstPerson()) {
            Vec3d cameraPos = this.dispatcher.camera.getPos();
            float yaw = MathHelper.lerp(tickDelta, player.prevYaw, player.getYaw());
            Vec3d rightVec = Vec3d.fromPolar(0, yaw - 90);
            return cameraPos.add(rightVec.multiply(side * 0.5)).add(0, -0.4, 0).add(Vec3d.fromPolar(0, yaw).multiply(0.1));
        }

        float swingProgress = player.getHandSwingProgress(tickDelta);
        float swingAmount = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
        float bodyYaw = MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw) * ((float)Math.PI / 180);
        double sinBodyYaw = Math.sin(bodyYaw);
        double cosBodyYaw = Math.cos(bodyYaw);

        double handXOffset = (double)side * 0.45;
        double handYOffset = -0.6;
        double handZOffset = 0.1;

        double playerX = MathHelper.lerp(tickDelta, player.prevX, player.getX());
        double playerY = MathHelper.lerp(tickDelta, player.prevY, player.getY()) + player.getStandingEyeHeight();
        double playerZ = MathHelper.lerp(tickDelta, player.prevZ, player.getZ());

        if (player.isSneaking()) {
            playerY -= 0.45;
        }

        double worldHandX = playerX + (cosBodyYaw * handXOffset - sinBodyYaw * handZOffset);
        double worldHandY = playerY + handYOffset - (swingAmount * 0.5);
        double worldHandZ = playerZ + (sinBodyYaw * handXOffset + cosBodyYaw * handZOffset);

        return new Vec3d(worldHandX, worldHandY, worldHandZ);
    }

    private static void drawChain(MatrixStack matrices, VertexConsumer vertexConsumer, Vec3d start, Vec3d end, int light) {
        float width = 0.6f;
        float halfWidth = width / 2.0f;

        Vec3d chainVec = end.subtract(start);
        float length = (float) chainVec.length();
        if (length < 1e-5) return;

        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();

        Vector3f chainDir = new Vector3f((float)chainVec.x, (float)chainVec.y, (float)chainVec.z).normalize();
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        if (Math.abs(chainDir.dot(up)) > 0.999f) {
            up = new Vector3f(1.0f, 0.0f, 0.0f);
        }

        Vector3f right = new Vector3f(chainDir).cross(up).normalize();
        Vector3f chainUp = new Vector3f(right).cross(chainDir).normalize();

        float vEnd = length / width;

        addQuad(vertexConsumer, positionMatrix, start, end, light, halfWidth, vEnd, chainUp);
        addQuad(vertexConsumer, positionMatrix, start, end, light, halfWidth, vEnd, right);
    }

    private static void addQuad(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Vec3d start, Vec3d end, int light, float halfWidth, float vEnd, Vector3f offsetDir) {
        Vector3f start1 = new Vector3f((float)start.x - offsetDir.x() * halfWidth, (float)start.y - offsetDir.y() * halfWidth, (float)start.z - offsetDir.z() * halfWidth);
        Vector3f end1 = new Vector3f((float)end.x - offsetDir.x() * halfWidth, (float)end.y - offsetDir.y() * halfWidth, (float)end.z - offsetDir.z() * halfWidth);
        Vector3f end2 = new Vector3f((float)end.x + offsetDir.x() * halfWidth, (float)end.y + offsetDir.y() * halfWidth, (float)end.z + offsetDir.z() * halfWidth);
        Vector3f start2 = new Vector3f((float)start.x + offsetDir.x() * halfWidth, (float)start.y + offsetDir.y() * halfWidth, (float)start.z + offsetDir.z() * halfWidth);

        addVertex(vertexConsumer, positionMatrix, start1, 0, 0, light);
        addVertex(vertexConsumer, positionMatrix, end1, 0, vEnd, light);
        addVertex(vertexConsumer, positionMatrix, end2, 1, vEnd, light);
        addVertex(vertexConsumer, positionMatrix, start2, 1, 0, light);
    }

    private static void addVertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Vector3f pos, float u, float v, int light) {
        vertexConsumer.vertex(positionMatrix, pos.x(), pos.y(), pos.z())
                .color(255, 255, 255, 255)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(0, 1, 0);
    }

    @Override
    public Identifier getTexture(AnchorProjectileEntity entity) {
        return null;
    }
}