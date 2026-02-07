package com.moon404.gunskills.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.moon404.gunskills.struct.MapGen;
import com.moon404.gunskills.struct.StructureGen;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class MapgenCommand
{
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_TEMPLATES = (command, builder) ->
    {
        StructureTemplateManager structureTemplateManager = command.getSource().getLevel().getStructureManager();
        return SharedSuggestionProvider.suggestResource(structureTemplateManager.listTemplates(), builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("mapgen")
        .then(Commands.literal("structure")
        .then(Commands.literal("clear")
        .executes((command) ->
            {
                if (StructureGen.clear())
                {
                    command.getSource().sendSuccess(() -> Component.translatable("command.gunskills.mapgen.structure.clear.success"), true);
                    return 1;
                }
                else
                {
                    command.getSource().sendFailure(Component.translatable("command.gunskills.mapgen.structure.clear.fail"));
                    return 0;
                }
            })
        )
    
        .then(Commands.literal("add")
        .then(Commands.argument("path", ResourceLocationArgument.id()).suggests(SUGGEST_TEMPLATES)
        .then(Commands.argument("count", IntegerArgumentType.integer(1, 10000))
        .then(Commands.argument("radius", IntegerArgumentType.integer(1, 10000))
        .then(Commands.argument("margin", IntegerArgumentType.integer(0, 10000))
        .executes((command) ->
            {
                ResourceLocation path = ResourceLocationArgument.getId(command, "path");
                int count = IntegerArgumentType.getInteger(command, "count");
                int radius = IntegerArgumentType.getInteger(command, "radius");
                int margin = IntegerArgumentType.getInteger(command, "margin");

                if (path == null)
                {
                    command.getSource().sendFailure(Component.translatable("command.gunskills.mapgen.structure.add.fail.1"));
                    return 0;
                }
                if (command.getSource().getLevel().getStructureManager().get(path).isEmpty())
                {
                    command.getSource().sendFailure(Component.translatable("command.gunskills.mapgen.structure.add.fail.2"));
                    return 0;
                }
                if (StructureGen.add(command.getSource().getLevel(), path, count, radius, margin))
                {
                    command.getSource().sendSuccess(() -> Component.translatable("command.gunskills.mapgen.structure.add.success", path.toString(), count, radius, margin), true);
                    return 1;
                }
                else
                {
                    command.getSource().sendFailure(Component.translatable("command.gunskills.mapgen.structure.add.fail.3"));
                    return 0;
                }
            })
        )))))
    
        .then(Commands.literal("start")
        .then(Commands.argument("x1", IntegerArgumentType.integer())
        .then(Commands.argument("z1", IntegerArgumentType.integer())
        .then(Commands.argument("x2", IntegerArgumentType.integer())
        .then(Commands.argument("z2", IntegerArgumentType.integer())

        .then(Commands.argument("structuresPerTick", IntegerArgumentType.integer(1, 64))
        .executes((command) ->
            {
                long seed = command.getSource().getLevel().getRandom().nextLong();
                return runStructureGen(command, seed);
            })
        )

        .then(Commands.argument("seed", LongArgumentType.longArg())
        .then(Commands.argument("structuresPerTick", IntegerArgumentType.integer(1, 64))
        .executes((command) ->
            {
                long seed = LongArgumentType.getLong(command, "seed");
                return runStructureGen(command, seed);
            })
        ))))))))

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
                return runMapGen(command, seed);
            })
        ))

        .then(Commands.argument("seed", LongArgumentType.longArg())
        .then(Commands.argument("scale", DoubleArgumentType.doubleArg(1.0, 10000.0))
        .then(Commands.argument("chunkPerTick", IntegerArgumentType.integer(1, 64))
        .executes((command) ->
            {
                long seed = LongArgumentType.getLong(command, "seed");
                return runMapGen(command, seed);
            })
        )))))))))));
        dispatcher.register(builder);
    }

    private static int runMapGen(CommandContext<CommandSourceStack> command, long seed)
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

    private static int runStructureGen(CommandContext<CommandSourceStack> command, long seed)
    {
        int x1 = IntegerArgumentType.getInteger(command, "x1");
        int z1 = IntegerArgumentType.getInteger(command, "z1");
        int x2 = IntegerArgumentType.getInteger(command, "x2");
        int z2 = IntegerArgumentType.getInteger(command, "z2");
        int structuresPerTick = IntegerArgumentType.getInteger(command, "structuresPerTick");

        int rx1 = Math.min(x1, x2);
        int rx2 = Math.max(x1, x2);
        int rz1 = Math.min(z1, z2);
        int rz2 = Math.max(z1, z2);

        boolean ok = StructureGen.startJob(command.getSource().getLevel(), x1, z1, x2, z2, seed, structuresPerTick);
        if (!ok)
        {
            command.getSource().sendFailure(Component.translatable("command.gunskills.mapgen.structure.start.fail"));
            return 0;
        }
        
        command.getSource().sendSuccess(() -> Component.translatable("command.gunskills.mapgen.structure.start.success", rx1, rz1, rx2, rz2, seed, structuresPerTick), true);
        return 1;
    }
}
