package me.leaf.devs;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.leaf.devs.effects.Effects;
import me.leaf.devs.effects.Explosion;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class Command {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("leaf")
            .requires(source -> source.hasPermission(2))
            .executes(context -> {
                ServerPlayer player = context.getSource().getPlayerOrException();
                Vec3 pos = player.position();
                Main.getLogger().info("Adding effect");
                Main.addEffect(new Explosion(player.level(), pos));
                return 1;
            }));
    }
}
