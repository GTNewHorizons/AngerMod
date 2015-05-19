package com.github.namikon.angermod.events;

import com.github.namikon.angermod.AngerMod;

import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import eu.usrv.yamcore.auxiliary.EntityHelper;
import eu.usrv.yamcore.auxiliary.LogHelper;

public class EatCookedAnimalsEvent {
	
	@SubscribeEvent
	public void onPlayerUsesItem(PlayerUseItemEvent.Start pEvent)
	{
		try
		{
			EntityPlayer tEP = pEvent.entityPlayer;
			if (tEP == null)
				return;
			
			String tUsedItemName = pEvent.item.getUnlocalizedName();
			AngerMod.Logger.debug(String.format("Using item %s", tUsedItemName));
			
			TryTriggerAnimals(AngerMod._cfgManager.PigFoodTrigger, tUsedItemName, tEP, EntityPig.class);
			TryTriggerAnimals(AngerMod._cfgManager.CowFoodTrigger, tUsedItemName, tEP, EntityCow.class);
			TryTriggerAnimals(AngerMod._cfgManager.ChickenFoodTrigger, tUsedItemName, tEP, EntityChicken.class);
			TryTriggerAnimals(AngerMod._cfgManager.SheepFoodTrigger, tUsedItemName, tEP, EntitySheep.class);
		}
		catch (Exception e)
		{
			AngerMod.Logger.warn("EatCookedAnimalsEvent.onPlayerUsesItem.Error", "An error occoured while processing onPlayerUsesItem. Please report");
			AngerMod.Logger.DumpStack("EatCookedAnimalsEvent.onPlayerUsesItem.Stack", e);
		}
	}
	
	private void TryTriggerAnimals(String[] pKeywordList, String pUsedItem, EntityPlayer pEP, Class pMobClassToTrigger)
	{
		int tRange = AngerMod._cfgManager.FriendlyMobRevengeRadius;
		try
		{
			for(String s : pKeywordList)
				if(pUsedItem.contains(s))
					EntityHelper.DealDamageToEntitiesInRange(pEP, tRange, pMobClassToTrigger, 0);			
		}
		catch (Exception e)
		{
			AngerMod.Logger.warn("EatCookedAnimalsEvent.TryTriggerAnimals.Error", "An error occoured while processing TriggerAnimals. Please report");
			AngerMod.Logger.DumpStack("EatCookedAnimalsEvent.TryTriggerAnimals.Stack", e);
		}

	}
}
