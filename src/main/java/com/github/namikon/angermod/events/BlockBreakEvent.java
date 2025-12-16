package com.github.namikon.angermod.events;

import java.util.List;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

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

    @SubscribeEvent
    public void onBreakBlock(BreakEvent event) {
        if (event.getPlayer() instanceof FakePlayer) return;

        var dimension = event.getPlayer().dimension;

        if (dimension == -1) {
            if (AngerModConfig.BlacklistedNetherBlocks.contains(event.block, event.blockMetadata)) {
                aggroZombiePigmenInRange(event.getPlayer(), AngerModConfig.PigmenAggrorange);
            }
        } else if (dimension == 1) {
            if (AngerModConfig.BlacklistedEndBlocks.contains(event.block, event.blockMetadata)) {
                aggroEndermenInRange(event.getPlayer(), AngerModConfig.EndermanAggrorange);
            }
        }
    }

    public static void aggroEndermenInRange(EntityPlayer player, int range) {
        int x = (int) player.posX;
        int y = (int) player.posY;
        int z = (int) player.posZ;

        // Define the area to check for entities
        AxisAlignedBB tBoundingBox = AxisAlignedBB
                .getBoundingBox(x - range, y - range, z - range, x + range + 1, y + range + 1, z + range + 1);

        List<? extends EntityEnderman> endermen = player.worldObj
                .getEntitiesWithinAABB(EntityEnderman.class, tBoundingBox);

        for (var enderman : endermen) {
            enderman.setTarget(player);
            enderman.setScreaming(true);
        }
    }

    public static void aggroZombiePigmenInRange(EntityPlayer player, int range) {
        int x = (int) player.posX;
        int y = (int) player.posY;
        int z = (int) player.posZ;

        // Define the area to check for entities
        AxisAlignedBB boundingBox = AxisAlignedBB
                .getBoundingBox(x - range, y - range, z - range, x + range + 1, y + range + 1, z + range + 1);

        List<? extends EntityPigZombie> pigmen = player.worldObj
                .getEntitiesWithinAABB(EntityPigZombie.class, boundingBox);

        for (var zombiePigman : pigmen) {
            zombiePigman.setTarget(player);
            zombiePigman.worldObj.playSoundAtEntity(zombiePigman, "mob.zombiepig.zpighurt", 1.0F, 1.0F);
        }
    }

}
