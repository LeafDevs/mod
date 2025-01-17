package me.leaf.devs;

import com.mojang.logging.LogUtils;

import me.leaf.devs.effects.Effects;
import me.leaf.devs.registry.ParticleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;

import java.util.ArrayList;

import org.slf4j.Logger;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.model.obj.ObjModel;
import team.lodestar.lodestone.systems.particle.world.type.LodestoneWorldParticleType;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Main.MODID)
public class Main {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "render_test";
    private static final Logger LOGGER = LogUtils.getLogger();

    static ArrayList<Effects> effects = new ArrayList<>();

    // Create DeferredRegister for items
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> EXPLOSION_WAND = ITEMS.register("explosion_wand", ExplosionWand::new);
    public static final RegistryObject<Item> SMITE_WAND = ITEMS.register("smite_wand", SmiteWand::new);

    public Main() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the items
        ITEMS.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(TestEffect.class);
        MinecraftForge.EVENT_BUS.register(ExplosionWand.class);
        MinecraftForge.EVENT_BUS.register(Command.class);
        MinecraftForge.EVENT_BUS.register(EffectRenderer.class);
        MinecraftForge.EVENT_BUS.register(SmiteWand.class);

        ParticleRegistry.PARTICLES.register(modEventBus);
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
