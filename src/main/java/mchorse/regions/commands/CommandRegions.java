package mchorse.regions.commands;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import mchorse.regions.Regions;
import mchorse.regions.regions.RegionExporter;
import mchorse.regions.regions.RegionImporter;
import mchorse.regions.regions.RegionRange;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

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

    /**
     * Execute the command. Delegate sub-actions to private methods of this 
     * command.
     */
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
     */
    private void defineRegion(MinecraftServer server, ICommandSender sender, String name, BlockPos min, BlockPos max) throws CommandException
    {
        int dx = max.getX() - min.getX();
        int dy = max.getY() - min.getY();
        int dz = max.getZ() - min.getZ();

        if (Math.abs(dx) == 0 || Math.abs(dy) == 0 || Math.abs(dz) == 0)
        {
            throw new CommandException("regions.error.zero_coord", dx, dy, dz);
        }

        File folder = Regions.serverFile("regions/" + name + "/");
        File region = Regions.serverFile("regions/" + name + "/region.dat");

        if (region.exists())
        {
            throw new CommandException("regions.error.region_defined", name);
        }

        folder.mkdirs();

        try
        {
            RandomAccessFile file = new RandomAccessFile(region, "rw");
            RegionRange result = new RegionRange(min, max);

            result.write(file);
            file.close();
        }
        catch (IOException e)
        {
            throw new CommandException("regions.error.io", e.getMessage());
        }

        sender.addChatMessage(new TextComponentTranslation("regions.success.region_defined", name));
    }

    /**
     * Save a region 
     */
    private void saveRegion(MinecraftServer server, ICommandSender sender, String name, String state) throws CommandException
    {
        File region = Regions.serverFile("regions/" + name + "/region.dat");
        File save = Regions.serverFile("regions/" + name + "/" + state + "/");

        if (!region.exists())
        {
            throw new CommandException("regions.error.region_undefined", name);
        }

        save.mkdirs();

        RegionRange range = new RegionRange();

        try
        {
            RandomAccessFile file = new RandomAccessFile(region, "rw");

            range.read(file);
            file.close();

            RegionExporter exporter = new RegionExporter(range, server.worldServerForDimension(0));

            exporter.exportRegion(save);
        }
        catch (IOException e)
        {
            throw new CommandException("regions.error.io", e.getMessage());
        }

        sender.addChatMessage(new TextComponentTranslation("regions.success.region_saved", name, state));
    }

    /**
     * Restore a region 
     */
    private void restoreRegion(MinecraftServer server, ICommandSender sender, String name, String state) throws CommandException
    {
        File region = Regions.serverFile("regions/" + name + "/region.dat");
        File save = Regions.serverFile("regions/" + name + "/" + state + "/");

        if (!region.exists())
        {
            throw new CommandException("regions.error.region_undefined", name);
        }

        if (!save.exists())
        {
            throw new CommandException("regions.error.state_undefined", name, state);
        }

        RegionRange range = new RegionRange();

        try
        {
            RandomAccessFile file = new RandomAccessFile(region, "rw");

            range.read(file);
            file.close();

            RegionImporter importer = new RegionImporter(range, server.worldServerForDimension(0));

            importer.importRegion(save);
        }
        catch (IOException e)
        {
            throw new CommandException("regions.error.io", e.getMessage());
        }

        sender.addChatMessage(new TextComponentTranslation("regions.success.region_restored", name, state));
    }
}