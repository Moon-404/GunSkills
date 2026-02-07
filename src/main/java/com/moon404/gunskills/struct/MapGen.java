package com.moon404.gunskills.struct;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MapGen
{
    private static MapGenJob current;

    public static boolean startJob(ServerLevel level, int x1, int z1, int x2, int z2, int minY, int maxY, int airY, long seed, double scale, int chunkPerTick)
    {
        if (current != null) return false;
        PerlinTerrain generator = new PerlinTerrain(seed, scale);
        current = new MapGenJob(level, x1, z1, x2, z2, minY, maxY, airY, generator, chunkPerTick);
        return true;
    }

    public static void tick()
    {
        if (current == null) return;
        boolean done = current.tick();
        if (done) current = null;
    }

    private static final class MapGenJob
    {
        private final ServerLevel level;
        private final int x1, z1, x2, z2, minY, maxY, airY, chunkPerTick;
        private final PerlinTerrain generator;
        private final int cx1, cz1, cx2, cz2;
        private int cx, cz;

        MapGenJob(ServerLevel level, int x1, int z1, int x2, int z2, int minY, int maxY, int airY, PerlinTerrain generator, int chunkPerTick)
        {
            this.level = level;
            this.x1 = x1;
            this.z1 = z1;
            this.x2 = x2;
            this.z2 = z2;
            this.minY = minY;
            this.maxY = maxY;
            this.airY = airY;
            this.generator = generator;
            this.chunkPerTick = chunkPerTick;

            this.cx1 = x1 / 16;
            this.cz1 = z1 / 16;
            this.cx2 = x2 / 16;
            this.cz2 = z2 / 16;
            this.cx = cx1;
            this.cz = cz1;
        }

        boolean tick()
        {
            int processed = 0;
            while (processed < chunkPerTick)
            {
                if (cz > cz2) return true;

                level.getChunk(cx, cz);
                int chunkMinX = cx * 16;
                int chunkMinZ = cz * 16;
                int chunkMaxX = chunkMinX + 15;
                int chunkMaxZ = chunkMinZ + 15;
                int sx = Math.max(x1, chunkMinX);
                int ex = Math.min(x2, chunkMaxX);
                int sz = Math.max(z1, chunkMinZ);
                int ez = Math.min(z2, chunkMaxZ);

                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
                BlockState dirt = Blocks.DIRT.defaultBlockState();
                BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
                BlockState air = Blocks.AIR.defaultBlockState();

                for (int x = sx; x <= ex; x++)
                {
                    for (int z = sz; z <= ez; z++)
                    {
                        int targetY = generator.sampleHeight(x, z, minY, maxY);
                        for (int y = minY; y < targetY; y++)
                        {
                            pos.set(x, y, z);
                            level.setBlock(pos, dirt, 2);
                        }
                        pos.set(x, targetY, z);
                        level.setBlock(pos, grass, 2);
                        for (int y = targetY + 1; y <= airY; y++)
                        {
                            pos.set(x, y, z);
                            if (!level.getBlockState(pos).isAir()) level.setBlock(pos, air, 2);
                        }
                    }
                }

                processed++;
                cx++;
                if (cx > cx2)
                {
                    cx = cx1;
                    cz++;
                }
            }
            return false;
        }
    }
}
