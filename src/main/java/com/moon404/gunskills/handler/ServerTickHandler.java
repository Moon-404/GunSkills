package com.moon404.gunskills.handler;

import java.util.Random;

import com.moon404.gunskills.Utils;
import com.moon404.gunskills.struct.MapGen;
import com.moon404.gunskills.struct.StructureGen;

import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerTickHandler
{
    private static Random random = new Random();

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent event)
    {
        if (event.phase != Phase.START) return;
        Utils.setScore(event.getServer().getScoreboard(), "random", "global", random.nextInt());
        MapGen.tick();
        StructureGen.tick();
    }
}
