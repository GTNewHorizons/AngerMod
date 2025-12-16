package com.github.namikon.angermod.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

import com.github.namikon.angermod.AngerMod;
import com.github.namikon.angermod.config.AngerModConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EatCookedAnimalsEvent {

    @SubscribeEvent
    public void onPlayerUsesItem(PlayerUseItemEvent.Start event) {
        EntityPlayer player = event.entityPlayer;
        if (player == null) return;

        String itemName = event.item.getUnlocalizedName();
        AngerMod.Logger.debug(String.format("Using item %s", itemName));

        tryTriggerAnimals(AngerModConfig.PigFoodTrigger, itemName, player, EntityPig.class);
        tryTriggerAnimals(AngerModConfig.CowFoodTrigger, itemName, player, EntityCow.class);
        tryTriggerAnimals(AngerModConfig.ChickenFoodTrigger, itemName, player, EntityChicken.class);
        tryTriggerAnimals(AngerModConfig.SheepFoodTrigger, itemName, player, EntitySheep.class);
    }

    private static void tryTriggerAnimals(String[] triggers, String item, EntityPlayer player,
            Class<? extends Entity> filter) {
        for (String s : triggers) {
            if (item.contains(s)) {
                damageEntitiesInRange(player, filter);
                return;
            }
        }
    }

    private static void damageEntitiesInRange(EntityPlayer player, Class<? extends Entity> filter) {
        int range = AngerModConfig.FriendlyMobRevengeRadius;
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
                player.posX - range,
                player.posY - range,
                player.posZ - range,
                player.posX + range + 1,
                player.posY + range + 1,
                player.posZ + range + 1);

        for (Entity entity : player.worldObj.getEntitiesWithinAABB(filter, box)) {
            entity.attackEntityFrom(DamageSource.causePlayerDamage(player), 0f);
        }
    }
}
