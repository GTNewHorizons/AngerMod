package com.github.namikon.angermod.auxiliary;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class EntityHelper {
	/**
	 * Deal specified amount of Damage to all entities within given range, caused by the player
	 * @param pEntityPlayer
	 * @param pRange
	 * @param pTargetEntityClass
	 * @param pDamageToDeal
	 */
	public static void DealDamageToEntitiesInRange(EntityPlayer pEntityPlayer, int pRange, Class pTargetEntityClass, int pDamageToDeal)
	{
		LogHelper.debug("TriggerNeutralMobs called");
		int tAggroRange = 16;
		List<Entity> tEntities = null;
		
		int x = (int) pEntityPlayer.posX;
		int y = (int) pEntityPlayer.posY;
		int z = (int) pEntityPlayer.posZ;
		
		try
		{
			// Get player's boundary box
			AxisAlignedBB tBoundingBox = AxisAlignedBB.getBoundingBox(
					x - pRange,
					y - pRange,
					z - pRange,
					x + pRange + 1,
					y + pRange + 1,
					z + pRange + 1);
			
			tEntities = pEntityPlayer.worldObj.getEntitiesWithinAABB(pTargetEntityClass, tBoundingBox);
			
			for (Entity pEntity : tEntities)
			{
				pEntity.attackEntityFrom(DamageSource.causePlayerDamage(pEntityPlayer), pDamageToDeal);
			}
		}
		catch(Exception e)
		{
			LogHelper.error("EntityHelper.DealDamageToEntitiesInRange.Error", "Error while processing DealDamageToEntitiesInRange");
			LogHelper.DumpStack("EntityHelper.DealDamageToEntitiesInRange.Error.StackTrace", e);
		}	
	}
}
