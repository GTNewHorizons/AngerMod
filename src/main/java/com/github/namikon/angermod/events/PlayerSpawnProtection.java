package com.github.namikon.angermod.events;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
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
	private class PlayerCoords
	{
		private LogHelper _mLog = AngerMod.Logger;
		public double _mX;
		public double _mY;
		public double _mZ;
		public long _mProtectionTime;
		
		public PlayerCoords(EntityPlayer pEp)
		{
			_mX = pEp.posX;
			_mY = pEp.posY;
			_mZ = pEp.posZ;
			_mProtectionTime = System.currentTimeMillis();
		}
		
		public boolean ProtectionVoided(double posX, double posY, double posZ)
		{
			try
			{
				int tTolerance = AngerMod._cfgManager.SpawnProtectionMoveTolerance;
				int tMaxtimeout = AngerMod._cfgManager.SpawnProtectionTimeout * 1000;
				
				boolean offsetX = OffsetMovement(_mX, posX, tTolerance);
				//_mLog.info(String.format("X: [%f] - [%f]", x, posX));
				
				boolean offsetY = OffsetMovement(_mY, posY, 5); // This is glitchy somehow, maybe need to remove it or increase it to like.. 10
				//_mLog.info(String.format("Y: [%f] - [%f]", y, posY));
				
				boolean offsetZ = OffsetMovement(_mZ, posZ, tTolerance);
				//_mLog.info(String.format("Z: [%f] - [%f]", z, posZ));
				
				long tCurrTime = System.currentTimeMillis();
				
				if (offsetX || offsetY || offsetZ)
				{
					_mLog.debug(String.format("Protection voided because of player movement X[%s] Y[%s] Z[%s]", offsetX, offsetY, offsetZ));
					return true;
				}
				
				if ((_mProtectionTime + tMaxtimeout) < tCurrTime)
				{
					_mLog.debug("Protection voided because of timeout");
					return true;
				}
				
				return false;
			}
			catch(Exception e)
			{
				// What did you do..?
				return true;
			}
		}
		
		private boolean OffsetMovement(double pBase, double posX, int pRange)
		{
			double tUpper = pBase + pRange;
			double tLower = pBase - pRange;
			
			return (posX > tUpper || posX < tLower);
		}
	}
	
	private AngerModConfig _mCfgManager = null;
	private Map<String, PlayerCoords> _mLastCoords = null;
	private Random _mRnd;
	private LogHelper _mLog = AngerMod.Logger;
	
	public PlayerSpawnProtection(AngerModConfig pConfigManager/*, IPersistedDataBase pPersistedDataBase*/)
	{
		_mCfgManager = pConfigManager;
		_mLastCoords = new HashMap<String, PlayerCoords>();
		_mRnd = new Random();
	}

	public void UpdateOrInitLastCoords(EntityPlayer tEP)
	{
		if (tEP == null)
			return;
		try
		{
			_mLog.debug(String.format("Updating player's information about his protection"));
			String UID = tEP.getUniqueID().toString();
			PlayerCoords pC = _mLastCoords.get(UID);
			if (pC != null)
				_mLastCoords.remove(UID);
			
			_mLastCoords.put(UID, new PlayerCoords(tEP));
		}
		catch(Exception e)
		{
			AngerMod.Logger.warn("PlayerSpawnProtection.UpdateOrInitLastCoords.Error", "An error occoured while processing UpdateOrInitLastCoords. Please report");
			AngerMod.Logger.DumpStack("PlayerSpawnProtection.UpdateOrInitLastCoords.Stack", e);
		}
	}
	
	private void CheckMovement(EntityPlayer pPlayer)
	{
		if (pPlayer == null)
			return;
		
		try
		{
			PlayerCoords pC = _mLastCoords.get(pPlayer.getUniqueID().toString());
	
			// for SOME reason (maybe restart) the player has no creative mode, but has disabled damage and
			// we do not have a record of that. So create one now..
			if (pC == null)
				UpdateOrInitLastCoords(pPlayer);
	
			pC = _mLastCoords.get(pPlayer.getUniqueID().toString());
			if (pC == null) // what the ..?
				_mLog.error("Can't keep track of players movement, something went terrible wrong");
			else
				if (pC.ProtectionVoided(pPlayer.posX, pPlayer.posY, pPlayer.posZ))
					PlayerHelper.RemoveProtection(pPlayer);
		}
		catch (Exception e)
		{
			AngerMod.Logger.warn("PlayerSpawnProtection.CheckMovement.Error", "An error occoured while processing CheckMovement. Please report");
			AngerMod.Logger.DumpStack("PlayerSpawnProtection.CheckMovement.Stack", e);
		}
	}
	
	public boolean HasProtection(EntityPlayer pEP)
	{
		if (pEP != null)
			return pEP.capabilities.disableDamage;
		else
			return false;
	}
	
	private void ProcessPlayerLoginOrRespawn(EntityPlayer pPlayer)
	{
		try
		{
			if (pPlayer.capabilities.isCreativeMode)
				return;
			
			if (!pPlayer.capabilities.disableDamage)
			{
				PlayerHelper.GiveProtection(pPlayer);
				UpdateOrInitLastCoords(pPlayer);
			}
		}
		catch (Exception e)
		{
			AngerMod.Logger.warn("PlayerSpawnProtection.ProcessPlayerLoginOrRespawn.Error", "An error occoured while processing ProcessPlayerLoginOrRespawn. Please report");
			AngerMod.Logger.DumpStack("PlayerSpawnProtection.ProcessPlayerLoginOrRespawn.Stack", e);
		}	
	}
	
	// Event section below this line \/
	// ---------------------------------------------------------------------------------
	/**
	 * Watch player for any interaction
	 * @param pEvent
	 */
	@SubscribeEvent
    public void onInteractEvent(PlayerInteractEvent pEvent)
    {
		try
		{
			if (pEvent.entityPlayer.worldObj.isRemote)
				return;
			
			if (pEvent.entityPlayer.capabilities.isCreativeMode)
				return;
			
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
			if (event.world.isRemote)
				return;
			
			if (!(event.entity instanceof EntityPlayer))
				return;
			
			EntityPlayer tEP = (EntityPlayer)event.entity;
			ProcessPlayerLoginOrRespawn(tEP);
		}
		catch (Exception e)
		{
			AngerMod.Logger.warn("PlayerSpawnProtection.onPlayerSpawn.Error", "An error occoured while processing onPlayerSpawn. Please report");
			AngerMod.Logger.DumpStack("PlayerSpawnProtection.onPlayerSpawn.Stack", e);
		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent pEvent)
	{
		if (pEvent.player.worldObj.isRemote)
			return;
		
		if (_mRnd.nextInt(40) == 0) // more than enough..
		{
			if (HasProtection(pEvent.player) && !pEvent.player.capabilities.isCreativeMode)
				CheckMovement(pEvent.player);
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
			
			if (event.entityPlayer.capabilities.isCreativeMode)
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
	
	/**
	 * Fired once the player changes dimensions
	 * @param pEvent
	 */
	@SubscribeEvent
	public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent pEvent)
	{
		try
		{
			if (pEvent.player.worldObj.isRemote)
				return;

			ProcessPlayerLoginOrRespawn(pEvent.player);
		}
		catch (Exception e)
		{
			AngerMod.Logger.warn("PlayerSpawnProtection.onDimensionChange.Error", "An error occoured while processing onDimensionChange. Please report");
			AngerMod.Logger.DumpStack("PlayerSpawnProtection.onDimensionChange.Stack", e);
		}
	}
}
