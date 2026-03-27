package com.moon404.gunskills.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WorldBorder.class)
public class WorldBorderMixin {

    //所有判断世界实体处于世界边界内的方法全返回true
    @Overwrite
    public boolean isWithinBounds(BlockPos pPos)
    {
        return true;
    }

    @Overwrite
    public boolean isWithinBounds(ChunkPos pChunkPos)
    {
        return true;
    }

    @Overwrite
    public boolean isWithinBounds(double pX, double pZ)
    {
        return true;
    }

    @Overwrite
    public boolean isWithinBounds(double pX, double pZ, double pOffset)
    {
        return true;
    }

    @Overwrite
    public boolean isWithinBounds(AABB pBox) {
        return true;
    }

    //实体在边界时不再被认为有碰撞
    @Overwrite
    public boolean isInsideCloseToBorder(Entity pEntity, AABB pBounds)
    {
        return false;
    }
}
