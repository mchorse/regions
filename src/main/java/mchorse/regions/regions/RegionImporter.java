package mchorse.regions.regions;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RegionImporter
{
    public RegionRange range;
    public World world;

    public RegionImporter(RegionRange range, World world)
    {
        this.range = range;
        this.world = world;
    }

    public void importRegion(File folder) throws IOException
    {
        File blocks = new File(folder + "/blocks.dat");
        RandomAccessFile file = new RandomAccessFile(blocks, "rw");
        RegionRange range = new RegionRange();

        range.read(file);

        BlockPos size = range.getSize();

        for (int i = 0; i <= size.getX(); i++)
        {
            for (int j = 0; j <= size.getY(); j++)
            {
                for (int k = 0; k <= size.getZ(); k++)
                {
                    this.restoreBlock(file, i, j, k);
                }
            }
        }

        file.close();
    }

    @SuppressWarnings("deprecation")
    private void restoreBlock(RandomAccessFile file, int i, int j, int k) throws IOException
    {
        int id = file.readShort();
        int meta = file.readByte();

        Block block = Block.getBlockById(id);

        world.setBlockState(this.range.min.add(i, j, k), block.getStateFromMeta(meta));
    }
}
