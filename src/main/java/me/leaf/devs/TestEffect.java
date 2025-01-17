package me.leaf.devs;

import net.minecraft.client.*;
import net.minecraft.client.player.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.event.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.*;
import team.lodestar.lodestone.registry.common.particle.*;
import team.lodestar.lodestone.systems.easing.*;
import team.lodestar.lodestone.systems.particle.builder.*;
import team.lodestar.lodestone.systems.particle.data.*;
import team.lodestar.lodestone.systems.particle.data.color.*;
import team.lodestar.lodestone.systems.particle.data.spin.*;
import java.awt.Color;


@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TestEffect {
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        final LocalPlayer player = Minecraft.getInstance().player;
        // if (player != null && player.input.jumping && player.isAlive()) {
        //     spawnExampleParticles(player.level(), player.position());
        //     Vec3 lookAngle = player.getLookAngle();
        //     Vec3 currentMotion = player.getDeltaMovement();
        //     double speed = 0.5;
            
        //     // Vertical movement
        //     double verticalMotion = player.isCrouching() ? -speed : speed;
            
        //     // Horizontal movement based on where player is looking and movement keys
        //     double forwardMotion = player.input.forwardImpulse * speed;
        //     double sidewaysMotion = player.input.leftImpulse * speed;
            
        //     Vec3 forward = lookAngle.multiply(forwardMotion, 0, forwardMotion);
        //     Vec3 strafe = lookAngle.cross(new Vec3(0, 1, 0)).multiply(sidewaysMotion, 0, sidewaysMotion);
            
        //     player.setDeltaMovement(
        //         forward.x + strafe.x,
        //         verticalMotion,
        //         forward.z + strafe.z
        //     );
        // }
    }

    

    public static void spawnExampleParticles(Level level, Vec3 pos) {
        WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
                .setScaleData(GenericParticleData.create(0.5f, 0).build())
                .setTransparencyData(GenericParticleData.create(0.75f, 0.25f).build())
                .setColorData(ColorParticleData.create(new Color(0, 0, 255), new Color(255, 165, 0)).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN_OUT).build())
                .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((level.getGameTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                .setLifetime(40)
                .addMotion(0, 0.01f, 0)
                .enableNoClip()
                .spawn(level, pos.x, pos.y, pos.z);
    }
}
