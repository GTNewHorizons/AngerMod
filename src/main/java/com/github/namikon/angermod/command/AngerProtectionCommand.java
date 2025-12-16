package com.github.namikon.angermod.command;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.PlayerSelector;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

import com.github.namikon.angermod.AngerMod;
import com.github.namikon.angermod.config.SpawnProtectionConfig;

/**
 * Angerprotection command. Enable ops to give/remove protection
 *
 * @author Namikon
 *
 */
public class AngerProtectionCommand extends CommandBase {

    private static final List<String> aliases = Collections.singletonList("angerprotection");
    private static final String[] actions = new String[] { "give", "remove" };

    @Override
    public String getCommandName() {
        return "angerprotection";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "angermod.commands.angerprotection.usage";
    }

    @Override
    public List<String> getCommandAliases() {
        return aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!SpawnProtectionConfig.enabled) {
            throw new CommandException("angermod.commands.angerprotection.disabled");
        }

        if (args.length < 2) {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        String selector = args[0];
        String action = args[1];

        EntityPlayerMP[] players = PlayerSelector.matchPlayers(sender, selector);
        if (players == null) {
            EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(selector);

            if (player == null) {
                throw new PlayerNotFoundException();
            } else {
                players = new EntityPlayerMP[] { player };
            }
        }

        if (action.equalsIgnoreCase("give")) {
            int count = 0;
            for (var player : players) {
                if (AngerMod.spawnProtectionEventHandler.protectPlayer(player)) count++;
            }

            sender.addChatMessage(new ChatComponentTranslation("angermod.commands.angerprotection.given", count));
        } else if (action.equalsIgnoreCase("remove")) {
            int count = 0;
            for (var player : players) {
                if (AngerMod.spawnProtectionEventHandler.unprotectPlayer(player)) count++;
            }

            sender.addChatMessage(new ChatComponentTranslation("angermod.commands.angerprotection.removed", count));
        } else {
            throw new CommandException("angermod.commands.angerprotection.wrong_action");
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(
                    args,
                    MinecraftServer.getServer().getConfigurationManager().getAllUsernames());
        } else if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, actions);
        } else {
            return null;
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}
