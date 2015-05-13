package com.github.namikon.angermod.events;

import java.util.List;

import com.github.namikon.angermod.auxiliary.EntityHelper;
import com.github.namikon.angermod.auxiliary.LogHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class EatCookedAnimalsEvent {
	private String[] _mPigTrigger = {"pork"};
	private String[] _mCowTrigger = {"beef"};
	private String[] _mChickenTrigger = {"chicken", "egg"};
	private String[] _mSheepTrigger = {"mutton"};
	
	@SubscribeEvent
	public void onPlayerUsesItem(PlayerUseItemEvent.Start pEvent)
	{
		EntityPlayer tEP = pEvent.entityPlayer;
		if (tEP == null)
			return;
		
		String tUsedItemName = pEvent.item.getUnlocalizedName();
		TryTriggerAnimals(_mPigTrigger, tUsedItemName, tEP, EntityPig.class);
		TryTriggerAnimals(_mCowTrigger, tUsedItemName, tEP, EntityCow.class);
		TryTriggerAnimals(_mChickenTrigger, tUsedItemName, tEP, EntityChicken.class);
		TryTriggerAnimals(_mSheepTrigger, tUsedItemName, tEP, EntitySheep.class);
	}
	
	private void TryTriggerAnimals(String[] pKeywordList, String pUsedItem, EntityPlayer pEP, Class pMobClassToTrigger)
	{
		for(String s : pKeywordList)
			if(pUsedItem.contains(s))
				EntityHelper.DealDamageToEntitiesInRange(pEP, 16, pMobClassToTrigger, 0);
	}
}
