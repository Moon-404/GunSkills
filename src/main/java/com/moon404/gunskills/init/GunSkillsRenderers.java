package com.moon404.gunskills.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.moon404.gunskills.GunSkills;
import com.moon404.gunskills.entity.FireRender;
import com.moon404.gunskills.entity.SilenceRender;
import com.moon404.gunskills.entity.SmokeRender;
import com.moon404.gunskills.entity.SnareRender;

@Mod.EventBusSubscriber(modid = GunSkills.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GunSkillsRenderers
{
    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(GunSkillsEntities.SNARE.get(), SnareRender::new);
        event.registerEntityRenderer(GunSkillsEntities.SILENCE.get(), SilenceRender::new);
        event.registerEntityRenderer(GunSkillsEntities.SMOKE.get(), SmokeRender::new);
        event.registerEntityRenderer(GunSkillsEntities.FIRE.get(), FireRender::new);
    }
}
