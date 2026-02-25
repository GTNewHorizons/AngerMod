package com.github.namikon.angermod.events;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import com.github.namikon.angermod.AngerMod;
import com.github.namikon.angermod.config.SpawnProtectionConfig;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

/**
 * Class for handling the spawn protection. Give/set/watch events,... probably better to split that up later on...
 *
 * @author Namikon
 *
 */
public final class PlayerSpawnProtection {

    private static class ProtectionData {

        public final double x;
        public final double y;
        public final double z;
        public final long creationTime;

        public ProtectionData(EntityPlayer player) {
            x = player.posX;
            y = player.posY;
            z = player.posZ;
            creationTime = player.worldObj.getTotalWorldTime();
        }

        public boolean isExpired(EntityPlayer player) {
            double maxDistSq = SpawnProtectionConfig.moveTolerance * SpawnProtectionConfig.moveTolerance;
            int maxDuration = SpawnProtectionConfig.maxDuration * 20;

            if (player.getDistanceSq(x, y, z) > maxDistSq) {
                AngerMod.LOGGER.debug("Protection voided because of distance");
                return true;
            }

            if ((creationTime + maxDuration) < player.worldObj.getTotalWorldTime()) {
                AngerMod.LOGGER.debug("Protection voided because of timeout");
                return true;
            }

            return false;
        }
    }

    private final HashMap<UUID, ProtectionData> protectedPlayers = new HashMap<>();

    /**
     * Give the player a bubble of protection or reset the timer on their current bubble if it would be safe to do so.
     *
     * @param player The player to protect
     * @return Whether the bubble was safely applied to the player.
     */
    public boolean protectPlayer(EntityPlayer player) {
        if (isInvalidPlayer(player)) return false;

        player.capabilities.disableDamage = true;
        player.sendPlayerAbilities();
        player.addChatMessage(
                new ChatComponentTranslation("yamcore.char.protection.give")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)));

        protectedPlayers.put(player.getUniqueID(), new ProtectionData(player));
        return true;
    }

    /**
     * Remove the bubble of protection from a player, if it would be safe to do so. Will remove the player's bubble data
     * regardless of safety
     *
     * @param player The player to unprotect
     * @return Whether the player's bubble was successfully removed.
     */
    public boolean unprotectPlayer(EntityPlayer player) {
        if (protectedPlayers.remove(player.getUniqueID()) == null) return false;
        if (isInvalidPlayer(player)) return false;

        player.capabilities.disableDamage = false;
        player.sendPlayerAbilities();
        player.addChatMessage(
                new ChatComponentTranslation("yamcore.char.protection.remove")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_PURPLE)));
        return true;
    }

    /***
     * Check if we can safely alter the player's invulnerability setting.
     *
     * @param player The player to check
     * @return True if it would be unsafe to alter their `capabilities.disableDamage`
     */
    public static boolean isInvalidPlayer(EntityPlayer player) {
        return player.capabilities.isCreativeMode || isSpectatorPlayer(player) || hasWhitelistedItems(player);
    }

    /**
     * Check if player is currently in spectator mode.
     *
     * In 1.7.10, spectator can be backported by other mods and is represented as a custom GameType id 3.
     * 
     * @return true if the player is in spectator mode, false otherwise.
     */
    private static boolean isSpectatorPlayer(EntityPlayer player) {
        return player instanceof EntityPlayerMP playerMP && playerMP.theItemInWorldManager.getGameType() != null
                && playerMP.theItemInWorldManager.getGameType().getID() == 3;
    }

    /**
     * Check if player has some special items in his/her inventory where AngerMod should just do nothing, since they're
     * doing stuff with "capabilities.disableDamage"
     *
     * @param player The player to check for baubles
     * @return True if the player has baubles that would affect their `capabilities.disableDamage`.
     */
    private static boolean hasWhitelistedItems(EntityPlayer player) {
        var baubles = BaublesApi.getBaubles(player);
        if (baubles == null) return false;

        for (int i = 0; i < baubles.getSizeInventory(); i++) {
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack == null) continue;

            if (SpawnProtectionConfig.WhitelistedProtectionBaubles.contains(stack.getItem(), stack.getItemDamage())) {
                return true;
            }
        }

        return false;
    }

    // Event section below this line \/
    // ---------------------------------------------------------------------------------
    @SubscribeEvent
    public void onInteractEvent(PlayerInteractEvent event) {
        if (event.world.isRemote) return;
        // Ignore right click on air, and left click in order to break your grave. Everything else removes protection.
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) return;

        unprotectPlayer(event.entityPlayer);
    }

    @SubscribeEvent
    public void onPlayerSpawn(EntityJoinWorldEvent event) {
        if (event.world.isRemote) return;
        if (!(event.entity instanceof EntityPlayer player)) return;

        protectPlayer(player);
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        final EntityPlayer player = event.player;
        if (player.worldObj.isRemote) return;
        if (isSpectatorPlayer(player)) return;
        if (!player.capabilities.disableDamage) return;
        if (protectedPlayers.isEmpty()) return;

        ProtectionData protection = protectedPlayers.get(player.getUniqueID());
        if (protection != null && protection.isExpired(player)) {
            unprotectPlayer(player);
        }
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.entityPlayer.worldObj.isRemote) return;

        unprotectPlayer(event.entityPlayer);
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.player.worldObj.isRemote) return;

        protectPlayer(event.player);
    }
}
