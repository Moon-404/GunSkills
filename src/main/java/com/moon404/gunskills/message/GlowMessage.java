package com.moon404.gunskills.message;

import java.util.HashMap;

import com.moon404.gunskills.GunSkills;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class GlowMessage
{
    private static final String PROTOCOL_VERSION = "1";
    private static int index = 1;

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(GunSkills.MODID, "glow"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static HashMap<String, Long> glowing = new HashMap<>();

    public static long getTime()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.level.getGameTime();
    }

    public static void register()
    {
        INSTANCE.registerMessage(index++, GlowInfo.class,
        (content, buf) ->
        {
            buf.writeUtf(content.name);
            buf.writeInt(content.duration);
        },
        (buf) ->
        {
            GlowInfo content = new GlowInfo();
            content.name = buf.readUtf();
            content.duration = buf.readInt();
            return content;
        },
        (content, ctx) ->
        {
            ctx.get().enqueueWork(() ->
            {
                long end = getTime() + content.duration;
                if (!glowing.containsKey(content.name))
                {
                    glowing.put(content.name, end);
                }
                else if (glowing.get(content.name) < end)
                {
                    glowing.put(content.name, end);
                }
            });
            ctx.get().setPacketHandled(true);
        });
    }

    public static class GlowInfo
    {
        public String name;
        public int duration;
    }

    public static void sendToTeam(Team team, Player target, int duration)
    {
        if (target.level() instanceof ServerLevel level)
        {
            GlowInfo msg = new GlowInfo();
            msg.name = target.getScoreboardName();
            msg.duration = duration;
            for (ServerPlayer player : level.players())
            {
                if (player.getTeam() == team)
                {
                    INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
                }
            }
        }
    }
}
