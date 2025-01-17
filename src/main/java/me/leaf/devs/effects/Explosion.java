package me.leaf.devs.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import java.awt.Color;
public class Explosion extends Effects {

    public int age = 0;

    public Explosion(Level level, Vec3 position) {
        super(level, position);
    }

    @Override
    public boolean render(PoseStack ps) {
        if (hasRendered) {
            return false;
        }

        // Lightning phase (first 10 ticks)
        if (age < 10) {
            if (age == 0) {
                // Spawn 3 lightning bolts at slightly different positions
                for (int i = 0; i < 3; i++) {
                    double offsetX = (random.nextDouble() - 0.5) * 4;
                    double offsetZ = (random.nextDouble() - 0.5) * 4;
                    
                    // Lightning core
                    for (int j = 0; j < 8; j++) {
                        WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
                            .setScaleData(GenericParticleData.create(2f, 0).build())
                            .setTransparencyData(GenericParticleData.create(0.9f, 0).build())
                            .setColorData(ColorParticleData.create(new Color(200, 0, 255), new Color(130, 0, 255))
                                .setCoefficient(1.5f)
                                .setEasing(Easing.EXPO_OUT)
                                .build())
                            .setLifetime(8)
                            .enableNoClip()
                            .spawn(level, 
                                position.x + offsetX + (random.nextDouble() - 0.5) * 0.5,
                                position.y + 20 - (j * 2.5),
                                position.z + offsetZ + (random.nextDouble() - 0.5) * 0.5);
                    }
                    
                    // Lightning glow
                    for (int j = 0; j < 12; j++) {
                        WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                            .setScaleData(GenericParticleData.create(3f, 0).build())
                            .setTransparencyData(GenericParticleData.create(0.6f, 0).build())
                            .setColorData(ColorParticleData.create(new Color(230, 180, 255), new Color(180, 100, 255))
                                .setCoefficient(1.2f)
                                .setEasing(Easing.EXPO_OUT)
                                .build())
                            .setLifetime(10)
                            .enableNoClip()
                            .spawn(level,
                                position.x + offsetX + (random.nextDouble() - 0.5) * 1.5,
                                position.y + 20 - (j * 2),
                                position.z + offsetZ + (random.nextDouble() - 0.5) * 1.5);
                    }
                }
            }
        }
        // Explosion phase (after lightning)
        else {
            float progress = (age - 10) / 20f; // Animation progress from 0 to 1
            float expansionScale = (float) (1 - Math.pow(1 - Math.min(progress * 1.5f, 1.0f), 2)); // Smooth easing
            float fadeOut = Math.max(0, 1 - (progress * 1.2f)); // Gradual fade out
            
            // Core explosion particles
            for (int i = 0; i < 50; i++) {
                double phi = random.nextDouble() * Math.PI * 2;
                double theta = random.nextDouble() * Math.PI;
                double radius = 5.0 * expansionScale;
                
                double offsetX = Math.sin(theta) * Math.cos(phi) * radius;
                double offsetY = Math.cos(theta) * radius;
                double offsetZ = Math.sin(theta) * Math.sin(phi) * radius;

                WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
                    .setScaleData(GenericParticleData.create(4.5f * fadeOut, 0).build())
                    .setTransparencyData(GenericParticleData.create(0.8f * fadeOut, 0).build())
                    .setColorData(ColorParticleData.create(
                        new Color(180, 100, 255),
                        new Color(80, 0, 150))
                        .setEasing(Easing.EXPO_OUT)
                        .build())
                    .setLifetime(35)
                    .addMotion(offsetX * 0.12, offsetY * 0.12 + 0.15, offsetZ * 0.12)
                    .enableNoClip()
                    .spawn(level, position.x, position.y, position.z);
            }

            // Fire ring
            for (int i = 0; i < 40; i++) {
                double angle = (i / 40.0) * Math.PI * 2;
                double radius = 6.0 * expansionScale;
                double offsetX = Math.cos(angle) * radius;
                double offsetZ = Math.sin(angle) * radius;

                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(3f * fadeOut, 0).build())
                    .setTransparencyData(GenericParticleData.create(0.9f * fadeOut, 0).build())
                    .setColorData(ColorParticleData.create(
                        new Color(255, 200, 255),
                        new Color(180, 0, 255))
                        .setCoefficient(1.8f)
                        .setEasing(Easing.EXPO_OUT)
                        .build())
                    .setLifetime(30)
                    .addMotion(offsetX * 0.1, 0.2, offsetZ * 0.1)
                    .enableNoClip()
                    .spawn(level, position.x, position.y, position.z);
            }

            // Sparks
            for (int i = 0; i < 25; i++) {
                double phi = random.nextDouble() * Math.PI * 2;
                double theta = random.nextDouble() * Math.PI;
                double radius = 7.0 * expansionScale;
                
                double offsetX = Math.sin(theta) * Math.cos(phi) * radius;
                double offsetY = Math.cos(theta) * radius;
                double offsetZ = Math.sin(theta) * Math.sin(phi) * radius;

                WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
                    .setScaleData(GenericParticleData.create(1f * fadeOut, 0).build())
                    .setTransparencyData(GenericParticleData.create(1f * fadeOut, 0).build())
                    .setColorData(ColorParticleData.create(
                        new Color(255, 230, 255),
                        new Color(230, 180, 255))
                        .build())
                    .setLifetime(20)
                    .addMotion(offsetX * 0.25, offsetY * 0.25, offsetZ * 0.25)
                    .enableNoClip()
                    .spawn(level, position.x, position.y, position.z);
            }
        }

        age++;
        if (age >= 30) {
            hasRendered = true;
            return true;
        }
        return false;
    }
}
