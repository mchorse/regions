package mchorse.regions.regions;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
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

    public boolean saveEntities = true;

    public RegionExporter(RegionRange range, World world)
    {
        this.range = range;
        this.world = world;
    }

    /**
     * This method is responsible for exporting blocks and tile entities and 
     * entities if those are exist, in the given region (passed in constructor). 
     */
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
        this.saveTileEntities(folder);
        this.saveEntities(folder);
    }

    /**
     * Save a block to the file and add its tile entity to the tiles list if 
     * it's not a null. 
     */
    private void saveBlock(RandomAccessFile file, int i, int j, int k) throws IOException
    {
        BlockPos pos = this.range.min.add(i, j, k);
        IBlockState state = world.getBlockState(pos);

        int id = Block.getIdFromBlock(state.getBlock());
        int meta = state.getBlock().getMetaFromState(state);

        file.writeShort(id);
        file.writeByte(meta);

        /* Add tile entity to tiles list */
        TileEntity tile = world.getTileEntity(pos);

        if (tile != null)
        {
            this.tiles.appendTag(tile.writeToNBT(new NBTTagCompound()));
        }
    }

    /**
     * Save tile entities to a separate file named "tiles.dat" if the tiles 
     * were added to tiles list during the process of block saving.
     */
    private void saveTileEntities(File folder) throws IOException
    {
        if (tiles.tagCount() == 0)
        {
            return;
        }

        NBTTagCompound output = new NBTTagCompound();

        output.setTag("Tiles", this.tiles);
        CompressedStreamTools.write(output, new File(folder + "/tiles.dat"));
    }

    /**
     * Save entities to a separate file "entities.dat" in given folder 
     */
    private void saveEntities(File folder) throws IOException
    {
        if (!this.saveEntities)
        {
            return;
        }

        AxisAlignedBB aabb = new AxisAlignedBB(this.range.min, this.range.max);
        NBTTagCompound output = new NBTTagCompound();
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);

        if (entities.isEmpty()) return;

        for (EntityLivingBase entity : entities)
        {
            if (entity instanceof EntityPlayer)
            {
                continue;
            }

            NBTTagCompound tag = entity.writeToNBT(new NBTTagCompound());
            String id = EntityList.getEntityStringFromClass(entity.getClass());

            tag.setString("id", id);
            this.entities.appendTag(tag);
        }

        output.setTag("Entities", this.entities);

        CompressedStreamTools.write(output, new File(folder + "/entities.dat"));
    }
}
