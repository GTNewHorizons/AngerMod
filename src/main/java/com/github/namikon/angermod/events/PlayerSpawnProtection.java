package com.github.namikon.angermod.events;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

import com.github.namikon.angermod.AngerMod;
import com.github.namikon.angermod.config.AngerModConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import eu.usrv.yamcore.auxiliary.LogHelper;
import eu.usrv.yamcore.auxiliary.PermConfigHelper;
import eu.usrv.yamcore.auxiliary.PlayerChatHelper;
import eu.usrv.yamcore.auxiliary.PlayerHelper;
import eu.usrv.yamcore.iface.IPersistedDataBase;

/**
 * Class for handling the spawn protection. Give/set/watch events,... probably better to split that up later on...
 * @author Namikon
 *
 */
public class PlayerSpawnProtection {
	private static final String BED_TIMES = "BedTimes";
	private IPersistedDataBase _mPersistedConfig = null;
	private AngerModConfig _mCfgManager = null;
	
	public PlayerSpawnProtection(AngerModConfig pConfigManager, IPersistedDataBase pPersistedDataBase)
	{
		_mCfgManager = pConfigManager;
		_mPersistedConfig = pPersistedDataBase;
	}
	
	/**
	 * Watch player for any interaction
	 * @param pEvent
	 */
	@SubscribeEvent
    public void onInteractEvent(PlayerInteractEvent pEvent)
    {
		try
		{
			// Not server, do nothing
			if(pEvent.world.isRemote)
				return;
			
			EntityPlayer tEP = pEvent.entityPlayer;
			// No player, no bananas
			if (tEP == null)
			{
				AngerMod.Logger.error("EntityPlayer is null, but PlayerInteractEvent event was raised. This should not happen!");
				return;
			}
			
			if (!tEP.capabilities.disableDamage) // Has no enabled protection, nothing to do here
				return;
			
			if (HasNoobProtection(tEP)) // As long as player has noob protection, opening chests and stuff is allowed
				return;
	
			// Ignore right click on air, and left click in order to break your grave. Everything else removes protection
			if (pEvent.action == Action.RIGHT_CLICK_AIR || pEvent.action == Action.LEFT_CLICK_BLOCK) 
				return;
			else
				PlayerHelper.RemoveProtection(tEP);
		}
		catch (Exception e)
		{
			AngerMod.Logger.warn("PlayerSpawnProtection.onInteractEvent.Error", "An error occoured while processing onInteractEvent. Please report");
			AngerMod.Logger.DumpStack("PlayerSpawnProtection.onInteractEvent.Stack", e);
		}
	}
	
	/**
	 * Protect the player from any damage or becoming a target to hostile mobs as they spawn
	 * @param event
	 */
	@SubscribeEvent
	public void onPlayerSpawn(EntityJoinWorldEvent event)
	{
		try
		{
			if(event.world.isRemote)
				return;
	
			if (event.entity instanceof EntityPlayerMP)
			{
				EntityPlayerMP tEP = (EntityPlayerMP)event.entity;
				if (!tEP.capabilities.disableDamage)
				{
					// Not THE BEST idea, but.. something..
					if(_mCfgManager.RespawnProtectionOnlyOnDeath && tEP.getScore() > 0)
						return;
					
					PlayerHelper.GiveProtection(tEP);
				}
			}
		}
		catch (Exception e)
		{
			AngerMod.Logger.warn("PlayerSpawnProtection.onPlayerSpawn.Error", "An error occoured while processing onPlayerSpawn. Please report");
			AngerMod.Logger.DumpStack("PlayerSpawnProtection.onPlayerSpawn.Stack", e);
		}	
	}
	
	/**
	 * Detect if player is sleeping in his bed
	 * @param pEvent
	 */
	@SubscribeEvent
	public void onSleepInBedEvent(PlayerSleepInBedEvent pEvent)
	{
		try
		{
			EntityPlayer tEP = pEvent.entityPlayer;
			
			// No player, no bananas
			if (tEP == null)
			{
				AngerMod.Logger.error("EntityPlayer is null, but sleep event was raised. This should not happen!");
				return;
			}
	
			// Only serverside
			if(tEP.worldObj.isRemote)
				return;
	
			// This is.. stupid.. pEvent.result should be set to OK if at night, but it's always null -.-
			if (!tEP.worldObj.isDaytime())
			{
				int tSleepyTimes = increasePlayerBedTime(tEP.getUniqueID());
				
				// Only notify player if they have not reached the theshold yet
				if(tSleepyTimes < _mCfgManager.SleepingThreshold)
				{
					PlayerChatHelper.SendNotifyNormal(tEP, String.format("Your protection weakens as you sleep (%s/%s)", tSleepyTimes, _mCfgManager.SleepingThreshold));
				}
				else if (tSleepyTimes == _mCfgManager.SleepingThreshold)
				{
					PlayerChatHelper.SendNotifyWarning(tEP, "You are now only protected once you respawn, and");
					PlayerChatHelper.SendNotifyWarning(tEP, "it will fade as soon as you attack, use tools or machines");
				}
			}
		}
		catch (Exception e)
		{
			AngerMod.Logger.warn("PlayerSpawnProtection.onSleepInBedEvent.Error", "An error occoured while processing onSleepInBedEvent. Please report");
			AngerMod.Logger.DumpStack("PlayerSpawnProtection.onSleepInBedEvent.Stack", e);
		}		
	}
	
	/**
	 * Remove player-protection as soon as the player starts to hit an entity
	 * @param event
	 */
	@SubscribeEvent
	public void onAttackEntity(AttackEntityEvent event)
	{
		try
		{
			if(event.entityPlayer.worldObj.isRemote)
				return;
			
			if (event.entityPlayer.capabilities.disableDamage)
				PlayerHelper.RemoveProtection(event.entityPlayer);
		}
		catch (Exception e)
		{
			AngerMod.Logger.warn("PlayerSpawnProtection.onAttackEntity.Error", "An error occoured while processing onAttackEntity. Please report");
			AngerMod.Logger.DumpStack("PlayerSpawnProtection.onAttackEntity.Stack", e);
		}	
	}

	private int getPlayerBedTimes(UUID pUserUID)
	{
		return _mPersistedConfig.getValueAsInt(PermConfigHelper.BuildConfigValueName(pUserUID, BED_TIMES), 0);
	}
	
	private boolean HasNoobProtection(EntityPlayer pEntityPlayer)
	{
		if (pEntityPlayer == null)
			return false;
		
		if (getPlayerBedTimes(pEntityPlayer.getUniqueID()) < _mCfgManager.SleepingThreshold)
			return true;
		else
			return false;
	}
	
	private int increasePlayerBedTime(UUID pUserUID)
	{
		int oldVal = getPlayerBedTimes(pUserUID);
		oldVal++;
		
		_mPersistedConfig.setValue(PermConfigHelper.BuildConfigValueName(pUserUID, BED_TIMES), oldVal);
		return oldVal;
	}
}
