package com.github.namikon.angermod.auxiliary;

/**
 * Not 'Meth'-helper!
 * @author Namikon
 *
 */
public class MathHelper {
	/**
	 * Flip a coin with given chance to get lucky
	 * @param pChance
	 * @return True if you're lucky
	 */
	public static boolean FlipTheCoin(int pChance)
	{
		if (pChance < 1 || pChance > 100)
			return false;
		
		int tFlipResult = (int)Math.floor((Math.random() * 100) + 1);
		if (tFlipResult >= 1 && tFlipResult <= pChance)
			return true;
		else
			return false;
	}
	
	/**
	 * Flip a coin with 50% chance to get lucky
	 * @return
	 */
	public static boolean FlipTheCoin()
	{
		return FlipTheCoin(50);
	}
}
