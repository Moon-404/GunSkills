package com.moon404.gunskills.entity;

import org.joml.Vector3f;
import com.moon404.gunskills.init.GunSkillsItems;
import com.moon404.gunskills.message.GlowMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ExhibitEntity extends ThrowSkillEntity
{
    public ExhibitEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        color = new Vector3f(0F, 0.67F, 0F);
    }

    @Override
    protected Item getDefaultItem()
    {
        return GunSkillsItems.EXHIBIT.get();
    }

    @Override
    protected void onPurify()
    {
        this.user.displayClientMessage(Component.literal("一览无余效果被净化"), true);
    }

    @Override
    protected void onEffect()
    {
        for (Player player : lastTickPlayers)
        {
            player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0, false, false, true));
            GlowMessage.sendToTeam(this.user.getTeam(), player, 100);
        }
        this.user.displayClientMessage(Component.literal("一览无余命中敌人数：" + lastTickPlayers.size()), true);
    }
}
