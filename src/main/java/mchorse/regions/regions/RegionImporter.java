package mchorse.regions.regions;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Region importer
 * 
 * This class is responsible for importing saved state from given folder into 
 * given region range and world.
 */
public class RegionImporter
{
    public RegionRange range;
    public World world;

    public RegionImporter(RegionRange range, World world)
    {
        this.range = range;
        this.world = world;
    }

    /**
     * Import given state from folder to the defined region. This method restores 
     * blocks and entities and tile entities, if those are exist. 
     */
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

        /* Restore entities */
        this.restoreTileEntities(folder);
        this.restoreEntities(folder);
    }

    /**
     * Restore block at given coordinates from file
     */
    @SuppressWarnings("deprecation")
    private void restoreBlock(RandomAccessFile file, int i, int j, int k) throws IOException
    {
        int id = file.readShort();
        int meta = file.readByte();

        BlockPos pos = this.range.min.add(i, j, k);
        Block block = Block.getBlockById(id);

        world.removeTileEntity(pos);
        world.setBlockState(pos, block.getStateFromMeta(meta), 2);
    }

    /**
     * Restore tile entities in the given state folder. 
     */
    private void restoreTileEntities(File folder) throws IOException
    {
        File tiles = new File(folder + "/tiles.dat");

        if (!tiles.exists())
        {
            return;
        }

        NBTTagCompound tag = CompressedStreamTools.read(tiles);
        NBTTagList entities = (NBTTagList) tag.getTag("Tiles");

        if (entities.tagCount() == 0)
        {
            return;
        }

        for (int i = 0; i < entities.tagCount(); i++)
        {
            TileEntity entity = TileEntity.func_190200_a(this.world, entities.getCompoundTagAt(i));

            world.setTileEntity(entity.getPos(), entity);
        }
    }

    /**
     * Restore entities from separate file "entities.dat" in given state 
     * folder. 
     */
    private void restoreEntities(File folder) throws IOException
    {
        File entityFile = new File(folder + "/entities.dat");

        if (!entityFile.exists())
        {
            return;
        }

        /* Load entities */
        NBTTagCompound entities = CompressedStreamTools.read(entityFile);
        final NBTTagList list = (NBTTagList) entities.getTag("Entities");

        if (list.tagCount() == 0)
        {
            return;
        }

        /* Remove all entities within region */
        AxisAlignedBB aabb = new AxisAlignedBB(this.range.min, this.range.max);

        for (Entity entity : world.getEntitiesWithinAABB(Entity.class, aabb))
        {
            if (entity instanceof EntityPlayer)
            {
                continue;
            }

            entity.setDead();
        }

        /* Schedule entity spawn later (to avoid conflict with already existing 
         * entities who share same UUID) */
        new EntityImporter(this.world, list).attach();
    }
}
