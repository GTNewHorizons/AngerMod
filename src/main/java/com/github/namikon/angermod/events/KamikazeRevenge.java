package com.github.namikon.angermod.events;

import java.util.UUID;

import com.github.namikon.angermod.auxiliary.MathHelper;
import com.github.namikon.angermod.auxiliary.PlayerChatHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;


/**
 * Just because reasons
 * @author Namikon
 *
 */
public class KamikazeRevenge {
	@SubscribeEvent
	public void onEntityDied(LivingDeathEvent pEvent)
	{
		if(!(pEvent.source.getSourceOfDamage() instanceof EntityPlayer))
			return;
		else
		{
			EntityPlayer tEP = (EntityPlayer) pEvent.source.getSourceOfDamage();
			World tW = tEP.worldObj;
			
			if (!MathHelper.FlipTheCoin())
				return; // Head, you win
			else
			{
				boolean flag = tW.getGameRules().getGameRuleBooleanValue("mobGriefing");
				tW.createExplosion((Entity) null, pEvent.entityLiving.posX, pEvent.entityLiving.posY, pEvent.entityLiving.posZ, 1F, flag);
			}
		}
	}
}
