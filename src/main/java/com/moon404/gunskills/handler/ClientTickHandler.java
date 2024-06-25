package com.moon404.gunskills.handler;

import java.util.List;

import com.moon404.gunskills.GunSkills;
import com.moon404.gunskills.WheelGui;
import com.moon404.gunskills.init.GunSkillsConfigs;
import com.moon404.gunskills.init.GunSkillsKeyMappings;
import com.moon404.gunskills.message.C2SPing;
import com.moon404.gunskills.struct.PingInfo;
import com.moon404.gunskills.struct.WheelItemList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GunSkills.MODID, value = Dist.CLIENT)
public class ClientTickHandler
{
    public static int holding;
    public static int duration;
    private static int pingcd;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        int lastHolding = holding;
        holding = -1;
        List<Item> list = null;
        int recoverIndex = GunSkillsConfigs.RECOVER_INDEX.get().intValue() - 1;
        if (recoverIndex > -1 && mc.options.keyHotbarSlots[recoverIndex].isDown())
        {
            holding = recoverIndex;
            list = WheelItemList.recoverList;
        }
        if (holding > -1 && lastHolding == holding) duration += 1;
        else duration = 0;
        if (duration > 5) WheelGui.activate(list);

        if (pingcd > 0) pingcd--;
        else if (pingcd == 0 && GunSkillsKeyMappings.PING_KEY.consumeClick())
        {
            pingcd = 5;
            ClientLevel level = mc.level;
            LocalPlayer player = mc.player;
            Vec3 start = player.getEyePosition();
            Vec3 look = player.getLookAngle().scale(100);
            Vec3 end = start.add(look);
            BlockHitResult hitResult = level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
            if (hitResult.getType() == Type.BLOCK)
            {
                PingInfo pingInfo = new PingInfo();
                BlockPos blockPos = hitResult.getBlockPos();
                pingInfo.x = blockPos.getX();
                pingInfo.y = blockPos.getY();
                pingInfo.z = blockPos.getZ();
                pingInfo.t = 100;
                C2SPing.INSTANCE.sendToServer(pingInfo);
            }
        }
    }
}
