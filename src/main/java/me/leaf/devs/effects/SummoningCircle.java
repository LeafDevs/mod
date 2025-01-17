package me.leaf.devs.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderType;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;

import java.awt.Color;

public class SummoningCircle extends Effects{


    public SummoningCircle(Level level, Vec3 position) {
        super(level, position);
    }


    
    private int age = 0;
    private static final ResourceLocation CIRCLE_TEXTURE = new ResourceLocation("render_test:textures/entities/circle6a.png");
    private static final RenderTypeToken TOKEN = RenderTypeToken.createCachedToken(CIRCLE_TEXTURE);
    private static final LodestoneRenderType RENDER_TYPE = LodestoneRenderTypeRegistry.TEXTURE.applyWithModifier(TOKEN, b -> b
            .setCullState(LodestoneRenderTypeRegistry.NO_CULL));
    
    @Override
    public boolean render() {
        if (hasRendered) {
            return true;
        }

        // Convert block size to world units (3x3 blocks)
        float size = 3f;
        float rotation = age * 0.5f; // Rotate 0.5 degrees per tick for slower rotation
        
        // Create flat texture on ground using custom texture
        WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
            .setScaleData(GenericParticleData.create(size, size).build())
            .setTransparencyData(GenericParticleData.create(0.8f, 0).build())
            .setColorData(ColorParticleData.create(new Color(255, 255, 255), new Color(255, 255, 255))
                .setCoefficient(1.0f)
                .setEasing(Easing.EXPO_OUT)
                .build())
            .setLifetime(5)
            .enableNoClip()

            .spawn(level, position.x, position.y + 0.1, position.z);

        age++;
        
        // Disappear after 5 seconds (100 ticks)
        if (age > 100) {
            hasRendered = true;
            return true;
        }
        
        return false;
    }

}
