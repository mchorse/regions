package mchorse.regions.regions;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
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

    public NBTTagList tiles = new NBTTagList();
    public NBTTagList entities = new NBTTagList();

    public RegionExporter(RegionRange range, World world)
    {
        this.range = range;
        this.world = world;
    }

    public void exportRegion(File folder) throws IOException
    {
        File blocks = new File(folder + "/blocks.dat");
        RandomAccessFile file = new RandomAccessFile(blocks, "rw");
        BlockPos size = this.range.getSize();

        this.range.write(file);

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

        /* Save entities */
        AxisAlignedBB aabb = new AxisAlignedBB(this.range.min, this.range.max);
        NBTTagCompound output = new NBTTagCompound();

        for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, aabb))
        {
            NBTTagCompound tag = entity.writeToNBT(new NBTTagCompound());

            tag.setString("id", EntityList.getEntityStringFromClass(entity.getClass()));

            this.entities.appendTag(tag);
        }

        output.setTag("Entities", this.entities);

        File entities = new File(folder + "/entities.dat");
        CompressedStreamTools.write(output, entities);
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
