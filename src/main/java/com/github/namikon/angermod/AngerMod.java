package com.github.namikon.angermod;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.github.namikon.angermod.auxiliary.*;
import com.github.namikon.angermod.command.AngerProtectionCommand;
import com.github.namikon.angermod.config.AngerModConfig;
import com.github.namikon.angermod.events.PlayerSpawnProtection;
import com.github.namikon.angermod.events.BlockBreakEvent;
import com.github.namikon.angermod.iface.IPersistedDataBase;
import com.github.namikon.angermod.persisteddata.PersistedDataBase;


/**
 * @author Namikon
 *
 */
@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class AngerMod {
	private static AngerModConfig _cfgManager = null;
	public static PlayerSpawnProtection SpawnProtectionModule = null;
	public static BlockBreakEvent BlockTakeAggroModule = null;
	
	public static boolean ModInitSuccessful = true;
	private static IPersistedDataBase _mPersistedConfig = null;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		try 
		{
			_cfgManager = new AngerModConfig(event.getModConfigurationDirectory());
			if (!_cfgManager.LoadConfig())
				ModInitSuccessful = false;
			
			_mPersistedConfig = new PersistedDataBase(event.getModConfigurationDirectory(), "AngerStorage.ser");
			if (!_mPersistedConfig.Load())
				ModInitSuccessful = false;
			
		}
	    catch (Exception e)
	    {
	    	LogHelper.error("Yeeks, I can't load my configuration. What did you do??");
	    	LogHelper.DumpStack(e);
	    }
	}
	
	@EventHandler
	public void Init(FMLInitializationEvent event)
	{
		if(ModInitSuccessful)
		{
			if (_cfgManager.MakeMobsAngryOnBlockBreak)
			{
				LogHelper.info("BlockBreak module is enabled. Some mobs will get very angry...");
				BlockTakeAggroModule = new BlockBreakEvent(_cfgManager);
				MinecraftForge.EVENT_BUS.register(BlockTakeAggroModule);
			}

			
			if (_cfgManager.NewPlayerProtection)
			{
				LogHelper.info("Spawn-Protection is enabled. Players will be protected until they attack");
				SpawnProtectionModule = new PlayerSpawnProtection(_cfgManager, _mPersistedConfig);
				MinecraftForge.EVENT_BUS.register(SpawnProtectionModule);
			}
		}
		else
			LogHelper.warn(Reference.MODID + " will NOT do anything as there where errors due the preInit event. Check the logfile!");
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		
	}
	
	
	/** Do some stuff once the server starts
	 * @param pEvent
	 */
	@EventHandler
	public void serverLoad(FMLServerStartingEvent pEvent)
	{
		if (_cfgManager.NewPlayerProtection)
		{
			pEvent.registerServerCommand(new AngerProtectionCommand());
		}
	}
}
