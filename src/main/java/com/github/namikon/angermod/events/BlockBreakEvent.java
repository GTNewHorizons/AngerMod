package com.github.namikon.angermod.events;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import com.github.namikon.angermod.AngerMod;
import com.github.namikon.angermod.auxiliary.MinecraftBlock;
import com.github.namikon.angermod.config.AngerModConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import eu.usrv.yamcore.auxiliary.EntityHelper;

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
                        EntityHelper.DealDamageToEntitiesInRange(
                                pEvent.getPlayer(),
                                _mConfig.PigmenAggrorange,
                                EntityPigZombie.class,
                                0);
                    else if (pEvent.getPlayer().dimension == 1) // End
                        EntityHelper.DealDamageToEntitiesInRange(
                                pEvent.getPlayer(),
                                _mConfig.EndermanAggrorange,
                                EntityEnderman.class,
                                0);
                }
            }
        } catch (Exception e) {
            AngerMod.Logger.warn(
                    "BlockBreakEvent.onBreakBlock.Error",
                    "An error occoured while processing onBreakBlock. Please report");
            AngerMod.Logger.DumpStack("BlockBreakEvent.onBreakBlock.Stack", e);
        }
    }
}
