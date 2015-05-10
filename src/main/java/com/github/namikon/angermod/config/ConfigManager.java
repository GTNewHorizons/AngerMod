package com.github.namikon.angermod.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.LogManager;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import com.github.namikon.angermod.auxiliary.*;

import net.minecraftforge.common.config.Configuration;

import org.apache.commons.io.FileUtils;

/**
 * config class to read/setup config files and folders
 * @author Namikon
 */
public class ConfigManager {
	private File _mainconfigDir = null;
	private File _blocksconfigDir = null;
	
	private Configuration _mainConfig = null;
	FMLPreInitializationEvent _event = null;

	 public List<MinecraftBlock> BlacklistedBlocks = null;
	 public int EndermanAggrorange;
	 public int PigmenAggrorange;
	 
	 public boolean DoDebugMessages = false;
	 
	 private String[] _mDefaultBlacklistedEndBlocks = null;
	 private String[] _mDefaultBlacklistedNetherBlocks = null;
	
	 public ConfigManager(FMLPreInitializationEvent pEvent) {
		 _event = pEvent;
		 PreInit();
	 }
	 
	 
	 /**
	 * PreInit default values and lists
	 */
	private void PreInit()
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
	 }
	 
	 /**
	  * Load/init the config file
	 * @return true/false if the load/init was successful or not
	 */
	public boolean LoadConfig()
	 {
		 try
		 {
			 if (_mainConfig == null)
			 {
				 LogHelper.error("Y u no call InitConfigDirs first?");
				 return false;
			 }
				 
			 _mainConfig.load();
			 
			 String tCfgBlacklistedEndBlocks[] = _mainConfig.getStringList("EndBlocks", "Blacklist", _mDefaultBlacklistedEndBlocks, "Define all Blocks here where Enderman should become angry when you break them");
			 String tCfgBlacklistedNetherBlocks[] = _mainConfig.getStringList("NetherBlocks", "Blacklist", _mDefaultBlacklistedNetherBlocks, "Define all Blocks here where Pigmen should become angry when you break them");
			 
			 EndermanAggrorange = _mainConfig.getInt("Enderman", "Aggrorange", EndermanAggrorange, 2, 128, "The maximum range where Enderman shall become angry");
			 PigmenAggrorange = _mainConfig.getInt("Pigmen", "Aggrorange", PigmenAggrorange, 2, 128, "The maximum range where Pigmen shall become angry");
			 
			 DoDebugMessages = _mainConfig.getBoolean("DoDebugMessages", "Debug", false, "Enable debug output to fml-client-latest.log");
			 
			 LogHelper.setDebugOutput(DoDebugMessages);
			 
			 _mainConfig.save();
			 
			 ParseBlacklistedBlocks(tCfgBlacklistedEndBlocks, 1);
			 ParseBlacklistedBlocks(tCfgBlacklistedNetherBlocks, -1);
			 
			 return true;
		 }
		 catch (Exception e)
		 {
			 LogHelper.error("Unable to init config file");
			 LogHelper.DumpStack(e);
			 return false;
		 }
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

	 
	 /**
	 * Search for required config-directory / file and create them if they can't be found 
	 */
	public void InitConfigDirs()
	 {
		 LogHelper.info("Checking/creating config folders");
		 
		 File file = _event.getSuggestedConfigurationFile();
		 String cfgDir = file.getParent();
		 
		 _mainconfigDir = new File(cfgDir + "\\" + Reference.COLLECTIONNAME);
	 
	    if(!_mainconfigDir.exists()) {
	    	LogHelper.info("Config folder not found. Creating...");
	    	_mainconfigDir.mkdir();
	    }
	    
	    File tRealConfigFile = new File(_mainconfigDir + "\\" + Reference.MODID + ".cfg");
	    
	    _mainConfig = new Configuration(tRealConfigFile);
	 }
}