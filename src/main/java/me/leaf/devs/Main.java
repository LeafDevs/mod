package me.leaf.devs;

import com.mojang.logging.LogUtils;

import me.leaf.devs.effects.Effects;

import me.leaf.devs.renderer.DirectionalEntityRenderer;

import net.minecraft.client.renderer.entity.EntityRenderers;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;

import org.slf4j.Logger;

import static me.leaf.devs.registry.EntityRegistry.*;
import static me.leaf.devs.registry.ItemRegistry.*;
import static me.leaf.devs.registry.ParticleRegistry.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Main.MODID)
public class Main {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "render_test";
    private static final Logger LOGGER = LogUtils.getLogger();

    static ArrayList<Effects> effects = new ArrayList<>();

    public void clientSetup(FMLClientSetupEvent ev) {
        EntityRenderers.register(DIRECTIONAL_ENTITY.get(), DirectionalEntityRenderer::new);
    }

    public Main() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the items
        ITEMS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        PARTICLES.register(modEventBus);

        

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(EffectRenderer.class);


    }

    private void commonSetup(final FMLCommonSetupEvent event) {
       
    }

    public static void addEffect(Effects effect) {
        effects.add(effect);
        LOGGER.info("Added effect: " + effect);
    }

    public static void removeEffect(Effects effect) {
        effects.remove(effect);
        LOGGER.info("Removed effect: " + effect);
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
