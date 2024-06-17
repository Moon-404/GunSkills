package com.moon404.gunskills.item.skill;

import com.moon404.gunskills.init.GunSkillsEffects;
import com.moon404.gunskills.struct.ClassType;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class Scan extends SkillItem
{
    public Scan(Properties properties)
    {
        super(properties, ClassType.SCOUT);
    }

    @Override
    public boolean onToss(Player player)
    {
        if (ClassType.getClass(player) != this.classType) return false;
        if (player.hasEffect(GunSkillsEffects.SILENCE.get())) return false;
        double mindis = Float.MAX_VALUE;
        Player nearest = null;
        for (Player target : player.level().players())
        {
            if (!target.isSpectator() && target.getTeam() != player.getTeam())
            {
                double dis = target.distanceTo(player);
                if (dis < mindis)
                {
                    mindis = dis;
                    nearest = target;
                }
            }
        }
        if (nearest == null)
        {
            Component component = Component.literal("未扫描到任何敌人");
            player.displayClientMessage(component, true);
        }
        else
        {
            String message = "最近的敌人在 " + (int)mindis + " 格外";
            Component component = Component.literal(message);
            player.displayClientMessage(component, true);
            /*
            LaserInfo laser = new LaserInfo();
            laser.from = new Vec3(nearest.getX(), 0, nearest.getZ());
            laser.length = 100;
            laser.xRot = -90;
            laser.yRot = 0;
            laser.size = 60; // 5秒
            laser.aiming = 0;
            laser.isShooter = 0;
            RenderLaserMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() ->
            {
                if (player instanceof ServerPlayer serverPlayer)
                {
                    return serverPlayer;
                }
                return null;
            }), laser);
            */
        }
        return true;
    }
}
