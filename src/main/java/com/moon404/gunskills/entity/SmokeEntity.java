package com.moon404.gunskills.entity;

import org.joml.Vector3f;
import com.moon404.gunskills.init.GunSkillsBlocks;
import com.moon404.gunskills.init.GunSkillsItems;
import com.moon404.gunskills.item.skill.Smoke;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class SmokeEntity extends ThrowableItemProjectile
{
    public int activeTickCount;
    private int anchorX, anchorY, anchorZ;
    private static final int RADIUS = Smoke.RADIUS;

    public SmokeEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        this.activeTickCount = -1;
    }

    @Override
    protected Item getDefaultItem()
    {
        return GunSkillsItems.SMOKE.get();
    }
    
    protected void onHit(HitResult pResult)
    {
        super.onHit(pResult);
        this.setDeltaMovement(0, 0, 0);
        this.activeTickCount = this.tickCount;
        this.anchorX = this.getBlockX();
        this.anchorY = this.getBlockY();
        this.anchorZ = this.getBlockZ();
    }

    private static double distance(BlockPos pos1, BlockPos pos2)
    {
        int dx = pos2.getX() - pos1.getX();
        int dy = pos2.getY() - pos1.getY();
        int dz = pos2.getZ() - pos1.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public void tick()
    {
        if (this.level() instanceof ServerLevel level && this.activeTickCount == -1)
        {
            Vector3f color = new Vector3f(0, 0, 0);
            DustParticleOptions options = new DustParticleOptions(color, 1.5F);
            level.sendParticles(options, this.getX(), this.getY(), this.getZ(), 0, 0, 0, 0, 0);
        }
        if (this.activeTickCount > -1)
        {
            this.setDeltaMovement(0, 0, 0);
        }
        super.tick();
        if (this.activeTickCount > -1)
        {
            this.setPos(this.anchorX, this.anchorY, this.anchorZ);
        }
        if (this.level() instanceof ServerLevel level && this.tickCount - this.activeTickCount > Smoke.DURATION * 20)
        {
            Iterable<BlockPos> blockPoses = BlockPos.betweenClosed(this.getBlockX() - RADIUS, this.getBlockY() - RADIUS, this.getBlockZ() - RADIUS,
                this.getBlockX() + RADIUS, this.getBlockY() + RADIUS, this.getBlockZ() + RADIUS);
            for (BlockPos pos : blockPoses)
            {
                if (distance(pos, this.blockPosition()) <= RADIUS)
                {
                    BlockState blockState = level.getBlockState(pos);
                    if (blockState.getBlock() == GunSkillsBlocks.SMOKE.get())
                    {
                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
            this.kill();
        }
        else if (this.level() instanceof ServerLevel level && this.activeTickCount > -1)
        {
            Iterable<BlockPos> blockPoses = BlockPos.betweenClosed(this.getBlockX() - RADIUS, this.getBlockY() - RADIUS, this.getBlockZ() - RADIUS,
                this.getBlockX() + RADIUS, this.getBlockY() + RADIUS, this.getBlockZ() + RADIUS);
            for (BlockPos pos : blockPoses)
            {
                if (distance(pos, this.blockPosition()) <= RADIUS)
                {
                    BlockState blockState = level.getBlockState(pos);
                    if (blockState.getBlock() == Blocks.AIR)
                    {
                        level.setBlockAndUpdate(pos, GunSkillsBlocks.SMOKE.get().defaultBlockState());
                    }
                }
            }
        }
    }
}
