package com.github.namikon.angermod.auxiliary;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

/**
 * Generic World-Helper
 * @author Namikon
 *
 */
public class WorldHelper {
	/**
	 * Find EntityPlayer instance for a given Minecraft name
	 * @param pName
	 * @return
	 */
	public static EntityPlayer FindPlayerByName(String pName) {
		EntityPlayer tEP = null;
		try
		{
			for (World world : DimensionManager.getWorlds())
			{
				tEP = world.getPlayerEntityByName(pName);
				if (tEP != null)
					break;
			}
			  
		}
		catch (Exception e)
		{
			LogHelper.DumpStack(e);
		}
		return tEP;
	}
}
