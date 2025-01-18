package me.leaf.devs.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import me.leaf.devs.entity.DirectionalEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

import static me.leaf.devs.registry.EntityRegistry.DIRECTIONAL_ENTITY;

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
    public boolean render(PoseStack ps) {
        age++;
        
        // Animation duration in ticks (5 seconds = 100 ticks)
        int summoningDuration = 200;
        
        // Create directional entity for summoning circle
        if (age == 1) {
            // Random size between 4-6 blocks
            float size = 4.0f + level.getRandom().nextFloat() * 2.0f;
            
            DirectionalEntity circle = DIRECTIONAL_ENTITY.get().create(level);
            circle.setPos(position.x, position.y + 0.1, position.z);
            circle.setRotation(0, 0); // Flat on ground
            circle.setScale(size);
            level.addFreshEntity(circle);
            
            // Add particle effects around circle
            for (int i = 0; i < 30; i++) {
                double angle = i * Math.PI * 2 / 30;
                double radius = size / 2;
                double px = position.x + Math.cos(angle) * radius;
                double pz = position.z + Math.sin(angle) * radius;
                
                WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.2f, 0).build())
                    .setTransparencyData(GenericParticleData.create(0.8f, 0).build())
                    .setColorData(ColorParticleData.create(new Color(180, 100, 255), new Color(100, 20, 200))
                        .setCoefficient(0.8f)
                        .setEasing(Easing.SINE_IN_OUT)
                        .build())
                    .setLifetime(30)
                    .setMotion(0, 0.05, 0)
                    .enableNoClip()
                    .spawn(level, px, position.y + 0.1, pz);
            }
        }

        // Create formation particles from sky
        if (age > 20 && age < summoningDuration - 40) {
            double skyHeight = position.y + 40; // Increased height
            
            // Create swirling particle effect descending from sky
            for (int i = 0; i < 5; i++) { // Increased number of particles
                double angle = (age * 0.05 + i * (Math.PI * 2 / 5)); // Slowed rotation
                double radius = Math.max(0.2, (summoningDuration - age) / 30.0);
                double px = position.x + Math.cos(angle) * radius;
                double pz = position.z + Math.sin(angle) * radius;
                
                // Calculate y position with slower easing for smooth descent
                double progress = (age - 20.0) / (summoningDuration - 60.0);
                double y = skyHeight - (skyHeight - position.y) * Easing.EXPO_OUT.ease(0, Math.min(progress, 1), 1, 1);
                
                WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.4f, 0).build())
                    .setTransparencyData(GenericParticleData.create(0.9f, 0).build())
                    .setColorData(ColorParticleData.create(new Color(255, 200, 100), new Color(200, 100, 255))
                        .setCoefficient(0.8f)
                        .setEasing(Easing.SINE_IN_OUT)
                        .build())
                    .setLifetime(25)
                    .enableNoClip()
                    .spawn(level, px, y, pz);
            }
        }
        
        if (age >= summoningDuration) {
            // Remove the directional entity
            level.getEntitiesOfClass(DirectionalEntity.class, 
                new net.minecraft.world.phys.AABB(position.subtract(1, 1, 1), position.add(1, 1, 1)))
                .forEach(Entity::discard);
            
            // Final particle burst
            // Create an expanding spiral burst
            for (int i = 0; i < 60; i++) {
                double spiralAngle = i * Math.PI * 4 / 60;
                double radiusMult = i / 60.0;
                double radius = 2.0 * radiusMult; // Expanding radius
                double px = position.x + Math.cos(spiralAngle) * radius;
                double pz = position.z + Math.sin(spiralAngle) * radius;
                
                // First layer - outward spiral
                WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.3f, 0.1f).build())
                    .setTransparencyData(GenericParticleData.create(0.9f, 0).build())
                    .setColorData(ColorParticleData.create(new Color(70, 200, 255), new Color(180, 100, 255))
                        .setCoefficient(1.0f)
                        .setEasing(Easing.EXPO_OUT)
                        .build())
                    .setLifetime(40)
                    .setMotion(Math.cos(spiralAngle) * 0.15 * (1-radiusMult), 
                             0.2 * radiusMult, 
                             Math.sin(spiralAngle) * 0.15 * (1-radiusMult))
                    .enableNoClip()
                    .spawn(level, px, position.y + 0.5, pz);
                
                // Second layer - upward burst
                if (i % 2 == 0) {
                    double innerRadius = 0.8;
                    double innerAngle = i * Math.PI * 2 / 30;
                    px = position.x + Math.cos(innerAngle) * innerRadius;
                    pz = position.z + Math.sin(innerAngle) * innerRadius;
                    
                    WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
                        .setScaleData(GenericParticleData.create(0.4f, 0).build())
                        .setTransparencyData(GenericParticleData.create(0.95f, 0).build())
                        .setColorData(ColorParticleData.create(new Color(255, 220, 100), new Color(255, 150, 50))
                            .setCoefficient(0.9f)
                            .setEasing(Easing.CIRC_OUT)
                            .build())
                        .setLifetime(35)
                        .setMotion(Math.cos(innerAngle) * 0.1, 0.3, Math.sin(innerAngle) * 0.1)
                        .enableNoClip()
                        .spawn(level, px, position.y + 0.2, pz);
                }
            }
            
            // Spawn random mob
            EntityType<?>[] possibleMobs = {
                EntityType.ZOMBIE,
                EntityType.SKELETON,
                EntityType.CREEPER,
                EntityType.SPIDER,
                EntityType.BLAZE
            };
            
            EntityType<?> chosenType = possibleMobs[level.getRandom().nextInt(possibleMobs.length)];
            Entity mob = chosenType.create(level);
            
            if (mob != null) {
                mob.setPos(position.x, position.y, position.z);
                level.addFreshEntity(mob);
            }
            
            return true; // Effect is complete after spawning mob
        }
        
        return false; // Still animating
    }

}
