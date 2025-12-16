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

import com.github.namikon.angermod.config.FriendlyAnimalRevengeConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class EatCookedAnimalsEvent {

    @SubscribeEvent
    public void onPlayerFinishedUsingItem(PlayerUseItemEvent.Finish event) {
        EntityPlayer player = event.entityPlayer;
        if (player == null) return;

        String itemName = event.item.getUnlocalizedName();

        tryTriggerAnimals(FriendlyAnimalRevengeConfig.pigFoodTrigger, itemName, player, EntityPig.class);
        tryTriggerAnimals(FriendlyAnimalRevengeConfig.cowFoodTrigger, itemName, player, EntityCow.class);
        tryTriggerAnimals(FriendlyAnimalRevengeConfig.chickenFoodTrigger, itemName, player, EntityChicken.class);
        tryTriggerAnimals(FriendlyAnimalRevengeConfig.sheepFoodTrigger, itemName, player, EntitySheep.class);
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
        int range = FriendlyAnimalRevengeConfig.revengeRadius;
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
