package me.leaf.devs.registry;

import me.leaf.devs.Main;
import me.leaf.devs.entity.DirectionalEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Main.MODID);

    public static final RegistryObject<EntityType<DirectionalEntity>> DIRECTIONAL_ENTITY =
            ENTITY_TYPES.register("directional_entity",
                    () -> EntityType.Builder.of(DirectionalEntity::new, MobCategory.MISC)
                            .sized(3.0f, 0.2f) // Adjust size as needed
                            .build(new ResourceLocation(Main.MODID, "directional_entity").toString()));
}
