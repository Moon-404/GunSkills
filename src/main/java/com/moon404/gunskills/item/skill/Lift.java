package com.moon404.gunskills.item.skill;

import com.moon404.gunskills.entity.LiftEntity;
import com.moon404.gunskills.init.GunSkillsEntities;
import com.moon404.gunskills.struct.ClassType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Lift extends SkillItem
{
    public static final int DURATION = 5;

    public Lift(Properties properties)
    {
        super(properties, 40, 2, ClassType.ROGUE);
        tooltips.add(Component.translatable("item.gunskills.lift.tooltip.1", DURATION));
        tooltips.add(Component.translatable("item.gunskills.lift.tooltip.2"));
    }

    @Override
    public void active(Player player)
    {
        LiftEntity lift = new LiftEntity(GunSkillsEntities.LIFT.get(), player.level());
        Vec3 forward = player.getViewVector(1.0F).multiply(1, 0, 1).normalize();

        lift.setPos(player.position().add(forward));
        player.level().addFreshEntity(lift);
    }
}
