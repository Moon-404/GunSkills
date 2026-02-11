package com.moon404.gunskills.item.skill;

import com.moon404.gunskills.struct.ClassType;

import net.minecraft.network.chat.Component;

public class Slow extends SkillItem
{
    public static final int DURATION = 1;

    public Slow(Properties properties)
    {
        super(properties, 20, 1, ClassType.ATTACK);
        tooltips.add(Component.translatable("item.gunskills.slow.tooltip", DURATION));
    }
}
