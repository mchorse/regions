package mchorse.regions.regions;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Region exporter class
 * 
 * This class is responsible for exporting the region from given region range 
 * and given world.
 */
public class RegionExporter
{
    public RegionRange range;
    public World world;

    public List<Short> ids = new ArrayList<Short>();

    public RegionExporter(RegionRange range, World world)
    {
        this.range = range;
        this.world = world;
    }

    public void export(File folder) throws IOException
    {
        File blocks = new File(folder + "/blocks.dat");
        RandomAccessFile file = new RandomAccessFile(blocks, "rw");
        BlockPos size = range.getSize();

        range.write(file);

        for (int i = 0; i <= size.getX(); i++)
        {
            for (int j = 0; j <= size.getY(); j++)
            {
                for (int k = 0; k <= size.getZ(); k++)
                {
                    this.saveBlock(file, i, j, k);
                }
            }
        }

        file.close();
    }

    private void saveBlock(RandomAccessFile file, int i, int j, int k) throws IOException
    {
        IBlockState state = world.getBlockState(this.range.min.add(i, j, k));

        int id = Block.getIdFromBlock(state.getBlock());
        int meta = state.getBlock().getMetaFromState(state);

        file.writeShort(id);
        file.writeByte(meta);
    }
}
