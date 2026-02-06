package com.moon404.gunskills.struct;

import java.util.stream.IntStream;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class PerlinTerrain
{
    private final PerlinNoise perlin;
    private final double scale;

    public PerlinTerrain(long seed, double scale)
    {
        this.scale = scale;
        RandomSource random = RandomSource.create(seed);
        this.perlin = PerlinNoise.create(random, IntStream.of(0));
    }

    public int sampleHeight(int x, int z, int minY, int maxY)
    {
        double nx = x / scale;
        double nz = z / scale;
        double n = perlin.getValue(nx, 0, nz);
        double t = (n + 1) / 2;
        int h = (int)Math.round(Mth.lerp(t, minY, maxY));
        return Mth.clamp(h, minY, maxY);
    }
}
