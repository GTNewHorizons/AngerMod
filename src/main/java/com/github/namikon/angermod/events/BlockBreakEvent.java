package com.github.namikon.angermod.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import com.github.namikon.angermod.AngerMod;
import com.github.namikon.angermod.auxiliary.MinecraftBlock;
import com.github.namikon.angermod.config.AngerModConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Class for handling all block-break code that will cause mobs become hostile against the player, depending on the
 * configuration
 *
 * @author Namikon
 *
 */
public class BlockBreakEvent {

    private AngerModConfig _mConfig = null;

    public BlockBreakEvent(AngerModConfig pCfgMan) {
        _mConfig = pCfgMan;
    }

    @SubscribeEvent
    public void onBreakBlock(BreakEvent pEvent) {
        try {
            for (MinecraftBlock tBlock : _mConfig.BlacklistedBlocks) {
                if (tBlock.isEqualTo(pEvent)) {
                    if (pEvent.getPlayer().dimension == -1) // Nether
                        aggroZombiePigmenInRange(pEvent.getPlayer(), _mConfig.PigmenAggrorange);
                    else if (pEvent.getPlayer().dimension == 1) // End
                        aggroEndermenInRange(pEvent.getPlayer(), _mConfig.EndermanAggrorange);
                }
            }
        } catch (Exception e) {
            AngerMod.Logger.warn(
                    "BlockBreakEvent.onBreakBlock.Error",
                    "An error occoured while processing onBreakBlock. Please report");
            AngerMod.Logger.DumpStack("BlockBreakEvent.onBreakBlock.Stack", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void aggroEndermenInRange(EntityPlayer player, int range) {
        List nearbyEntities;

        int x = (int) player.posX;
        int y = (int) player.posY;
        int z = (int) player.posZ;

        // Define the area to check for entities
        AxisAlignedBB tBoundingBox = AxisAlignedBB
                .getBoundingBox(x - range, y - range, z - range, x + range, y + range, z + range);

        nearbyEntities = player.worldObj.getEntitiesWithinAABB(EntityEnderman.class, tBoundingBox);

        for (Entity entity : (List<Entity>) nearbyEntities) {
            if (entity instanceof EntityEnderman enderman) {
                enderman.setTarget(player);
                enderman.setScreaming(true);
            }
        }
    }

    private static Method becomeAngryAtMethod;

    static {
        try {
            becomeAngryAtMethod = EntityPigZombie.class.getDeclaredMethod("becomeAngryAt", Entity.class);
            becomeAngryAtMethod.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public static void aggroZombiePigmenInRange(EntityPlayer player, int range) {
        int x = (int) player.posX;
        int y = (int) player.posY;
        int z = (int) player.posZ;

        // Define the area to check for entities
        AxisAlignedBB boundingBox = AxisAlignedBB
                .getBoundingBox(x - range, y - range, z - range, x + range, y + range, z + range);

        List<Entity> nearbyEntities = player.worldObj.getEntitiesWithinAABB(EntityPigZombie.class, boundingBox);

        for (Entity entity : nearbyEntities) {
            if (entity instanceof EntityPigZombie zombiePigman) {
                zombiePigman.setTarget(player);

                // Invoke the cached private method
                try {
                    if (becomeAngryAtMethod != null) {
                        becomeAngryAtMethod.invoke(zombiePigman, player);
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
