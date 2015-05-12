package com.github.namikon.angermod.events;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import com.github.namikon.angermod.AngerMod;
import com.github.namikon.angermod.auxiliary.LogHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PlayerSpawnProtection {
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
			{
				tEP.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + "A magic bubble of protection appears..."));
				tEP.capabilities.disableDamage = true;
				tEP.sendPlayerAbilities();
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
		{
			event.entityPlayer.capabilities.disableDamage = false;
			event.entityPlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "Your magic bubble of protection fades..."));
			event.entityPlayer.sendPlayerAbilities();
		}
	}
}
