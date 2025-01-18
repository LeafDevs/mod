package me.leaf.devs.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import me.leaf.devs.Main;
import me.leaf.devs.registry.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
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
    public boolean render(PoseStack ps) {
        if (hasRendered) {
            return true;
        }
        Main.getLogger().info("Age: {}", age);
        // Initial lightning buildup phase (0.5 seconds)
        if (age < 30) {
            // Create swirling particles around strike point
            for (int i = 0; i < 8; i++) {
                double angle = (age + i) * Math.PI / 4;
                double radius = age * 0.1;
                double x = end.x + Math.cos(angle) * radius;
                double z = end.z + Math.sin(angle) * radius;
                
                WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
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
            double ringThickness = 0.25; // Reduced thickness
            
            // Create just the ring outline
            double ringHeight = Math.max(0, 2 * (1 - expansionProgress)); // Ring rises and falls
            for (int i = 0; i < 144; i++) { // Doubled number of particles
                double angle = (i * Math.PI * 2) / 144;
                
                // Create particles only along the ring circumference
                double x = end.x + Math.cos(angle) * baseRadius;
                double z = end.z + Math.sin(angle) * baseRadius;
                
                // Main ring particles
                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(1.5f, 0).build()) // Slightly smaller scale
                    .setTransparencyData(GenericParticleData.create(0.9f * (1 - expansionProgress * 0.7f), 0).build())
                    .setColorData(ColorParticleData.create(new Color(255, 60, 60), new Color(220, 30, 30))
                        .setCoefficient(1.4f)
                        .setEasing(Easing.EXPO_OUT)
                        .build())
                    .setLifetime(10)
                    .enableNoClip()
                    .spawn(level, x, end.y + ringHeight, z);
                
                // Additional particles with varied colors
                if (random.nextFloat() < 0.7) { // Increased spawn chance
                    double offsetRadius = baseRadius + (random.nextDouble() * 2 - 1) * ringThickness;
                    double offsetX = end.x + Math.cos(angle) * offsetRadius;
                    double offsetZ = end.z + Math.sin(angle) * offsetRadius;
                    
                    // Randomly choose between different shades of red
                    Color startColor, endColor;
                    int colorChoice = random.nextInt(3);
                    switch(colorChoice) {
                        case 0:
                            startColor = new Color(255, 40, 40);
                            endColor = new Color(200, 20, 20);
                            break;
                        case 1:
                            startColor = new Color(255, 80, 80);
                            endColor = new Color(180, 40, 40);
                            break;
                        default:
                            startColor = new Color(255, 120, 120);
                            endColor = new Color(160, 60, 60);
                            break;
                    }
                    
                    WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
                        .setScaleData(GenericParticleData.create(1.2f * (1 - expansionProgress * 0.5f), 0).build())
                        .setTransparencyData(GenericParticleData.create(0.7f * (1 - expansionProgress), 0).build())
                        .setColorData(ColorParticleData.create(startColor, endColor)
                            .setCoefficient(1.2f)
                            .setEasing(Easing.EXPO_OUT)
                            .build())
                        .setLifetime(15)
                        .enableNoClip()
                        .spawn(level, offsetX, end.y + ringHeight, offsetZ);
                }
            }
        }

        // Create crater effect at halfway point
        if (age == 90) {
            // Create multiple rings of smoke particles with varying properties
            for (int ring = 0; ring < 3; ring++) {
                double ringRadius = 2.0 + ring * 0.8;
                int particlesInRing = 16 + ring * 8;
                
                for (int i = 0; i < particlesInRing; i++) {
                    double angle = (i / (double)particlesInRing) * Math.PI * 2;
                    double x = end.x + Math.cos(angle) * ringRadius;
                    double z = end.z + Math.sin(angle) * ringRadius;
                    
                    // Randomize particle properties
                    float scale = 1.5f + random.nextFloat() * 1.5f;
                    int lifetime = 160 + random.nextInt(80);
                    float verticalSpeed = 0.03f + random.nextFloat() * 0.04f;
                    
                    // Randomize motion direction
                    double motionX = (random.nextDouble() - 0.5) * 0.04;
                    double motionZ = (random.nextDouble() - 0.5) * 0.04;
                    
                    // Create larger smoke plumes at varying heights
                    double startHeight = end.y + 0.2 + random.nextDouble() * 0.8;
                    WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
                        .setScaleData(GenericParticleData.create(scale, 0).build())
                        .setTransparencyData(GenericParticleData.create(0.9f, 0).build())
                        .setColorData(ColorParticleData.create(new Color(255, 100, 0), new Color(40, 40, 40))
                            .setCoefficient(1.4f)
                            .setEasing(Easing.EXPO_OUT)
                            .build())
                        .setLifetime(lifetime)
                        .setMotion(motionX, verticalSpeed, motionZ)
                        .enableNoClip()
                        .spawn(level, x, startHeight, z);
                        
                    // Add some smaller embers mixed in at different heights
                    if (random.nextFloat() < 0.6) { // Increased spawn chance
                        double emberHeight = end.y + 0.1 + random.nextDouble() * 1.0;
                        double emberMotionX = (random.nextDouble() - 0.5) * 0.06;
                        double emberMotionZ = (random.nextDouble() - 0.5) * 0.06;
                        
                        WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
                            .setScaleData(GenericParticleData.create(0.4f + random.nextFloat() * 0.3f, 0).build())
                            .setTransparencyData(GenericParticleData.create(0.8f, 0).build())
                            .setColorData(ColorParticleData.create(new Color(255, 140, 0), new Color(180, 40, 0))
                                .setCoefficient(1.2f)
                                .setEasing(Easing.EXPO_OUT)
                                .build())
                            .setLifetime(80 + random.nextInt(40))
                            .setMotion(emberMotionX, verticalSpeed * 1.5, emberMotionZ)
                            .enableNoClip()
                            .spawn(level, x, emberHeight, z);
                    }
                    
                    // Add additional high-rising sparks
                    if (random.nextFloat() < 0.3) {
                        double sparkHeight = end.y + 0.5 + random.nextDouble() * 1.5;
                        double sparkMotionX = (random.nextDouble() - 0.5) * 0.08;
                        double sparkMotionZ = (random.nextDouble() - 0.5) * 0.08;
                        
                        WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
                            .setScaleData(GenericParticleData.create(0.3f + random.nextFloat() * 0.2f, 0).build())
                            .setTransparencyData(GenericParticleData.create(0.9f, 0).build())
                            .setColorData(ColorParticleData.create(new Color(255, 200, 0), new Color(255, 100, 0))
                                .setCoefficient(1.3f)
                                .setEasing(Easing.EXPO_OUT)
                                .build())
                            .setLifetime(100 + random.nextInt(60))
                            .setMotion(sparkMotionX, verticalSpeed * 2.0, sparkMotionZ)
                            .enableNoClip()
                            .spawn(level, x, sparkHeight, z);
                    }
                }
            }

            // Create crater by removing blocks in a sphere pattern
            int radius = 5; // Medium sized crater radius
            int horizontalRadius = 5;
            int maxDepth = 2;
            
            for(int x = -horizontalRadius; x <= horizontalRadius; x++) {
                for(int z = -horizontalRadius; z <= horizontalRadius; z++) {
                    // Calculate horizontal distance from center
                    double horizontalDist = Math.sqrt(x*x + z*z);
                    
                    // Determine depth based on distance from center
                    int depth = horizontalDist <= horizontalRadius/2 ? maxDepth : 1;
                    
                    for(int y = 0; y >= -depth; y--) {
                        // Get block position
                        BlockPos pos = new BlockPos(
                            (int)end.x + x,
                            (int)end.y + y,
                            (int)end.z + z
                        );
                        
                        if(horizontalDist <= horizontalRadius) {
                            // Remove block within main radius
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        } else if(horizontalDist <= horizontalRadius + 2 && random.nextFloat() < 0.3) {
                            // Random blocks outside main radius
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }

            // Damage entities within radius
            double damageRadius = 3.0;
            level.getEntities(null, new AABB(
                end.x - damageRadius, end.y - damageRadius, end.z - damageRadius,
                end.x + damageRadius, end.y + damageRadius, end.z + damageRadius
            )).forEach(entity -> {
                double distance = entity.position().distanceTo(end);
                if (distance <= damageRadius) {
                    entity.hurt(entity.damageSources().magic(), 10f);
                }
            });
        }

        age++;
        if (age > 180) {

            
            hasRendered = true;
            return true;
        }
        return false;
    }
}
