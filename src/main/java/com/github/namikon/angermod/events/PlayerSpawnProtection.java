package com.github.namikon.angermod.events;

import java.nio.file.AccessMode;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

import com.github.namikon.angermod.AngerMod;
import com.github.namikon.angermod.auxiliary.LogHelper;
import com.github.namikon.angermod.auxiliary.PlayerChatHelper;
import com.github.namikon.angermod.config.AngerModConfig;
import com.github.namikon.angermod.iface.IPersistedDataBase;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	
	private String BuildConfigValueName(UUID pUserUID, String pCategory)
	{
		return String.format("%s.%s", pUserUID.toString(), pCategory);
	}
	
	private int getPlayerBedTimes(UUID pUserUID)
	{
		return _mPersistedConfig.getValueAsInt(BuildConfigValueName(pUserUID, BED_TIMES), 0);
	}
	
	private boolean HasNoobProtection(EntityPlayer pEntityPlayer)
	{
		if (getPlayerBedTimes(pEntityPlayer.getUniqueID()) < _mCfgManager.SleepingThreshold)
			return true;
		else
			return false;
	}
	
	private int increasePlayerBedTime(UUID pUserUID)
	{
		int oldVal = getPlayerBedTimes(pUserUID);
		oldVal++;
		
		_mPersistedConfig.setValue(BuildConfigValueName(pUserUID, BED_TIMES), oldVal);
		return oldVal;
	}
	
	/**
	 * Protect the player from any damage or becoming a target to hostile mobs as they spawn
	 * @param event
	 */
	@SubscribeEvent
	public void onPlayerSpawn(EntityJoinWorldEvent event)
	{
		if(event.world.isRemote)
			return;

		if (event.entity instanceof EntityPlayerMP)
		{
			EntityPlayerMP tEP = (EntityPlayerMP)event.entity;
			if (!tEP.capabilities.disableDamage)
				GiveProtection(tEP);
		}
	}
	
	/**
	 * Detect if player is sleeping in his bed
	 * @param pEvent
	 */
	@SubscribeEvent
	public void onSleepInBedEvent(PlayerSleepInBedEvent pEvent)
	{
		EntityPlayer tEP = pEvent.entityPlayer;
		
		// No player, no bananas
		if (tEP == null)
		{
			LogHelper.error("EntityPlayer is null, but sleep event was raised. This should not happen!");
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
	
	/**
	 * Remove player-protection as soon as the player starts to hit an entity
	 * @param event
	 */
	@SubscribeEvent
	public void onAttackEntity(AttackEntityEvent event)
	{
		if(event.entityPlayer.worldObj.isRemote)
			return;
		
		if (event.entityPlayer.capabilities.disableDamage)
			RemoveProtection(event.entityPlayer);
	}

	/**
	 * Remove protection from the player
	 * @param pEntityPlayer
	 */
	public void RemoveProtection(EntityPlayer pEntityPlayer)
	{
		pEntityPlayer.capabilities.disableDamage = false;
		pEntityPlayer.sendPlayerAbilities();
		PlayerChatHelper.SendNotifyWarning(pEntityPlayer, "Your magic bubble of protection fades...");
	}
	
	/**
	 * Actually GIVE protection to the player
	 * @param pEntityPlayer
	 */
	public void GiveProtection(EntityPlayer pEntityPlayer)
	{
		pEntityPlayer.capabilities.disableDamage = true;
		pEntityPlayer.sendPlayerAbilities();
		PlayerChatHelper.SendNotifyPositive(pEntityPlayer, "A magic bubble of protection appears...");
	}
	
	/**
	 * Watch player for any interaction
	 * @param pEvent
	 */
	@SubscribeEvent
    public void onInteractEvent(PlayerInteractEvent pEvent)
    {
		// Not server, do nothing
		if(pEvent.world.isRemote)
			return;
		
		EntityPlayer tEP = pEvent.entityPlayer;
		// No player, no bananas
		if (tEP == null)
		{
			LogHelper.error("EntityPlayer is null, but PlayerInteractEvent event was raised. This should not happen!");
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
			RemoveProtection(tEP);
	}
}
