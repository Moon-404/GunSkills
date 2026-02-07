package com.moon404.gunskills.struct;

import java.util.ArrayList;
import java.util.List;

import com.moon404.gunskills.GunSkills;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
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
        final List<BlockPos> candidates;
        final RandomSource random;

        StructureGenJob(ServerLevel level, int x1, int z1, int x2, int z2, long seed, int structuresPerTick)
        {
            this.level = level;
            this.random = RandomSource.create(seed);

            int minr = Integer.MAX_VALUE, maxr = 0;
            for (Entry e : REGISTRY)
            {
                minr = Math.min(e.radius, minr);
                maxr = Math.max(e.radius, maxr);
            }
            this.candidates = PoissonDisk.sample(x1, z1, x2, z2, minr, seed);

            int border = maxr + 1;
            this.candidates.removeIf(p -> (p.getX() - x1) < border || (p.getZ() - z1) < border || (x2 - p.getX()) < border || (z2 - p.getZ()) < border);
        }

        boolean tick()
        {
            for (Entry entry : REGISTRY)
            {
                if (entry.processed >= entry.count) continue;
                if (candidates.isEmpty())
                {
                    if (candidates.isEmpty()) GunSkills.LOGGER.warn("StructureGen no pos left!");
                    return true;
                }
                int index = random.nextInt(candidates.size());
                BlockPos startPos = candidates.get(index);

                StructureTemplate template = level.getStructureManager().get(entry.id).get();

                Rotation rotation = Rotation.getRandom(random);
                Mirror mirror = switch (random.nextInt(3))
                {
                    case 1 -> Mirror.LEFT_RIGHT;
                    case 2 -> Mirror.FRONT_BACK;
                    default -> Mirror.NONE;
                };

                StructurePlaceSettings settings = new StructurePlaceSettings();
                settings.setIgnoreEntities(false);
                settings.setRotation(rotation);
                settings.setMirror(mirror);

                int startY = level.getHeight(Heightmap.Types.WORLD_SURFACE, startPos.getX(), startPos.getZ());
                startPos = startPos.offset(0, startY, 0);
                BoundingBox box = template.getBoundingBox(settings, startPos);

                BlockPos center = new BlockPos((box.minX() + box.maxX()) / 2, startY, (box.minZ() + box.maxZ()) / 2);
                startPos = startPos.offset(startPos).subtract(center);
                box = template.getBoundingBox(settings, startPos);

                // 清除已有方块
                for (int x = box.minX(); x <= box.maxX(); x++)
                {
                    for (int z = box.minZ(); z <= box.maxZ(); z++)
                    {
                        for (int y = box.minY(); y <= box.maxY(); y++)
                        {
                            BlockPos p = new BlockPos(x, y, z);
                            BlockState state = level.getBlockState(p);
                            if (state.isAir() || !state.getFluidState().isEmpty()) continue;
                            level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                        }
                    }
                }
                
                // 填充额外方块，平滑 margin
                for (int x = box.minX() - entry.margin; x <= box.maxX() + entry.margin; x++)
                {
                    for (int z = box.minZ() - entry.margin; z <= box.maxZ() + entry.margin; z++)
                    {
                        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1; // 地面所在格
                        int targetTop = box.minY() - 1; // 地基的下一格

                        int dx = 0;
                        if (x < box.minX()) dx = box.minX() - x;
                        else if (x > box.maxX()) dx = x - box.maxX();

                        int dz = 0;
                        if (z < box.minZ()) dz = box.minZ() - z;
                        else if (z > box.maxZ()) dz = z - box.maxZ();

                        // 在地基内，填充[地面上一格, 地基下一格]
                        if (dx == 0 && dz == 0)
                        {
                            for (int y = surfaceY + 1; y <= targetTop; y++)
                            {
                                BlockPos p = new BlockPos(x, y, z);
                                BlockState cur = level.getBlockState(p);
                                if (!cur.isAir() && cur.getFluidState().isEmpty()) continue;
                                BlockState fill = y == targetTop ? Blocks.GRASS_BLOCK.defaultBlockState() : Blocks.DIRT.defaultBlockState();
                                level.setBlock(p, fill, 2);
                            }
                        }
                        // 在地基外，平滑到指定格
                        else
                        {
                            double d = dx + dz;
                            if (d > entry.margin) continue;

                            double t = (d - 1) / entry.margin;
                            int smoothY = (int)Math.round((targetTop) * (1 - t) + surfaceY * t);
                            // 平滑格子低于地面，削除
                            if (smoothY < surfaceY)
                            {
                                for (int y = smoothY; y <= surfaceY; y++)
                                {
                                    BlockPos p = new BlockPos(x, y, z);
                                    BlockState fill = y == smoothY ? Blocks.GRASS_BLOCK.defaultBlockState() : Blocks.AIR.defaultBlockState();
                                    level.setBlock(p, fill, 2);
                                }
                            }
                            // 平滑格子高于地面，填充
                            else if (smoothY > surfaceY)
                            {
                                for (int y = surfaceY + 1; y <= smoothY; y++)
                                {
                                    BlockPos p = new BlockPos(x, y, z);
                                    BlockState fill = y == smoothY ? Blocks.GRASS_BLOCK.defaultBlockState() : Blocks.DIRT.defaultBlockState();
                                    level.setBlock(p, fill, 2);
                                }
                            }
                        }
                    }
                }
                
                template.placeInWorld(level, startPos, BlockPos.ZERO, settings, random, 2);

                BoundingBox radiusBox = box.inflatedBy(entry.radius);
                candidates.removeIf(p -> p.getX() >= radiusBox.minX() && p.getX() <= radiusBox.maxX() && p.getZ() >= radiusBox.minZ() && p.getZ() <= radiusBox.maxZ());
                entry.processed++;
                return false;
            }
            return true;
        }
    }
}
