package com.github.namikon.angermod.auxiliary;

import java.util.UUID;

public class PermConfigHelper {
	/**
	 * Build valid Config-String, based on the players UUID to store persistent data
	 * @param pUserUID
	 * @param pCategory
	 * @return
	 */
	public static String BuildConfigValueName(UUID pUserUID, String pCategory)
	{
		return String.format("%s.%s", pUserUID.toString(), pCategory);
	}
}
