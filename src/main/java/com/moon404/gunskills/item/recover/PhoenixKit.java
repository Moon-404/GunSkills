package com.moon404.gunskills.item.recover;

import com.moon404.gunskills.init.GunSkillsEffects;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PhoenixKit extends RecoverItem
{
    public PhoenixKit(Item.Properties properties)
    {
        super(properties);
        tooltips.add(Component.literal("完全恢复护盾和生命"));
    }

    public int getUseDuration(ItemStack stack)
    {
        return 100;
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
    {
        if (!level.isClientSide && entity instanceof Player player)
        {
            if (player.hasEffect(GunSkillsEffects.SILENCE.get()))
            {
                player.displayClientMessage(Component.literal("被禁疗无法使用"), true);
                return stack;
            }
            float shield = player.experienceLevel * 4 + 4;
            player.setAbsorptionAmount(shield);
            player.heal(20);
        }
        stack.setDamageValue(0);
        return this.isEdible() ? entity.eat(level, stack) : stack;
    }
}
