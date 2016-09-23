package mchorse.regions;

import java.io.File;

import mchorse.regions.commands.CommandRegions;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * Regions mod
 * 
 * This mod allows defining world regions for further state saving and 
 * restoring. Great for damage control, region versioning and for dynamic 
 * machinima scenes.
 */
@Mod(modid = Regions.MODID, version = Regions.VERSION)
public class Regions
{
    public static final String MODID = "regions";
    public static final String VERSION = "0.1.3";

    /**
     * Server file
     */
    public static File serverFile(String path)
    {
        return new File(DimensionManager.getCurrentSaveRootDirectory() + "/" + path);
    }

    @EventHandler
    public void registerCommand(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandRegions());

        serverFile("regions").mkdir();
    }
}
