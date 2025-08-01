package com.moon404.gunskills.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @ModifyVariable(method = "baseTick", at = @At("STORE"), ordinal = 0)
    private double changeWorldBorderDamage(double d0)
    {
        return -1;
    }
}
