package com.moon404.gunskills.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.moon404.gunskills.struct.MapGen;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class MapgenCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("mapgen")
        .then(Commands.literal("start")
        .then(Commands.argument("x1", IntegerArgumentType.integer())
        .then(Commands.argument("z1", IntegerArgumentType.integer())
        .then(Commands.argument("x2", IntegerArgumentType.integer())
        .then(Commands.argument("z2", IntegerArgumentType.integer())
        .then(Commands.argument("miny", IntegerArgumentType.integer())
        .then(Commands.argument("maxy", IntegerArgumentType.integer())
        .then(Commands.argument("airy", IntegerArgumentType.integer())

        .then(Commands.argument("scale", DoubleArgumentType.doubleArg(1.0, 10000.0))
        .then(Commands.argument("chunkPerTick", IntegerArgumentType.integer(1, 64))
        .executes((command) ->
            {
                long seed = command.getSource().getLevel().getRandom().nextLong();
                return run(command, seed);
            })
        ))

        .then(Commands.argument("seed", LongArgumentType.longArg())
        .then(Commands.argument("scale", DoubleArgumentType.doubleArg(1.0, 10000.0))
        .then(Commands.argument("chunkPerTick", IntegerArgumentType.integer(1, 64))
        .executes((command) ->
            {
                long seed = LongArgumentType.getLong(command, "seed");
                return run(command, seed);
            })
        )))))))))));
        dispatcher.register(builder);
    }

    private static int run(CommandContext<CommandSourceStack> command, long seed)
    {
        int x1 = IntegerArgumentType.getInteger(command, "x1");
        int z1 = IntegerArgumentType.getInteger(command, "z1");
        int x2 = IntegerArgumentType.getInteger(command, "x2");
        int z2 = IntegerArgumentType.getInteger(command, "z2");
        int minY = IntegerArgumentType.getInteger(command, "miny");
        int maxY = IntegerArgumentType.getInteger(command, "maxy");
        int airY = IntegerArgumentType.getInteger(command, "airy");
        double scale = DoubleArgumentType.getDouble(command, "scale");
        int chunkPerTick = IntegerArgumentType.getInteger(command, "chunkPerTick");

        int rx1 = Math.min(x1, x2);
        int rx2 = Math.max(x1, x2);
        int rz1 = Math.min(z1, z2);
        int rz2 = Math.max(z1, z2);
        if (minY > maxY)
        {
            int t = minY;
            minY = maxY;
            maxY = t;
        }
        
        long area = (long)(rx2 - rx1 + 1) * (long)(rz2 - rz1 + 1);
        if (area > 4000000)
        {
            command.getSource().sendFailure(Component.translatable("command.gunskills.mapgen.start.fail.1"));
            return 0;
        }

        boolean ok = MapGen.startJob(command.getSource().getLevel(), rx1, rz1, rx2, rz2, minY, maxY, airY, seed, scale, chunkPerTick);
        if (!ok)
        {
            command.getSource().sendFailure(Component.translatable("command.gunskills.mapgen.start.fail.2"));
            return 0;
        }

        int miny = minY;
        int maxy = maxY;
        command.getSource().sendSuccess(() -> Component.translatable("command.gunskills.mapgen.start.success", rx1, rz1, rx2, rz2, miny, maxy, airY, seed, scale, chunkPerTick), true);
        return 1;
    }
}
