package com.github.namikon.angermod.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.LogManager;
import com.github.namikon.angermod.auxiliary.*;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.io.FileUtils;

/**
 * config class to read/setup config files and folders
 * @author Namikon
 */
public abstract class ConfigManager {
	private File _mainconfigDir = null;
	private File _blocksconfigDir = null;
	
	protected Configuration _mainConfig = null;

	protected File _mConfigBaseDirectory;
	 public boolean DoDebugMessages = false;

	 protected abstract void PreInit();
	 protected abstract void Init();
	 protected abstract void PostInit();
	 
	 
	 public ConfigManager(File pConfigBaseDirectory)
	 {
		 _mConfigBaseDirectory = pConfigBaseDirectory;
	 }
	 
	 /**
	  * Load/init the config file
	 * @return true/false if the load/init was successful or not
	 */
	public boolean LoadConfig()
	 {
		 try
		 {
			 InitConfigDirs();
			 if (_mainConfig == null)
				 return false;
			 
			 PreInit();
			 _mainConfig.load();
			 Init();
			 
			 DoDebugMessages = _mainConfig.getBoolean("DoDebugMessages", "Debug", false, "Enable debug output to fml-client-latest.log");
			 LogHelper.setDebugOutput(DoDebugMessages);
			 _mainConfig.save();
			 
			 PostInit();
			 

			 
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
	 * Search for required config-directory / file and create them if they can't be found 
	 */
	private void InitConfigDirs()
	 {
		 LogHelper.info("Checking/creating config folders");
		 
		 _mainconfigDir = new File(String.format("%s%s%s", _mConfigBaseDirectory, File.separator, Reference.COLLECTIONNAME));
	 
	    if(!_mainconfigDir.exists()) {
	    	LogHelper.info("Config folder not found. Creating...");
	    	_mainconfigDir.mkdir();
	    }
	    
	    File tRealConfigFile = new File(String.format("%s%s%s%s", _mainconfigDir, File.separator, Reference.MODID, ".cfg"));
    
	    _mainConfig = new Configuration(tRealConfigFile);
	 }
}