package com.github.namikon.angermod.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.github.namikon.angermod.AngerMod;
import com.github.namikon.angermod.config.AngerModConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import eu.usrv.yamcore.auxiliary.LogHelper;
import eu.usrv.yamcore.auxiliary.MathHelper;


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
			if(!(pEvent.source.getSourceOfDamage() instanceof EntityPlayer) || pEvent.source.getSourceOfDamage() instanceof FakePlayer)
				return;
			else
			{
				EntityPlayer tEP = (EntityPlayer) pEvent.source.getSourceOfDamage();
				ItemStack tHeldItem = tEP.inventory.getCurrentItem();
				if(tHeldItem != null)
				{
					String tUnlocItemName = tHeldItem.getUnlocalizedName().toLowerCase();
					for (String s : AngerMod._cfgManager.ButcherItems)
					{
						if(tUnlocItemName.contains(s.toLowerCase()))
							return; // Player used defined butcher-item to slay animal. Trigger no explosion
					}
				}
				
				World tW = tEP.worldObj;
				
				if (!MathHelper.FlipTheCoin(_mCfg.KamikazeChance))
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
			AngerMod.Logger.warn("KamikazeRevenge.onEntityDied.Error", "An error occoured while processing onEntityDied. Please report");
			AngerMod.Logger.DumpStack("KamikazeRevenge.onEntityDied.Stack", e);
		}
	}
}
