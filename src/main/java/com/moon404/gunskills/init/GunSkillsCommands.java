package com.moon404.gunskills.init;

import com.moon404.gunskills.GunSkills;
import com.moon404.gunskills.command.ChooseCommand;
import com.moon404.gunskills.command.ClassCommand;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = GunSkills.MODID, bus = Bus.FORGE)
public class GunSkillsCommands
{
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event)
    {
        ClassCommand.register(event.getDispatcher());
        ChooseCommand.register(event.getDispatcher());
    }
}
