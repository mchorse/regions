package mchorse.regions.regions;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

/**
 * Entity importer class
 * 
 * This class is responsible for importing the entities. Why in a separate class 
 * and not in the {@link RegionImporter}? Because it will result into collision 
 * of entities with same UUID's at that time.
 * 
 * Here, we are scheduling the entity import procedure to 2 ticks later after 
 * the RegionImporter import.  
 */
public class EntityImporter
{
    public World world;
    public NBTTagList entities;

    private int counter = 2;

    public EntityImporter(World world, NBTTagList entities)
    {
        this.world = world;
        this.entities = entities;
    }

    public void attach()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event)
    {
        if (event.world != this.world || counter-- > 0)
        {
            return;
        }

        for (int i = 0; i < this.entities.tagCount(); i++)
        {
            NBTTagCompound tag = this.entities.getCompoundTagAt(i);
            EntityLivingBase entity = (EntityLivingBase) EntityList.createEntityFromNBT(tag, this.world);

            this.world.spawnEntityInWorld(entity);
        }

        MinecraftForge.EVENT_BUS.unregister(this);
    }
}