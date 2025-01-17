package me.leaf.devs.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
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

import me.leaf.devs.shaders.SmiteShader;

public class Smite extends Effects {
    private int age = 0;
    private final Vec3 start;
    private final Vec3 end;

    public Smite(Level level, Vec3 start, Vec3 end) {
        super(level, start);
        this.start = start;
        this.end = end;
    }


    public static final ResourceLocation texture = new ResourceLocation("textures/vfx/beam.png");
    public static final RenderTypeToken token = RenderTypeToken.createCachedToken(texture);

    private static final LodestoneRenderType RENDER_TYPE = LodestoneRenderTypeRegistry.TEXTURE.applyWithModifier(token, b -> b
            .setCullState(LodestoneRenderTypeRegistry.NO_CULL));

    @Override
    public boolean render() {
        if (hasRendered) {
            return true;
        }

        // Initial lightning buildup phase (0.5 seconds)
        if (age < 30) {
            // Create swirling particles around strike point
            for (int i = 0; i < 8; i++) {
                double angle = (age + i) * Math.PI / 4;
                double radius = age * 0.1;
                double x = end.x + Math.cos(angle) * radius;
                double z = end.z + Math.sin(angle) * radius;
                
                WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
                    .setScaleData(GenericParticleData.create(1.5f, 0).build())
                    .setTransparencyData(GenericParticleData.create(0.6f, 0).build())
                    .setColorData(ColorParticleData.create(new Color(255, 100, 100), new Color(200, 50, 50))
                        .setCoefficient(1.2f)
                        .setEasing(Easing.EXPO_OUT)
                        .build())
                    .setLifetime(20)
                    .enableNoClip()
                    .spawn(level, x, end.y + 0.1, z);
            }
        }

        // Main lightning strike (at 0.5 seconds)
        if (age == 80) {
            // Spawn red lightning bolt
            BlockPos bp = new BlockPos((int)end.x, (int)end.y, (int)end.z);
            for (int branch = 0; branch < 3; branch++) {
                double offsetX = (random.nextDouble() - 0.5) * 2;
                double offsetZ = (random.nextDouble() - 0.5) * 2;
                
                // Main lightning core
                for (int i = 0; i < 20; i++) {
                    double heightProgress = i / 20.0;
                    double jitter = Math.sin(heightProgress * 8) * (1 - heightProgress) * 2;
                    
                    WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
                        .setScaleData(GenericParticleData.create(1.8f, 0).build())
                        .setTransparencyData(GenericParticleData.create(0.95f, 0).build())
                        .setColorData(ColorParticleData.create(new Color(220, 240, 255), new Color(180, 220, 255))
                            .setCoefficient(1.4f)
                            .setEasing(Easing.EXPO_OUT)
                            .build())
                        .setLifetime(5)
                        .enableNoClip()
                        .spawn(level,
                            end.x + offsetX + jitter + (random.nextDouble() - 0.5) * 0.3,
                            end.y + 20 - (i * 1.0),
                            end.z + offsetZ + jitter + (random.nextDouble() - 0.5) * 0.3);
                }
                
                // Outer glow
                for (int i = 0; i < 15; i++) {
                    double heightProgress = i / 15.0;
                    double jitter = Math.sin(heightProgress * 6) * (1 - heightProgress) * 1.5;
                    
                    WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                        .setScaleData(GenericParticleData.create(2.5f, 0).build())
                        .setTransparencyData(GenericParticleData.create(0.7f, 0).build())
                        .setColorData(ColorParticleData.create(new Color(200, 230, 255), new Color(160, 200, 255))
                            .setCoefficient(1.2f)
                            .setEasing(Easing.EXPO_OUT)
                            .build())
                        .setLifetime(7)
                        .enableNoClip()
                        .spawn(level,
                            end.x + offsetX + jitter + (random.nextDouble() - 0.5) * 0.8,
                            end.y + 20 - (i * 1.4),
                            end.z + offsetZ + jitter + (random.nextDouble() - 0.5) * 0.8);
                }
            }
        }

        // Ground impact effects and shockwave (1.5 seconds)
        if (age >= 85 && age <= 150) {
            float expansionProgress = (age - 35) / 90f;
            double baseRadius = expansionProgress * 15; // Radius of expanding ring
            double ringThickness = 0.5; // Thickness of the ring

            // Create just the ring outline
            double ringHeight = Math.max(0, 2 * (1 - expansionProgress)); // Ring rises and falls
            for (int i = 0; i < 72; i++) { // More particles for smoother ring
                double angle = (i * Math.PI * 2) / 72;
                
                // Create particles only along the ring circumference
                double x = end.x + Math.cos(angle) * baseRadius;
                double z = end.z + Math.sin(angle) * baseRadius;
                
                // Main ring particles
                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(2f, 0).build())
                    .setTransparencyData(GenericParticleData.create(0.9f * (1 - expansionProgress * 0.7f), 0).build())
                    .setColorData(ColorParticleData.create(new Color(255, 80, 80), new Color(200, 40, 40))
                        .setCoefficient(1.4f)
                        .setEasing(Easing.EXPO_OUT)
                        .build())
                    .setLifetime(10)
                    .enableNoClip()
                    .spawn(level, x, end.y + ringHeight, z);
                
                // Additional particles to make ring more defined
                if (random.nextFloat() < 0.5) {
                    double offsetRadius = baseRadius + (random.nextDouble() * 2 - 1) * ringThickness;
                    double offsetX = end.x + Math.cos(angle) * offsetRadius;
                    double offsetZ = end.z + Math.sin(angle) * offsetRadius;
                    
                    WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
                        .setScaleData(GenericParticleData.create(1.5f * (1 - expansionProgress * 0.5f), 0).build())
                        .setTransparencyData(GenericParticleData.create(0.7f * (1 - expansionProgress), 0).build())
                        .setColorData(ColorParticleData.create(new Color(255, 100, 100), new Color(180, 50, 50))
                            .setCoefficient(1.2f)
                            .setEasing(Easing.EXPO_OUT)
                            .build())
                        .setLifetime(15)
                        .enableNoClip()
                        .spawn(level, offsetX, end.y + ringHeight, offsetZ);
                }
            }
        }

        age++;
        if (age > 180) {
            hasRendered = true;
            return true;
        }
        return false;
    }
}
