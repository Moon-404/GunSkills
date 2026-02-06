package com.moon404.gunskills.struct;

import java.util.ArrayList;
import java.util.List;

import com.moon404.gunskills.GunSkills;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class StructureGen
{
    public static final class Entry
    {
        public final ResourceLocation id;
        public final int count;
        public final int radius;
        public final int margin;
        public int processed;

        public Entry(ResourceLocation id, int count, int radius, int margin)
        {
            this.id = id;
            this.count = count;
            this.radius = radius;
            this.margin = margin;
            this.processed = 0;
        }
    }

    private static StructureGenJob current;
    private static final List<Entry> REGISTRY = new ArrayList<>();

    public static boolean clear()
    {
        if (current != null) return false;
        REGISTRY.clear();
        return true;
    }

    public static boolean add(ResourceLocation id, int count, int radius, int margin)
    {
        if (current != null) return false;
        REGISTRY.add(new Entry(id, count, radius, margin));
        return true;
    }

    public static boolean startJob(ServerLevel level, int x1, int z1, int x2, int z2, long seed, int structuresPerTick)
    {
        if (current != null) return false;
        current = new StructureGenJob(level, x1, z1, x2, z2, seed, structuresPerTick);
        return true;
    }

    public static void tick()
    {
        if (current == null) return;
        boolean done = current.tick();
        if (done) current = null;
    }

    private static final class StructureGenJob
    {
        final ServerLevel level;
        final int x1, z1, x2, z2;
        final long seed;
        final int structuresPerTick;
        final int minRadius, maxRadius;
        final List<BlockPos> candidates;
        final RandomSource random;

        StructureGenJob(ServerLevel level, int x1, int z1, int x2, int z2, long seed, int structuresPerTick)
        {
            this.level = level;
            this.x1 = x1;
            this.z1 = z1;
            this.x2 = x2;
            this.z2 = z2;
            this.seed = seed;
            this.structuresPerTick = structuresPerTick;
            this.random = RandomSource.create(seed);

            int minr = Integer.MAX_VALUE, maxr = 0;
            for (Entry e : REGISTRY)
            {
                minr = Math.min(e.radius, minr);
                maxr = Math.max(e.radius, maxr);
            }
            this.minRadius = minr;
            this.maxRadius = maxr;
            this.candidates = PoissonDisk.sample(x1, z1, x2, z2, minr, seed);

            int border = maxr * 2;
            this.candidates.removeIf(p -> (p.getX() - x1) < border || (p.getZ() - z1) < border || (x2 - p.getX()) < border || (z2 - p.getZ()) < border);
        }

        boolean tick()
        {
            for (Entry entry : REGISTRY)
            {
                if (entry.processed >= entry.count) continue;
                int index = random.nextInt(candidates.size());
                BlockPos pos = candidates.get(index);

                StructureTemplate template = level.getStructureManager().get(entry.id).get();
                StructurePlaceSettings settings = new StructurePlaceSettings();
                settings.setIgnoreEntities(false);
                template.placeInWorld(level, pos, BlockPos.ZERO, settings, random, 2);

                int radiusSqr = entry.radius * entry.radius;
                candidates.removeIf(p -> p.distSqr(pos) < radiusSqr);
                entry.processed++;
                return false;
            }
            if (candidates.isEmpty()) GunSkills.LOGGER.warn("StructureGen no pos left!");
            return true;
        }
    }
}
