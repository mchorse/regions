package mchorse.regions.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

/**
 * Command /regions
 * 
 * This command is responsible for defining, saving and restoring the freaking 
 * regions. It looks like we'll have a lot of work to do on this command.
 */
public class CommandRegions extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "regions";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "regions.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        String action = args[0];

        if (action.equals("define") && args.length >= 8)
        {
            String name = args[1];

            BlockPos pos1 = CommandBase.parseBlockPos(sender, args, 2, true);
            BlockPos pos2 = CommandBase.parseBlockPos(sender, args, 5, true);

            BlockPos min = new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
            BlockPos max = new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));

            this.defineRegion(server, sender, name, min, max);
        }
        else if (action.equals("save") && args.length >= 3)
        {
            String name = args[1];
            String state = args[2];

            this.saveRegion(server, sender, name, state);
        }
        else if (action.equals("restore") && args.length >= 3)
        {
            String name = args[1];
            String state = args[2];

            this.restoreRegion(server, sender, name, state);
        }
        else
        {
            throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    /**
     * Define a region 
     * 
     * @todo implement
     */
    private void defineRegion(MinecraftServer server, ICommandSender sender, String name, BlockPos min, BlockPos max)
    {

    }

    /**
     * Save a region 
     * 
     * @todo implement
     */
    private void saveRegion(MinecraftServer server, ICommandSender sender, String name, String state)
    {

    }

    /**
     * Restore a region 
     * 
     * @todo implement
     */
    private void restoreRegion(MinecraftServer server, ICommandSender sender, String name, String state)
    {

    }
}