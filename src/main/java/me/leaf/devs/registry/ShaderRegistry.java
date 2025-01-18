package me.leaf.devs.registry;

import com.mojang.blaze3d.vertex.*;
import me.leaf.devs.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.*;
import team.lodestar.lodestone.registry.client.*;
import team.lodestar.lodestone.systems.rendering.shader.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShaderRegistry {
    public static ShaderHolder GLOW = new ShaderHolder(new ResourceLocation(Main.MODID, "glow"), DefaultVertexFormat.POSITION_TEX );

    @SubscribeEvent
    public static void shaderRegistry(RegisterShadersEvent event) {
        Main.getLogger().info("Registering Shader");
        LodestoneShaderRegistry.registerShader(event, GLOW);
    }
}