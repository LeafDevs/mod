package me.leaf.devs.registry;


import me.leaf.devs.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import team.lodestar.lodestone.systems.particle.world.type.*;

@SuppressWarnings("unused")
public class ParticleRegistry {
    public static DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Main.MODID);

    public static RegistryObject<LodestoneWorldParticleType> SMOKE = PARTICLES.register("smoke", LodestoneWorldParticleType::new);

    public static void registerParticleFactory(RegisterParticleProvidersEvent event) {

        Minecraft.getInstance().particleEngine.register(SMOKE.get(), LodestoneWorldParticleType.Factory::new);


    }
}