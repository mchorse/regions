package mchorse.regions.regions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.util.math.BlockPos;

/**
 * Region range class
 * 
 * Instances of this class represent a region range, basically a region box with 
 * minimum and maximum points.
 */
public class RegionRange
{
    public BlockPos min;
    public BlockPos max;

    public RegionRange()
    {}

    public RegionRange(BlockPos min, BlockPos max)
    {
        this.min = min;
        this.max = max;
    }

    /**
     * Get dimensions of the region 
     */
    public BlockPos getSize()
    {
        return new BlockPos(this.max.getX() - this.min.getX(), this.max.getY() - this.min.getY(), this.max.getZ() - this.min.getZ());
    }

    /**
     * Get block count of the region 
     */
    public int getCount()
    {
        BlockPos size = this.getSize();

        return size.getX() * size.getY() * size.getZ();
    }

    /* Reading and writing methods */

    public void read(DataInput in) throws IOException
    {
        this.min = new BlockPos(in.readInt(), in.readInt(), in.readInt());
        this.max = new BlockPos(in.readInt(), in.readInt(), in.readInt());
    }

    public void write(DataOutput out) throws IOException
    {
        /* Minimum */
        out.writeInt(this.min.getX());
        out.writeInt(this.min.getY());
        out.writeInt(this.min.getZ());

        /* Maximum */
        out.writeInt(this.max.getX());
        out.writeInt(this.max.getY());
        out.writeInt(this.max.getZ());
    }
}
