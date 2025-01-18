package me.leaf.devs.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.leaf.devs.Main;
import me.leaf.devs.registry.ShaderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.pipeline.VertexConsumerWrapper;
import org.joml.Matrix4f;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderType;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;
import org.joml.Vector3f;
import team.lodestar.lodestone.systems.rendering.shader.ShaderHolder;

import java.awt.*;

public class VFXTest extends Effects {

    public VFXTest(Level level, Vec3 position) {
        super(level, position);
    }

    private static RenderTypeToken getRenderTypeToken() {
        return RenderTypeToken.createToken(new ResourceLocation(Main.MODID, "textures/particles/beam.png"));
    }

    private static final LodestoneRenderType RENDER_LAYER = LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE.applyWithModifierAndCache(
            getRenderTypeToken(), b -> b.replaceVertexFormat(VertexFormat.Mode.QUADS)
    );

    @Override
    public boolean render(PoseStack ps) {
        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld();
        builder.replaceBufferSource(RenderHandler.DELAYED_RENDER.getTarget())
                .setRenderType(RENDER_LAYER)
                .setAlpha(1.0f)
                .setColor(134, 165, 223, 255);

        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 relativePos = position.subtract(cameraPos);

        ps.pushPose();
        ps.translate(relativePos.x, relativePos.y, relativePos.z);
        ps.scale(1,1,1);

        VertexConsumer vC = RenderHandler.DELAYED_RENDER.getTarget().getBuffer(RENDER_LAYER);
        Matrix4f pose = ps.last().pose();
        float radius = 2.4f;
        int latitudes = 32;
        int longitudes = 64;
        
        // Render sphere by creating strips of triangles
        // Render cylinder by creating strips of quads around the circumference
        int segments = 64; // Number of segments around the cylinder
        float height = 1.5f; // Height of the cylinder
        float halfHeight = height / 2.0f;

        // Draw the sides of the cylinder
        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (i * 2 * Math.PI / segments);
            float angle2 = (float) ((i + 1) * 2 * Math.PI / segments);

            float x1 = radius * (float) Math.cos(angle1);
            float z1 = radius * (float) Math.sin(angle1);
            float x2 = radius * (float) Math.cos(angle2); 
            float z2 = radius * (float) Math.sin(angle2);

            // Calculate UV coordinates - modified to repeat texture for each segment
            float u1 = (float) i / segments; // Start of texture for this segment
            float u2 = (float) (i + 1) / segments; // End of texture for this segment
            float v1 = 0; // Top of texture
            float v2 = 1; // Bottom of texture

            // Draw quad for cylinder side (front face)
            vC.vertex(pose, x1, -halfHeight, z1)
                .color(134, 165, 223, 255)
                .uv(u1, v2)
                .uv2(0xF000F0)
                .normal(x1/radius, 0, z1/radius)
                .endVertex();
            vC.vertex(pose, x2, -halfHeight, z2)
                .color(134, 165, 223, 255)
                .uv(u2, v2)
                .uv2(0xF000F0)
                .normal(x2/radius, 0, z2/radius)
                .endVertex();
            vC.vertex(pose, x2, halfHeight, z2)
                .color(134, 165, 223, 255)
                .uv(u2, v1)
                .uv2(0xF000F0)
                .normal(x2/radius, 0, z2/radius)
                .endVertex();
            vC.vertex(pose, x1, halfHeight, z1)
                .color(134, 165, 223, 255)
                .uv(u1, v1)
                .uv2(0xF000F0)
                .normal(x1/radius, 0, z1/radius)
                .endVertex();

            // Only render back faces where texture has opacity
            vC.vertex(pose, x2, -halfHeight, z2)
                .color(134, 165, 223, 255) // Set alpha to 0 to make transparent unless texture has opacity
                .uv(u1, v2)
                .uv2(0xF000F0)
                .normal(-x2/radius, 0, -z2/radius)
                .endVertex();
            vC.vertex(pose, x1, -halfHeight, z1)
                .color(134, 165, 223, 255)
                .uv(u2, v2)
                .uv2(0xF000F0)
                .normal(-x1/radius, 0, -z1/radius)
                .endVertex();
            vC.vertex(pose, x1, halfHeight, z1)
                .color(134, 165, 223, 255)
                .uv(u2, v1)
                .uv2(0xF000F0)
                .normal(-x1/radius, 0, -z1/radius)
                .endVertex();
            vC.vertex(pose, x2, halfHeight, z2)
                .color(134, 165, 223, 255)
                .uv(u1, v1)
                .uv2(0xF000F0)
                .normal(-x2/radius, 0, -z2/radius)
                .endVertex();
        }

        builder.setVertexConsumer(vC);

        builder.renderSphere(ps, 1, 24, 5);

        ps.popPose();
        return false;
    }
}