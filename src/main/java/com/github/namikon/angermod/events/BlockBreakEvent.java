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
import com.github.namikon.angermod.auxiliary.LogHelper;
import com.github.namikon.angermod.auxiliary.MinecraftBlock;
import com.github.namikon.angermod.config.AngerModConfig;

/**
 * @author Namikon
 *
 */
public class BlockBreakEvent {
	private AngerModConfig _mConfig = null;
	
	public BlockBreakEvent(AngerModConfig pCfgMan) {
		_mConfig = pCfgMan;
	}
	
	@SubscribeEvent
	public void onBreakBlock(BreakEvent event)
	{
		for (MinecraftBlock tBlock : _mConfig.BlacklistedBlocks)
		{
			if (tBlock.isEqualTo(event))
			{
				TriggerNeutralMobs(event);
			}
		}
	}

	/**
	 * Attach neutral mobs with 0 damage to switch from neutral to hostile
	 * @param pEvent
	 */
	private void TriggerNeutralMobs(BreakEvent pEvent)
	{
		LogHelper.debug("TriggerNeutralMobs called");
		int tAggroRange = 16;
		List<Entity> tEntities = null;
		
		int x = pEvent.x;
		int y = pEvent.y;
		int z = pEvent.z;
		
		try
		{
			// Get player's boundary box
			AxisAlignedBB tBoundingBox = AxisAlignedBB.getBoundingBox(
					x - tAggroRange,
					y - tAggroRange,
					z - tAggroRange,
					x + tAggroRange + 1,
					y + tAggroRange + 1,
					z + tAggroRange + 1);
			
			EntityPlayer tPlayer = pEvent.getPlayer();
			if(tPlayer.dimension == -1) // TODO: Make it dynamic..?
			{
				tAggroRange = _mConfig.PigmenAggrorange;
				tEntities = pEvent.world.getEntitiesWithinAABB(EntityPigZombie.class, tBoundingBox);
			}
			else if(tPlayer.dimension == 1)
			{
				tAggroRange = _mConfig.EndermanAggrorange;
				tEntities = pEvent.world.getEntitiesWithinAABB(EntityEnderman.class, tBoundingBox);
			}
			
			for (Entity pEntity : tEntities)
			{
				pEntity.attackEntityFrom(DamageSource.causePlayerDamage(tPlayer), 0);
			}
		}
		catch(Exception e)
		{
			LogHelper.error("BlockBreakEvent.TriggerNeutralMobs.Error", "Error while processing TriggerEvent");
			LogHelper.DumpStack("BlockBreakEvent.TriggerNeutralMobs.Error.StackTrace", e);
		}	
	}
}
