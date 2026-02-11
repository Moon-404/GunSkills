package com.moon404.gunskills.entity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.joml.Vector3f;

import com.moon404.gunskills.Utils;
import com.moon404.gunskills.init.GunSkillsItems;
import com.moon404.gunskills.item.skill.Fire;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FireEntity extends ThrowableItemProjectile
{
    public Player user;
    public int activeTickCount;
    private double anchorX, anchorY, anchorZ;
    private List<BlockPos> firePos = new ArrayList<>();
    private static final HashMap<UUID, Long> playerDamageCooldown = new HashMap<>();
    private static final HashMap<UUID, Long> playerResetCooldown = new HashMap<>();
    private static final HashMap<UUID, Integer> playerLastDamage = new HashMap<>();
    private static final int RADIUS = Fire.RADIUS;
    private static final int INTERVAL = 20;

    public FireEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        this.activeTickCount = -1;
    }

    @Override
    protected Item getDefaultItem()
    {
        return GunSkillsItems.FIRE.get();
    }
    
    class Node
    {
        private BlockPos pos;
        private int depth;

        Node(BlockPos pos, int depth)
        {
            this.pos = pos;
            this.depth = depth;
        }
    }

    protected void onHit(HitResult pResult)
    {
        super.onHit(pResult);
        Vec3 hitPos = pResult.getLocation();
        BlockPos center = pResult instanceof BlockHitResult blockHitResult ?
            blockHitResult.getBlockPos().relative(blockHitResult.getDirection()) :
            new BlockPos((int)hitPos.x, (int)hitPos.y, (int)hitPos.z);

        this.setDeltaMovement(0, 0, 0);
        this.activeTickCount = this.tickCount;
        this.anchorX = center.getX() + 0.5;
        this.anchorY = center.getY() + 0.5;
        this.anchorZ = center.getZ() + 0.5;

        if (!(this.level() instanceof ServerLevel level)) return;

        int startGroundY = Utils.findGroundBelow(level, center.getX(), center.getZ(), center.getY());
        if (startGroundY == level.getMinBuildHeight()) return;

        BlockPos start = center.atY(startGroundY + 1);
        firePos.add(start);

        ArrayDeque<Node> queue = new ArrayDeque<>();
        HashMap<BlockPos, Integer> visited = new HashMap<>();

        queue.add(new Node(start, 0));
        visited.put(start, 0);

        int[][] dirs = new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

        while (!queue.isEmpty())
        {
            Node cur = queue.poll();
            if (cur.depth >= RADIUS) continue;

            for (int[] d : dirs)
            {
                int nx = cur.pos.getX() + d[0];
                int nz = cur.pos.getZ() + d[1];
                int scanY = cur.pos.getY();
                int ng = Utils.findGroundBelow(level, nx, nz, scanY);
                if (ng == level.getMinBuildHeight() || ng == scanY) continue;

                BlockPos next = new BlockPos(nx, ng + 1, nz);
                Integer old = visited.get(next);
                int nd = cur.depth + 1;
                if (old != null && old <= nd) continue;

                visited.put(next, nd);
                firePos.add(next);
                queue.add(new Node(next, nd));
            }
        }
    }

    public void tick()
    {
        if ((this.activeTickCount == -1 && this.tickCount > Fire.DURATION * 20)
            || this.tickCount - this.activeTickCount >= Fire.DURATION * 20)
        {
            this.kill();
            return;
        }
        if (this.level() instanceof ServerLevel level && this.activeTickCount == -1)
        {
            Vector3f color = new Vector3f(0.75F, 0.33F, 0.80F);
            DustParticleOptions options = new DustParticleOptions(color, 1.5F);
            level.sendParticles(options, this.getX(), this.getY(), this.getZ(), 0, 0, 0, 0, 0);
        }
        if (this.activeTickCount > -1) this.setDeltaMovement(0, 0, 0);
        super.tick();
        if (this.activeTickCount > -1) this.setPos(this.anchorX, this.anchorY, this.anchorZ);
        else return;
        if (this.level() instanceof ServerLevel level)
        {
            if (this.tickCount % 10 == 0)
            {
                for (BlockPos pos : firePos)
                {
                    Vec3 dustPos = pos.getCenter();
                    Vector3f color = new Vector3f(0.75F, 0.33F, 0.80F);
                    DustParticleOptions options = new DustParticleOptions(color, 1.5F);
                    level.sendParticles(options, dustPos.x, dustPos.y - 0.3, dustPos.z, 1, 0, 0, 0, 0);
                }
            }

            long currentGameTime = level.getGameTime();
            for (Player player : level.players())
            {
                BlockPos pos = player.getOnPos().above();
                if (!inFire(pos)) continue;
                UUID uuid = player.getUUID();
                long nextResetTime = playerResetCooldown.getOrDefault(uuid, Long.MAX_VALUE);
                if (currentGameTime > nextResetTime)
                {
                    playerLastDamage.put(uuid, 0);
                    playerResetCooldown.put(uuid, Long.MAX_VALUE);
                }
                long nextDamageTime = playerDamageCooldown.getOrDefault(uuid, 0L);
                if (currentGameTime >= nextDamageTime)
                {
                    int damage = playerLastDamage.getOrDefault(uuid, 0) + 1;
                    player.hurt(player.damageSources().mobAttack(this.user), damage);
                    playerLastDamage.put(uuid, damage);
                    playerResetCooldown.put(uuid, currentGameTime + INTERVAL);
                    playerDamageCooldown.put(uuid, currentGameTime + INTERVAL);
                }
            }
        }
    }

    private boolean inFire(BlockPos pos)
    {
        for (BlockPos p : firePos)
        {
            if (p.equals(pos))
            {
                return true;
            }
        }
        return false;
    }
}
