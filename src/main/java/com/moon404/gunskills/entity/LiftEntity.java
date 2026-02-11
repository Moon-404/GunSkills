package com.moon404.gunskills.entity;

import com.moon404.gunskills.item.skill.Lift;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class LiftEntity extends Marker
{
    private static final float detectAreaRadius = 0.5F;
    private static final float detectAreaHeight = 9.0F;

    public LiftEntity(EntityType<?> p_147250_, Level p_147251_)
    {
        super(p_147250_, p_147251_);
    }
    
    @Override
    public void tick()
    {
        if (this.tickCount >= Lift.DURATION * 20)
        {
            this.kill();
            return;
        }
        if (this.level() instanceof ServerLevel level)
        {
            level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), 0, 0, 1, 0, 1);

            AABB area = new AABB(this.getX() - detectAreaRadius, this.getY(), this.getZ() - detectAreaRadius,
                    this.getX() + detectAreaRadius, this.getY() + detectAreaHeight, this.getZ() + detectAreaRadius);

            List<ServerPlayer> playersInArea = level.getEntitiesOfClass(ServerPlayer.class, area, player -> !player.isSpectator());

            for (ServerPlayer player : playersInArea)
            {
                int lv = (int) (this.getY() + detectAreaHeight - player.getY());

                player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 2, lv * 2, false, false, true));
            }
        }
    }
}
