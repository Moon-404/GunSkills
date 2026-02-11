package com.moon404.gunskills;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.moon404.gunskills.init.GunSkillsEffects;
import com.moon404.gunskills.item.skill.Charge;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.scores.Scoreboard;

public class Utils
{
    public static int getScore(Scoreboard scoreboard, String player, String objective)
    {
        return scoreboard.getOrCreatePlayerScore(player, scoreboard.getOrCreateObjective(objective)).getScore();
    }

    public static void setScore(Scoreboard scoreboard, String player, String objective, int score)
    {
        scoreboard.getOrCreatePlayerScore(player, scoreboard.getOrCreateObjective(objective)).setScore(score);
    }

    public static void invokeFunction(ServerPlayer player, String function)
    {
        MinecraftServer server = player.getServer();
        ServerFunctionManager manager = server.getFunctions();
        CommandDispatcher<CommandSourceStack> dispatcher = manager.getDispatcher();
        CommandSourceStack sourceStack = new CommandSourceStack(CommandSource.NULL, player.getPosition(1), player.getRotationVector(), player.serverLevel(), 2, null, null, server, null);
        sourceStack = sourceStack.withEntity(player);
        try {
            dispatcher.execute("function " + function, sourceStack);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }

    public static int getMaxShield(Player player)
    {
        if (player.experienceLevel == 0) return 0;
        int bonus = player.hasEffect(GunSkillsEffects.CHARGE.get()) ? Charge.AMOUNT : 0;
        return (player.experienceLevel + 1) * 4 + bonus;
    }

    public static int findGroundBelow(ServerLevel level, int x, int z, int maxY)
    {
        int minY = level.getMinBuildHeight();
        int y = Math.min(maxY, level.getMaxBuildHeight() - 1);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
        while (y > minY)
        {
            pos.setY(y);
            BlockState state = level.getBlockState(pos);
            if (!state.isAir()) return y;
            y--;
        }
        return minY;
    }
}
