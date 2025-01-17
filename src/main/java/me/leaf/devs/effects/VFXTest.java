package me.leaf.devs.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

import java.awt.*;

public class VFXTest extends Effects {

    public VFXTest(Level level, Vec3 position) {
        super(level, position);
    }

    @Override
    public boolean render(PoseStack ps) {

        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld();
        builder.setColor(new Color(255,255,255))
                .setAlpha(1.0f)
                .replaceBufferSource(RenderHandler.LATE_DELAYED_RENDER.getTarget())
        ;
        ps.pushPose();
        builder.renderBeam(ps.last().pose(), position, position.add(0,5,0), 2f);
        ps.popPose();
        return false;
    }
}