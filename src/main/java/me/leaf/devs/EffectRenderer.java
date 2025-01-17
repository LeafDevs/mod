package me.leaf.devs;

import me.leaf.devs.effects.Effects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import java.util.concurrent.CopyOnWriteArrayList;

@EventBusSubscriber(value = Dist.CLIENT, modid = Main.MODID)
public class EffectRenderer {
    private static final CopyOnWriteArrayList<Effects> effects = new CopyOnWriteArrayList<>();
    
    @SubscribeEvent(priority = EventPriority.LOWEST) 
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_WEATHER)) {
            effects.removeIf(effect -> {
                boolean rendered = effect.render();
                Main.getLogger().info("Rendering effect: " + effect + ", rendered: " + rendered);
                return rendered;
            });
        }
    }

    public static void addEffect(Effects effect) {
        effects.add(effect);
    }
}
