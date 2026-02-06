package com.moon404.gunskills.struct;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public class PoissonDisk
{
    public static List<BlockPos> sample(int x1, int z1, int x2, int z2, double r, long seed)
    {
        if (r <= 0) throw new IllegalArgumentException("r must be > 0");

        final RandomSource rng = RandomSource.create(seed);

        final int minX = Math.min(x1, x2);
        final int maxX = Math.max(x1, x2);
        final int minZ = Math.min(z1, z2);
        final int maxZ = Math.max(z1, z2);

        if (minX > maxX || minZ > maxZ) return List.of();

        final double cellSize = r / Math.sqrt(2.0);

        final int width = (maxX - minX) + 1;
        final int height = (maxZ - minZ) + 1;

        final int gridW = (int) Math.ceil(width / cellSize);
        final int gridH = (int) Math.ceil(height / cellSize);

        final int[] grid = new int[gridW * gridH];
        for (int i = 0; i < grid.length; i++) grid[i] = -1;

        final ArrayList<BlockPos> points = new ArrayList<>();
        final ArrayList<Integer> active = new ArrayList<>();

        BlockPos first = new BlockPos(minX + rng.nextInt((maxX - minX) + 1), 0, minZ + rng.nextInt((maxZ - minZ) + 1));
        points.add(first);
        active.add(0);
        putInGrid(first, 0, minX, minZ, cellSize, gridW, gridH, grid);

        final double r2 = r * r;
        final int k = 30;

        while (!active.isEmpty())
        {
            int activeIdxPos = rng.nextInt(active.size());
            int pIndex = active.get(activeIdxPos);
            BlockPos p = points.get(pIndex);

            boolean found = false;

            for (int attempt = 0; attempt < k; attempt++)
            {
                double angle = rng.nextDouble() * (Math.PI * 2.0);
                double radius = r * Math.sqrt(1.0 + rng.nextDouble() * 3.0);

                double cx = p.getX() + Math.cos(angle) * radius;
                double cz = p.getZ() + Math.sin(angle) * radius;

                int ix = (int) Math.round(cx);
                int iz = (int) Math.round(cz);

                if (ix < minX || ix > maxX || iz < minZ || iz > maxZ) continue;

                BlockPos q = new BlockPos(ix, 0, iz);

                if (isFarEnough(q, points, minX, minZ, cellSize, gridW, gridH, grid, r2))
                {
                    int newIndex = points.size();
                    points.add(q);
                    active.add(newIndex);
                    putInGrid(q, newIndex, minX, minZ, cellSize, gridW, gridH, grid);
                    found = true;
                    break;
                }
            }

            if (!found)
            {
                int last = active.size() - 1;
                active.set(activeIdxPos, active.get(last));
                active.remove(last);
            }
        }

        return points;
    }

    private static void putInGrid(BlockPos p, int pointIndex, int minX, int minZ, double cellSize, int gridW, int gridH, int[] grid)
    {
        int gx = (int) ((p.getX() - minX) / cellSize);
        int gz = (int) ((p.getZ() - minZ) / cellSize);

        if (gx < 0) gx = 0;
        if (gz < 0) gz = 0;
        if (gx >= gridW) gx = gridW - 1;
        if (gz >= gridH) gz = gridH - 1;

        grid[gz * gridW + gx] = pointIndex;
    }

    private static boolean isFarEnough(BlockPos q, ArrayList<BlockPos> points, int minX, int minZ, double cellSize, int gridW, int gridH, int[] grid, double r2)
    {
        int qgx = (int) ((q.getX() - minX) / cellSize);
        int qgz = (int) ((q.getZ() - minZ) / cellSize);

        int startX = Math.max(0, qgx - 2);
        int endX = Math.min(gridW - 1, qgx + 2);
        int startZ = Math.max(0, qgz - 2);
        int endZ = Math.min(gridH - 1, qgz + 2);

        for (int gz = startZ; gz <= endZ; gz++)
        {
            int row = gz * gridW;
            for (int gx = startX; gx <= endX; gx++)
            {
                int idx = grid[row + gx];
                if (idx == -1) continue;

                BlockPos p = points.get(idx);
                long dx = (long) p.getX() - q.getX();
                long dz = (long) p.getZ() - q.getZ();
                double dist2 = (double) dx * dx + (double) dz * dz;

                if (dist2 < r2) return false;
            }
        }

        return true;
    }
}
