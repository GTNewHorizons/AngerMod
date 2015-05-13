package com.github.namikon.angermod.auxiliary;

/**
 * @author Namikon
 *
 */
public class MathHelper {
	public static boolean FlipTheCoin()
	{
		int tFlipResult = (int)Math.floor((Math.random() * 100) + 1);
		if (tFlipResult >= 1 || tFlipResult < 50)
			return true;
		else
			return false;
	}
}
