package com.github.namikon.angermod.auxiliary;

import java.util.Date;

/**
 * Generic functions about time
 * @author Namikon
 *
 */
public class TimeHelper {
	public static long GetCurrentTimestamp()
	{
		try
		{
			Date now = new Date();
			return (long)(now.getTime() / 1000);		
		} catch (Exception e)
		{
			LogHelper.error("TimeHelper.GetCurrentTimestamp.Exception", "GetCurrentTimestamp failed and is probably unreliable. You totally should report this...");
			LogHelper.DumpStack("TimeHelper.GetCurrentTimestamp.Exception.Stack", e);
			return 0;
		}
	}
}
