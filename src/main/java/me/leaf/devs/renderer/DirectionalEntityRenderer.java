package me.leaf.devs.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import me.leaf.devs.Main;
import me.leaf.devs.entity.DirectionalEntity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LightBlock;
import org.joml.Matrix4f;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.postprocess.PostProcessor;

import java.awt.*;

public class DirectionalEntityRenderer extends EntityRenderer<DirectionalEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/entity/summoning_circle.png");

    public DirectionalEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(DirectionalEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // Rotate entity based on its yaw and pitch
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYaw()));
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getPitch()));

        // Add continuous rotation animation
        float rotationSpeed = 0.25F; // Degrees per second
        float rotationAngle = (entity.level().getGameTime() + partialTicks) * rotationSpeed % 360;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotationAngle));

        // Bind the texture and set up rendering
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        Matrix4f matrix = poseStack.last().pose();

        float size = entity.getSize();
        float halfSize = size / 2.0F;

        // Render a flat quad facing the direction set by the entity
        vertexConsumer.vertex(matrix, -halfSize, 0, -halfSize).color(255, 255, 255, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0, 1, 0).endVertex();
        vertexConsumer.vertex(matrix, -halfSize, 0, halfSize).color(255, 255, 255, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0, 1, 0).endVertex();
        vertexConsumer.vertex(matrix, halfSize, 0, halfSize).color(255, 255, 255, 255).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0, 1, 0).endVertex();
        vertexConsumer.vertex(matrix, halfSize, 0, -halfSize).color(255, 255, 255, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0, 1, 0).endVertex();

        // Render a circular border with increased thickness and opacity
        float radius = 5.0F;

        // Add particle effects
        if (entity.level().isClientSide && entity.level().random.nextFloat() < 0.1) { // Only spawn particles 20% of ticks
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();

            // Spawn particles in random positions within the circle
            double randomRadius = Math.sqrt(entity.level().random.nextDouble()) * radius; // Distribute evenly in circle
            double randomAngle = entity.level().random.nextDouble() * Math.PI * 2;
            
            double particleX = x + (randomRadius * Math.cos(randomAngle));
            double particleZ = z + (randomRadius * Math.sin(randomAngle));

            WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
                .setScaleData(GenericParticleData.create(0.15f + entity.level().random.nextFloat() * 0.1f, 0).build())
                .setTransparencyData(GenericParticleData.create(0.6f, 0).build())
                .setColorData(ColorParticleData.create(new Color(180, 100, 255), new Color(100, 20, 200))
                    .setCoefficient(0.8f)
                    .setEasing(Easing.SINE_IN_OUT)
                    .build())
                .setLifetime(15 + entity.level().random.nextInt(10))
                .setMotion(0, 0.02 + entity.level().random.nextDouble() * 0.02, 0)
                .enableNoClip()
                .spawn(entity.level(), particleX, y + 0.1, particleZ);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(DirectionalEntity entity) {
        return TEXTURE;
    }
}
