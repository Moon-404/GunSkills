package com.moon404.gunskills.item.skill;

import com.moon404.gunskills.entity.ShieldBottleEntity;
import com.moon404.gunskills.init.GunSkillsEffects;
import com.moon404.gunskills.init.GunSkillsEntities;
import com.moon404.gunskills.struct.ClassType;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

public class ShieldBottle extends SkillItem
{
    public ShieldBottle(Properties properties)
    {
        super(properties, ClassType.SUPPORT);
    }

    @Override
    public boolean onLand(ItemEntity entity)
    {
        if (entity.getOwner() instanceof Player player)
        {
            if (ClassType.getClass(player) != this.classType) return false;
            if (player.hasEffect(GunSkillsEffects.SILENCE.get())) return false;
        }
        ShieldBottleEntity bottle = new ShieldBottleEntity(GunSkillsEntities.SHIELD_BOTTLE.get(), entity.level());
        bottle.setPos(entity.position());
        entity.level().addFreshEntity(bottle);
        entity.kill();
        return false;
    }
}
