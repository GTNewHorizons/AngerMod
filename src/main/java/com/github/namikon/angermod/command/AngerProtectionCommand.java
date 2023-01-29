package com.github.namikon.angermod.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.github.namikon.angermod.AngerMod;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import eu.usrv.yamcore.auxiliary.PlayerChatHelper;
import eu.usrv.yamcore.auxiliary.PlayerHelper;
import eu.usrv.yamcore.auxiliary.WorldHelper;

/**
 * Angerprotection command. Enable ops to give/remove protection
 * 
 * @author Namikon
 *
 */
public class AngerProtectionCommand implements ICommand {

    private List aliases;

    public AngerProtectionCommand() {
        this.aliases = new ArrayList();
        this.aliases.add("angerprotection");
    }

    @Override
    public String getCommandName() {
        return "angerprotection";
    }

    @Override
    public String getCommandUsage(ICommandSender pCommandSender) {
        return "angerprotection <Player> give/remove";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender pCommandSender, String[] pArgs) {
        if (AngerMod.SpawnProtectionModule == null) {
            PlayerChatHelper.SendError(pCommandSender, "Spawn protection is not enabled");
            return;
        }

        if (pArgs.length < 2) {
            PlayerChatHelper.SendError(pCommandSender, "Invalid arguments");
            return;
        }

        String tTargetPlayer = pArgs[0];
        String tFunction = pArgs[1];
        EntityPlayer tEP = WorldHelper.FindPlayerByName(tTargetPlayer);

        if (tEP == null) {
            PlayerChatHelper.SendError(pCommandSender, String.format("Player %s not found", tTargetPlayer));
            return;
        }

        if (tFunction.equalsIgnoreCase("give")) {
            PlayerHelper.GiveProtection(tEP);
            AngerMod.SpawnProtectionModule.UpdateOrInitLastCoords(tEP); // Update protection tracker so it doesn't run
                                                                        // out instantly
            PlayerChatHelper.SendInfo(pCommandSender, "Protection given");
        } else if (tFunction.equalsIgnoreCase("remove")) {
            PlayerHelper.RemoveProtection(tEP);
            PlayerChatHelper.SendInfo(pCommandSender, "Protection removed");
        } else PlayerChatHelper.SendError(pCommandSender, "Must specify either give or remove");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender pCommandSender) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER
                && !FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer())
            return true;

        if (pCommandSender instanceof EntityPlayerMP) {
            EntityPlayerMP tEP = (EntityPlayerMP) pCommandSender;
            return MinecraftServer.getServer().getConfigurationManager().func_152596_g(tEP.getGameProfile());
        }
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }
}
