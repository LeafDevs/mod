package me.leaf.devs.registry;

import me.leaf.devs.ExplosionWand;
import me.leaf.devs.Main;
import me.leaf.devs.SmiteWand;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings({"unused", "deprecated"})
public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);

    public static final RegistryObject<Item> EXPLOSION_WAND = ITEMS.register("explosion_wand", ExplosionWand::new);
    public static final RegistryObject<Item> SMITE_WAND = ITEMS.register("smite_wand", SmiteWand::new);
}
