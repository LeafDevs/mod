package me.leaf.devs.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.easing.Easing;
import java.awt.Color;
import java.util.Random;

import me.leaf.devs.Main;

public abstract class Effects {
    protected final Level level;
    protected final Vec3 position;
    protected boolean hasRendered;
    protected final RandomSource random;
    
    protected Effects(Level level, Vec3 position) {
        this.level = level;
        this.position = position;
        this.hasRendered = false;
        this.random = level.getRandom();
    }

    public abstract boolean render(PoseStack ps);
}
