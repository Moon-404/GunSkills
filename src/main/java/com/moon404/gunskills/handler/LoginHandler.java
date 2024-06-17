package com.moon404.gunskills.handler;

import com.moon404.gunskills.GunSkills;
import com.moon404.gunskills.struct.ClassType;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GunSkills.MODID, value = Dist.DEDICATED_SERVER)
public class LoginHandler
{
    @SubscribeEvent
    public static void onLogin(PlayerLoggedInEvent event)
    {
        Player player = event.getEntity();
        player.sendSystemMessage(Component.literal("欢迎使用枪战技能MOD，当前职业：").append(ClassType.getClass(player).getDisplay()));
        player.sendSystemMessage(Component.literal("您可以点击下面的职业或使用/class指令来选择职业"));
        for (ClassType type : ClassType.values())
        {
            player.sendSystemMessage(type.getHelper());
        }
    }
}
