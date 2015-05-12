package com.github.namikon.angermod.command;
import java.util.ArrayList;
import java.util.List;

import com.github.namikon.angermod.AngerMod;
import com.github.namikon.angermod.auxiliary.LogHelper;
import com.github.namikon.angermod.auxiliary.WorldHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;


/**
 * Angerprotection command. Enable ops to give/remove protection
 * @author Namikon
 *
 */
public class AngerProtectionCommand implements ICommand {
	private List aliases;
	public AngerProtectionCommand()
	{
		this.aliases = new ArrayList();
		this.aliases.add("angerprotection");
	}
	
	@Override
	public String getCommandName()
	{
		return "angerprotection";
	}
	
	@Override
	public String getCommandUsage(ICommandSender pCommandSender)
	{
		return "angerprotection <Player> give/remove";
	}
	
	  @Override
	  public List getCommandAliases()
	  {
	    return this.aliases;
	  }
	  
	  @Override
	  public void processCommand(ICommandSender pCommandSender, String[] pArgs)
	  {
		  if (AngerMod.SpawnProtectionModule == null)
		  {
			  pCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Spawn protection is not enabled"));
			  return;
		  }
		  
		  if (pArgs.length < 2)
		  {
			  pCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid arguments"));
			  return;
		  }
		  
		  String tTargetPlayer = pArgs[0];
		  String tFunction = pArgs[1];
		  EntityPlayer tEP = WorldHelper.FindPlayerByName(tTargetPlayer);
		  
		  
		  if (tEP == null)
		  {
			  pCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Player " + tTargetPlayer + " not found"));
			  return;
		  }
		  
		  if (tFunction.equalsIgnoreCase("give"))
		  {
			  AngerMod.SpawnProtectionModule.GiveProtection(tEP);
			  pCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Protection given"));
		  }
		  else if (tFunction.equalsIgnoreCase("remove"))
		  {
			  AngerMod.SpawnProtectionModule.RemoveProtection(tEP);
			  pCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Protection removed"));
		  }
		  else
			  pCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Must specify either give or remove"));
	  }
	  
	  @Override
	  public boolean canCommandSenderUseCommand(ICommandSender pCommandSender)
	  {
		  if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && !FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer())
			  return true;
		  
		  if(pCommandSender instanceof EntityPlayerMP)
		  {
			  EntityPlayerMP tEP = (EntityPlayerMP)pCommandSender;
			  return MinecraftServer.getServer().getConfigurationManager().func_152596_g(tEP.getGameProfile());
		  }
		  return false;
	  }

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_,
			String[] p_71516_2_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		// TODO Auto-generated method stub
		return false;
	}
}
