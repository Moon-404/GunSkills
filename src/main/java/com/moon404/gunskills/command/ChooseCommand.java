package com.moon404.gunskills.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.moon404.gunskills.Utils;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class ChooseCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("choose")
        .then(Commands.argument("choose", TeamArgument.team()).executes((command) ->
        {
            Player player = command.getSource().getPlayer();
            Scoreboard scoreboard = player.level().getScoreboard();
            if (Utils.getScore(scoreboard, "game_start", "global") == 0)
            {
                PlayerTeam team = TeamArgument.getTeam(command, "choose");
                if (team.getName().equals("test"))
                {
                    player.sendSystemMessage(Component.translatable("command.gunskills.choose.fail.1").withStyle(Style.EMPTY.withColor(0xFF0000)));
                    return Command.SINGLE_SUCCESS;
                }
                if (team.getPlayers().size() >= Utils.getScore(scoreboard, "game_max_team_player", "global"))
                {
                    player.sendSystemMessage(Component.translatable("command.gunskills.choose.fail.2").withStyle(Style.EMPTY.withColor(0xFF0000)));
                    return Command.SINGLE_SUCCESS;
                }
                scoreboard.addPlayerToTeam(player.getScoreboardName(), team);
                player.sendSystemMessage(Component.translatable("command.gunskills.choose.success.1").append(team.getFormattedDisplayName()));
            }
            else
            {
                player.sendSystemMessage(Component.translatable("command.gunskills.choose.fail.3").withStyle(Style.EMPTY.withColor(0xFF0000)));
            }
            return Command.SINGLE_SUCCESS;
        }))
        .then(Commands.literal("observe").executes((command) ->
        {
            Player player = command.getSource().getPlayer();
            Scoreboard scoreboard = player.level().getScoreboard();
            if (Utils.getScore(scoreboard, "game_start", "global") == 0)
            {
                scoreboard.removePlayerFromTeam(player.getScoreboardName());
                player.sendSystemMessage(Component.translatable("command.gunskills.choose.success.2"));
            }
            else
            {
                player.sendSystemMessage(Component.translatable("command.gunskills.choose.fail.4").withStyle(Style.EMPTY.withColor(0xFF0000)));
            }
            return Command.SINGLE_SUCCESS;
        }));
        dispatcher.register(builder);
    }
}
