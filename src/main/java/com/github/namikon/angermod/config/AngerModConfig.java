package com.github.namikon.angermod.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.namikon.angermod.auxiliary.LogHelper;
import com.github.namikon.angermod.auxiliary.MinecraftBlock;

/**
 * Specific configuration for >this< mod
 * @author Namikon
 *
 */
public class AngerModConfig extends ConfigManager {
	 public List<MinecraftBlock> BlacklistedBlocks = null;
	 public int EndermanAggrorange;
	 public int PigmenAggrorange;
	 public int SleepingThreshold;
	 
	 public boolean NewPlayerProtection;
	 public boolean MakeMobsAngryOnBlockBreak;	 
	 
	 private String[] _mDefaultBlacklistedEndBlocks = null;
	 private String[] _mDefaultBlacklistedNetherBlocks = null;

	 private String tCfgBlacklistedEndBlocks[] = null;
	 private String tCfgBlacklistedNetherBlocks[] = null;

	 //private List<String> _mUnprotectedPlayers = null;
	 
	
	public AngerModConfig(File pConfigBaseDirectoryt) {
		super(pConfigBaseDirectoryt);
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
		 SleepingThreshold = 20;
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
		 
		 EndermanAggrorange = _mainConfig.getInt("Enderman", "Limits", EndermanAggrorange, 2, 128, "The maximum range where Enderman shall become angry");
		 PigmenAggrorange = _mainConfig.getInt("Pigmen", "Limits", PigmenAggrorange, 2, 128, "The maximum range where Pigmen shall become angry");
		 SleepingThreshold = _mainConfig.getInt("MaxSleepTimes", "Limits", SleepingThreshold, 1, Integer.MAX_VALUE, "How often can a player sleep until his protection bubble will fade on every world-interaction (except breaking blocks with his bare hands)");
		 
		 NewPlayerProtection = _mainConfig.getBoolean("ProtectionEnabled", "ModuleControl", true, "Define if new players / respawned players shall be ignored from monsters until they attack something");
		 MakeMobsAngryOnBlockBreak = _mainConfig.getBoolean("BlockBreakEnabled", "ModuleControl", true, "Enable/disable block-breaking-makes-mobs-angry module");
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
