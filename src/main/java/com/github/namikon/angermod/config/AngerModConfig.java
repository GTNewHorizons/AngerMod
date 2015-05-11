package com.github.namikon.angermod.config;

import java.util.ArrayList;
import java.util.List;

import com.github.namikon.angermod.auxiliary.LogHelper;
import com.github.namikon.angermod.auxiliary.MinecraftBlock;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class AngerModConfig extends ConfigManager {
	 public List<MinecraftBlock> BlacklistedBlocks = null;
	 public int EndermanAggrorange;
	 public int PigmenAggrorange;
	 public boolean NewPlayerProtection;
	 
	 private String[] _mDefaultBlacklistedEndBlocks = null;
	 private String[] _mDefaultBlacklistedNetherBlocks = null;

	 private String tCfgBlacklistedEndBlocks[] = null;
	 private String tCfgBlacklistedNetherBlocks[] = null;

	 //private List<String> _mUnprotectedPlayers = null;
	 
	
	public AngerModConfig(FMLPreInitializationEvent pEvent) {
		super(pEvent);
	}

	
	 
	 /**
	 * PreInit default values and lists
	 */
	 @Override
	protected void PreInit()
	 {
		BlacklistedBlocks = new ArrayList<MinecraftBlock>();
		
		 _mDefaultBlacklistedEndBlocks = new String[] { 
				 "minecraft:end_stone",
				 "gregtech:gt.blockores"
		 };
		 
		 _mDefaultBlacklistedNetherBlocks = new String[] {
				 "gregtech:gt.blockores",
		 };
		 
		 EndermanAggrorange = 16;
		 PigmenAggrorange = 16;
		 //_mUnprotectedPlayers = new ArrayList<String>();
	 }

	@Override
	protected void PostInit() {
		 ParseBlacklistedBlocks(tCfgBlacklistedEndBlocks, 1);
		 ParseBlacklistedBlocks(tCfgBlacklistedNetherBlocks, -1);
	}

	
	@Override
	protected void Init() {
		 tCfgBlacklistedEndBlocks = _mainConfig.getStringList("EndBlocks", "Blacklist", _mDefaultBlacklistedEndBlocks, "Define all Blocks here where Enderman should become angry when you break them");
		 tCfgBlacklistedNetherBlocks = _mainConfig.getStringList("NetherBlocks", "Blacklist", _mDefaultBlacklistedNetherBlocks, "Define all Blocks here where Pigmen should become angry when you break them");
		 
		 EndermanAggrorange = _mainConfig.getInt("Enderman", "Aggrorange", EndermanAggrorange, 2, 128, "The maximum range where Enderman shall become angry");
		 PigmenAggrorange = _mainConfig.getInt("Pigmen", "Aggrorange", PigmenAggrorange, 2, 128, "The maximum range where Pigmen shall become angry");
		 NewPlayerProtection = _mainConfig.getBoolean("ProtectionEnabled", "SpawnProtection", true, "Define if new players / respawned players shall be ignored from monsters until they attack something");
		 
/*		 String[] tUnprotectedPlayers = _mainConfig.getStringList("ListOfNames", "ProtectionBlacklist", new String[] {}, "List of all Names who shall not get the protection bubble. This list will automaticly fill with new names as soon as the player reaches a certain limit");
		 for (String player : tUnprotectedPlayers)
			 _mUnprotectedPlayers.add(player);
*/
	}

	
	 /**
	  * Go ahead and parse the given list of strings to actual instances of MinecraftBlock classes with bound dimension ID
	 * @param pBlockNames
	 * @param pDimension
	 */
	private void ParseBlacklistedBlocks(String pBlockNames[], int pDimension)
	 {
		 try
		 {
			 for (String tBlockName : pBlockNames)
			 {
				 try
				 {
					 MinecraftBlock tBlock = new MinecraftBlock(tBlockName, pDimension);
					 LogHelper.info("New block added for Dimension " + pDimension + " BlockID: " + tBlockName);
					 BlacklistedBlocks.add(tBlock); // TODO: Make sure we only add each block once... 
				 }
				 catch (Exception e)
				 {
					 LogHelper.warn("NetherBlock Definition " + tBlockName + " will be ignored. Check your spelling [ModID]:[BlockName] or [ModID]:[BlockName]:[BlockMeta]");
					 LogHelper.DumpStack(e);
				 }
			 }
		 }
		 catch (Exception e)
		 {
			 LogHelper.error("Error while parsing Blacklist for Nether blocks");
			 LogHelper.DumpStack(e);
		 }
	 }
}
