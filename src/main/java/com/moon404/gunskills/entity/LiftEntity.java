package com.moon404.gunskills.entity;

import com.moon404.gunskills.item.skill.Lift;
import com.moon404.gunskills.message.S2CLift;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class LiftEntity extends Marker
{
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

            AABB area = new AABB(this.getX() - 0.5F, this.getY(), this.getZ() - 0.5F,
                    this.getX() + 0.5F, this.getY() + 5.0F, this.getZ() + 0.5F);
            List<ServerPlayer> playersInArea = level.getEntitiesOfClass(ServerPlayer.class, area, player -> !player.isSpectator());

            for (ServerPlayer player : playersInArea)
            {
                if (player.getDeltaMovement().y < 1.2) {
                    Vec3 speed = player.getDeltaMovement();
                    Vec3 delta = new Vec3(1.9 * speed.x, 1.2, 1.9 * speed.z);
                    player.setDeltaMovement(delta);
                    //ModMessages.sendToPlayer(new DeltaMovementSyncS2CPacket(delta), player);

                    S2CLift.INSTANCE.send(PacketDistributor.ALL.noArg(), delta);
                    player.level().playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL,0.5F, 1.0F);

                }
            }

            //level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), 0, 0, 1, 0, 1);
            //for (Player player : level.players())
            //{
            //    Vec3 delta = this.position().vectorTo(player.position());
            //    if (delta.x * delta.x + delta.z * delta.z <= 2)
            //    {
            //        int lv = 9 - (int)delta.y;
            //        if (!player.isSpectator() && lv >= 0)
            //        {
            //            player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 2, lv, false, false, true));
            //        }
            //    }
            //}
        }
    }
}
