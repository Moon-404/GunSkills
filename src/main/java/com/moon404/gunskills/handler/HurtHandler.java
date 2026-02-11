package com.moon404.gunskills.handler;

import com.moon404.gunskills.item.skill.Boot;
import com.moon404.gunskills.item.skill.Fast;
import com.moon404.gunskills.item.skill.Ire;
import com.moon404.gunskills.item.skill.Slow;
import com.moon404.gunskills.message.DamageIndicatorMessage;
import com.moon404.gunskills.message.GlowMessage;
import com.moon404.gunskills.message.ShowDamageMessage;
import com.moon404.gunskills.message.DamageIndicatorMessage.DamageIndicator;
import com.moon404.gunskills.struct.DamageInfo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

public class HurtHandler
{
    @SubscribeEvent
    public static void onLivingDamage(LivingHurtEvent event)
    {
        DamageSource source = event.getSource();
        if (event.getEntity() instanceof Player player && event.getSource().is(DamageTypes.FALL))
        {
            ItemStack itemStack = player.getOffhandItem();
            if (itemStack.getItem() instanceof Boot item && item.canUse(player))
            {
                item.enterCooldown(player);
            }
            else
            {
                double duration = Math.cbrt(event.getAmount()) * 10;
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int)duration, 4, false, false, true));
            }
            event.setCanceled(true);
            return;
        }

        if (source.getEntity() instanceof Player a && event.getEntity() instanceof Player b && a.getTeam() == b.getTeam())
        {
            if (!event.getSource().is(DamageTypes.MOB_ATTACK)) event.setCanceled(true);
            return;
        }

        if (source.getEntity() instanceof ServerPlayer from)
        {
            DamageInfo damage = new DamageInfo();
            damage.amount = event.getAmount();
            if (event.getEntity() instanceof Player target)
            {
                if (target.getAbsorptionAmount() > 0)
                {
                    damage.getArmorColor(target.experienceLevel);
                }
            }
            from.giveExperiencePoints((int)(damage.amount * 100));
            ShowDamageMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> {return from;}), damage);

            if (event.getEntity() instanceof ServerPlayer target)
            {
                DamageIndicator indicator = new DamageIndicator();
                indicator.x = (float)from.getX();
                indicator.z = (float)from.getZ();
                DamageIndicatorMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> {return target;}), indicator);
            }

            ItemStack itemStack = from.getOffhandItem();
            if (itemStack.getItem() instanceof Ire item && !event.getEntity().hasEffect(MobEffects.GLOWING) && item.canUse(from))
            {
                event.getEntity().addEffect(new MobEffectInstance(MobEffects.GLOWING, Ire.DURATION * 20, 0, false, false, true));
                if (event.getEntity() instanceof Player target)
                    GlowMessage.sendToTeam(from.getTeam(), target, Ire.DURATION * 20);
                item.enterCooldown(from);
            }

            if (itemStack.getItem() instanceof Slow item && !event.getEntity().hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && item.canUse(from))
            {
                event.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, Slow.DURATION * 20, 4, false, false, true));
                item.enterCooldown(from);
            }
        }

        if (event.getEntity() instanceof Player player)
        {
            ItemStack itemStack = player.getOffhandItem();
            if (itemStack.getItem() instanceof Fast item && !player.hasEffect(MobEffects.MOVEMENT_SPEED) && item.canUse(player))
            {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Fast.DURATION * 20, 2, false, false, true));
                item.enterCooldown(player);
            }
        }
    }
}
