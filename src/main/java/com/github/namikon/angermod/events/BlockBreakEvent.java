package com.github.namikon.angermod.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import java.util.List;

import com.github.namikon.angermod.AngerMod;
import com.github.namikon.angermod.auxiliary.EntityHelper;
import com.github.namikon.angermod.auxiliary.LogHelper;
import com.github.namikon.angermod.auxiliary.MinecraftBlock;
import com.github.namikon.angermod.config.AngerModConfig;

/**
 * Class for handling all block-break code that will cause mobs become hostile against the player, depending on
 * the configuration
 * @author Namikon
 *
 */
public class BlockBreakEvent {
	private AngerModConfig _mConfig = null;
	
	public BlockBreakEvent(AngerModConfig pCfgMan) {
		_mConfig = pCfgMan;
	}
	
	@SubscribeEvent
	public void onBreakBlock(BreakEvent pEvent)
	{
		try
		{
			for (MinecraftBlock tBlock : _mConfig.BlacklistedBlocks)
			{
				if (tBlock.isEqualTo(pEvent))
				{
					if (pEvent.getPlayer().dimension == -1) // Nether
						EntityHelper.DealDamageToEntitiesInRange(pEvent.getPlayer(), _mConfig.PigmenAggrorange, EntityPigZombie.class, 0);
					else if (pEvent.getPlayer().dimension == 1) // End
						EntityHelper.DealDamageToEntitiesInRange(pEvent.getPlayer(), _mConfig.EndermanAggrorange, EntityEnderman.class, 0);
				}
			}
		}
		catch (Exception e)
		{
			LogHelper.warn("BlockBreakEvent.onBreakBlock.Error", "An error occoured while processing onBreakBlock. Please report");
			LogHelper.DumpStack("BlockBreakEvent.onBreakBlock.Stack", e);
		}
	}
}
