package mchorse.regions.regions;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
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

        File entityFile = new File(folder + "/entities.dat");

        if (!entityFile.exists()) return;

        /* Remove all entities within region */
        AxisAlignedBB aabb = new AxisAlignedBB(this.range.min, this.range.max);

        for (Entity entity : world.getEntitiesWithinAABB(Entity.class, aabb))
        {
            entity.setDead();
        }

        /* Load entities */
        NBTTagCompound entities = CompressedStreamTools.read(entityFile);
        NBTTagList list = (NBTTagList) entities.getTag("Entities");

        for (int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            EntityLivingBase entity = (EntityLivingBase) EntityList.createEntityFromNBT(tag, world);

            world.spawnEntityInWorld(entity);
        }
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
