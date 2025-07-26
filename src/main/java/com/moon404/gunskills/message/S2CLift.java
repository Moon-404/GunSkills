package com.moon404.gunskills.message;

import com.moon404.gunskills.GunSkills;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class S2CLift {

    private static final String PROTOCOL_VERSION = "1";
    private static int index = 1;

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(GunSkills.MODID, "s2c_lift"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void register()
    {
        INSTANCE.registerMessage(index++, Vec3.class,
                (content, buf) ->
                {
                    buf.writeDouble(content.x);
                    buf.writeDouble(content.y);
                    buf.writeDouble(content.z);
                },
                (buf) ->
                        new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                (content, ctx) ->
                {
                    ctx.get().enqueueWork(() ->
                    {
                        Minecraft minecraft = Minecraft.getInstance();
                        Player player = minecraft.player;

                        if (player != null) {
                            player.setDeltaMovement(new Vec3(content.toVector3f()));
                        }

                    });
                    ctx.get().setPacketHandled(true);
                });
    }
}
