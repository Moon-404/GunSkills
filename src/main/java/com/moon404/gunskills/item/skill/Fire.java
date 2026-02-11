package com.moon404.gunskills.item.skill;

import com.moon404.gunskills.entity.FireEntity;
import com.moon404.gunskills.init.GunSkillsEntities;
import com.moon404.gunskills.struct.ClassType;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class Fire extends SkillItem
{
    public static final int RADIUS = 4;
    public static final int DURATION = 5;

    public Fire(Properties properties)
    {
        super(properties, 40, 3, ClassType.ATTACK);
        tooltips.add(Component.translatable("item.gunskills.fire.tooltip.1"));
        tooltips.add(Component.translatable("item.gunskills.fire.tooltip.2", RADIUS, DURATION));
    }

    @Override
    public void active(Player player)
    {
        FireEntity fire = new FireEntity(GunSkillsEntities.FIRE.get(), player.level());
        fire.user = player;
        fire.setPos(player.getEyePosition());
        fire.setNoGravity(true);
        fire.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 3.2F, 0);
        player.level().addFreshEntity(fire);
    }
}
