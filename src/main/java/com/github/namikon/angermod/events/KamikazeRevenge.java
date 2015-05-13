package com.github.namikon.angermod.events;

import java.util.UUID;

import com.github.namikon.angermod.auxiliary.LogHelper;
import com.github.namikon.angermod.auxiliary.MathHelper;
import com.github.namikon.angermod.auxiliary.PlayerChatHelper;
import com.github.namikon.angermod.config.AngerModConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;


/**
 * Just because reasons
 * @author Namikon
 *
 */
public class KamikazeRevenge {
	AngerModConfig _mCfg = null;
	
	public KamikazeRevenge(AngerModConfig pConfigManager) {
		_mCfg = pConfigManager;
	}
	
	/**
	 * Catch LivingDeathEvent. Fires whenever an Entity dies
	 * @param pEvent
	 */
	@SubscribeEvent
	public void onEntityDied(LivingDeathEvent pEvent)
	{
		try
		{
			if(!(pEvent.source.getSourceOfDamage() instanceof EntityPlayer))
				return;
			else
			{
				EntityPlayer tEP = (EntityPlayer) pEvent.source.getSourceOfDamage();
				World tW = tEP.worldObj;
				
				if (MathHelper.FlipTheCoin(_mCfg.KamikazeChance))
					return; // Head, you win
				else
				{
					// obey mobGriefing gamerule
					boolean tGriefingRule = tW.getGameRules().getGameRuleBooleanValue("mobGriefing");
					boolean flag = (tGriefingRule && _mCfg.KamikazeMobsDoTerrainDamage);
					
					tW.createExplosion(pEvent.entityLiving, pEvent.entityLiving.posX, pEvent.entityLiving.posY, pEvent.entityLiving.posZ, 1.5F, flag);
				}
			}
		}
		catch (Exception e)
		{
			LogHelper.warn("KamikazeRevenge.onEntityDied.Error", "An error occoured while processing onEntityDied. Please report");
			LogHelper.DumpStack("KamikazeRevenge.onEntityDied.Stack", e);
		}
	}
}
