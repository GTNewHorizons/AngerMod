package com.github.namikon.angermod.auxiliary;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

/**
 * Method to easily send chat-messages to EntityPlayer
 * @author Namikon
 *
 */
public class PlayerChatHelper {
	public static void SendInfo(EntityPlayer pPlayer, String pMessage)
	{
		pPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + pMessage));
	}
	
	public static void SendError(EntityPlayer pPlayer, String pMessage)
	{
		pPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + pMessage));
	}
	
	public static void SendWarn(EntityPlayer pPlayer, String pMessage)
	{
		pPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + pMessage));
	}	
	
	public static void SendNotifyPositive(EntityPlayer pPlayer, String pMessage)
	{
		pPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + pMessage));
	}
	
	public static void SendNotifyNormal(EntityPlayer pPlayer, String pMessage)
	{
		pPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + pMessage));
	}
	
	public static void SendNotifyWarning(EntityPlayer pPlayer, String pMessage)
	{
		pPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + pMessage));
	}
	
}
