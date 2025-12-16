package com.github.namikon.angermod.events;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
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
public class PlayerSpawnProtection {

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
                AngerMod.Logger.debug("Protection voided because of distance");
                return true;
            }

            if ((creationTime + maxDuration) < player.worldObj.getTotalWorldTime()) {
                AngerMod.Logger.debug("Protection voided because of timeout");
                return true;
            }

            return false;
        }
    }

    private final HashMap<UUID, ProtectionData> protectedPlayers = new HashMap<>();

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

    public static boolean isInvalidPlayer(EntityPlayer player) {
        return player.capabilities.isCreativeMode || hasWhitelistedItems(player);
    }

    /**
     * Check if player has some special items in his/her inventory where AngerMod should just do nothing, since they're
     * doing stuff with "capabilities.disableDamage"
     *
     * This is a temp solution for now; Later on, it might be better (if it has performance issues), to create a
     * buffered UID list for that so we don't query the players inventory all the time
     *
     * @return
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
