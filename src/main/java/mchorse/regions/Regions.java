package mchorse.regions;

import mchorse.regions.commands.CommandRegions;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * Regions mod
 */
@Mod(modid = Regions.MODID, version = Regions.VERSION, serverSideOnly = true)
public class Regions
{
    public static final String MODID = "regions";
    public static final String VERSION = "0.1";

    @EventHandler
    public void registerCommands(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandRegions());
    }
}
