package me.leaf.devs.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.leaf.devs.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderType;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;

import java.awt.*;

public class VFXTest extends Effects {

    public VFXTest(Level level, Vec3 position) {
        super(level, position);
    }

    private static RenderTypeToken getRenderTypeToken() {
        return RenderTypeToken.createToken(new ResourceLocation(Main.MODID, "textures/blocks/blue_sapphire.png"));
    }

    private static final LodestoneRenderType RENDER_LAYER = LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE.applyWithModifierAndCache(
            getRenderTypeToken(), b -> b.replaceVertexFormat(VertexFormat.Mode.TRIANGLES));

    @Override
    public boolean render(PoseStack ps) {
        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld();
        builder.replaceBufferSource(RenderHandler.DELAYED_RENDER.getTarget())
                .setRenderType(RENDER_LAYER)

        ;

        Vec3 playerPos = Minecraft.getInstance().player.position();
        Vec3 relativePos = position.subtract(playerPos);

        ps.pushPose();
        ps.translate(relativePos.x, relativePos.y, relativePos.z);
        builder.renderSphere(ps, 4, 32, 36);
        ps.popPose();
        return false;
    }
}