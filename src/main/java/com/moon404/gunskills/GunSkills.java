package com.moon404.gunskills;

import com.moon404.gunskills.handler.HurtHandler;
import com.moon404.gunskills.handler.ItemTossHandler;
import com.moon404.gunskills.handler.LoginHandler;
import com.moon404.gunskills.handler.PlayerTickHandler;
import com.moon404.gunskills.init.GunSkillsBlocks;
import com.moon404.gunskills.init.GunSkillsEffects;
import com.moon404.gunskills.init.GunSkillsEntities;
import com.moon404.gunskills.init.GunSkillsItems;
import com.moon404.gunskills.message.C2SSlide;
import com.moon404.gunskills.message.S2CSlide;
import com.moon404.gunskills.message.ShowDamageMessage;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(GunSkills.MODID)
public class GunSkills
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "gunskills";

    public GunSkills()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::onCommonSetup);

        GunSkillsBlocks.REGISTER.register(modEventBus);
        GunSkillsItems.REGISTER.register(modEventBus);
        GunSkillsItems.CREATIVE_MODE_TABS.register(modEventBus);
        GunSkillsEffects.REGISTER.register(modEventBus);
        GunSkillsEntities.REGISTER.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(HurtHandler.class);
        MinecraftForge.EVENT_BUS.register(ItemTossHandler.class);
        MinecraftForge.EVENT_BUS.register(LoginHandler.class);
        MinecraftForge.EVENT_BUS.register(PlayerTickHandler.class);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            ShowDamageMessage.register();
            C2SSlide.register();
            S2CSlide.register();
        });
    }
}
