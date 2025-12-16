package com.github.namikon.angermod.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.github.namikon.angermod.config.KamikazeConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Just because reasons
 *
 * @author Namikon
 *
 */
public class KamikazeRevenge {

    @SubscribeEvent
    public void onEntityDied(LivingDeathEvent event) {
        if (!(event.source.getSourceOfDamage() instanceof EntityPlayer player)) return;
        if (player instanceof FakePlayer) return;

        ItemStack heldItem = player.inventory.getCurrentItem();
        if (heldItem != null) {
            String itemName = heldItem.getUnlocalizedName().toLowerCase();
            for (String s : KamikazeConfig.butcherItems) {
                if (itemName.contains(s.toLowerCase())) return; // Player used defined butcher-item to
                // slay animal. Trigger no explosion
            }
        }

        World world = player.worldObj;

        if (world.rand.nextInt(100) >= KamikazeConfig.chance) return;

        // obey mobGriefing gamerule
        boolean mobGriefing = world.getGameRules().getGameRuleBooleanValue("mobGriefing");
        boolean flag = (mobGriefing && KamikazeConfig.doTerrainDamage);

        world.createExplosion(
                event.entityLiving,
                event.entityLiving.posX,
                event.entityLiving.posY,
                event.entityLiving.posZ,
                1.5F,
                flag);
    }
}
