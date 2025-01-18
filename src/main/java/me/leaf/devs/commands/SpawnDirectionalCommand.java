package me.leaf.devs.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import me.leaf.devs.Main;
import me.leaf.devs.entity.DirectionalEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static me.leaf.devs.registry.EntityRegistry.DIRECTIONAL_ENTITY;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpawnDirectionalCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        register(dispatcher);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("spawndirectionalentity")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("pos", Vec3Argument.vec3())
                .executes(context -> spawnDirectionalEntity(context.getSource(), 
                    Vec3Argument.getVec3(context, "pos"),
                    context.getSource().getRotation().y,
                    context.getSource().getRotation().x,
                    1.0f))
                .then(Commands.argument("yaw", FloatArgumentType.floatArg(-180f, 180f))
                    .then(Commands.argument("pitch", FloatArgumentType.floatArg(-90f, 90f))
                        .then(Commands.argument("size", FloatArgumentType.floatArg(0.1f, 10.0f))
                            .executes(context -> spawnDirectionalEntity(context.getSource(),
                                Vec3Argument.getVec3(context, "pos"),
                                FloatArgumentType.getFloat(context, "yaw"),
                                FloatArgumentType.getFloat(context, "pitch"),
                                FloatArgumentType.getFloat(context, "size"))))))));
    }

    private static int spawnDirectionalEntity(CommandSourceStack source, Vec3 pos, float yaw, float pitch, float size) {
        @NotNull EntityType<DirectionalEntity> entityType = DIRECTIONAL_ENTITY.get();
        DirectionalEntity entity = entityType.create(source.getLevel());
        if (entity != null) {
            entity.setPos(pos.x, pos.y, pos.z);
            entity.setRotation(yaw, pitch);
            entity.setScale(size);
            source.getLevel().addFreshEntity(entity);
        }
        return 1;
    }
}
