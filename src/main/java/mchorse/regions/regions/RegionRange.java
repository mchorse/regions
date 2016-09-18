package mchorse.regions.regions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.util.math.BlockPos;

public class RegionRange
{
    public BlockPos min;
    public BlockPos max;

    public RegionRange(BlockPos min, BlockPos max)
    {
        this.min = min;
        this.max = max;
    }

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
