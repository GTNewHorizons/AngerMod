package com.github.namikon.angermod;

import net.minecraftforge.common.MinecraftForge;

import com.github.namikon.angermod.auxiliary.Reference;
import com.github.namikon.angermod.command.AngerProtectionCommand;
import com.github.namikon.angermod.config.AngerModConfig;
import com.github.namikon.angermod.events.BlockBreakEvent;
import com.github.namikon.angermod.events.EatCookedAnimalsEvent;
import com.github.namikon.angermod.events.KamikazeRevenge;
import com.github.namikon.angermod.events.PlayerSpawnProtection;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import eu.usrv.yamcore.auxiliary.LogHelper;

/**
 * @author Namikon
 *
 */
@Mod(
        modid = Reference.MODID,
        name = Reference.NAME,
        version = Reference.VERSION,
        dependencies = "required-after:Forge@[10.13.4.1614,);required-after:Baubles@[1.0.1.10,);"
                + "required-after:YAMCore@[0.3,);")
public class AngerMod {

    public static AngerModConfig _cfgManager = null;
    public static LogHelper Logger = new LogHelper("AngerMod");

    public static PlayerSpawnProtection SpawnProtectionModule = null;
    public static BlockBreakEvent BlockTakeAggroModule = null;
    public static EatCookedAnimalsEvent EatCookedAnimalsModule = null;
    public static KamikazeRevenge KamikazeRevengeModule = null;

    public static boolean ModInitSuccessful = true;
    // private static IPersistedDataBase _mPersistedConfig = null;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        try {
            _cfgManager = new AngerModConfig(
                    event.getModConfigurationDirectory(),
                    Reference.COLLECTIONNAME,
                    Reference.MODID);
            if (!_cfgManager.LoadConfig()) ModInitSuccessful = false;
            /*
             * _mPersistedConfig = new PersistedDataBase(event.getModConfigurationDirectory(), "AngerStorage.ser",
             * Reference.COLLECTIONNAME); if (!_mPersistedConfig.Load()) ModInitSuccessful = false;
             */

        } catch (Exception e) {
            Logger.error("Yeeks, I can't load my configuration. What did you do??");
            Logger.DumpStack(e);
        }
    }

    @EventHandler
    public void Init(FMLInitializationEvent event) {
        if (ModInitSuccessful) {
            if (_cfgManager.MakeMobsAngryOnBlockBreak) {
                Logger.info("BlockBreak module is enabled. Some mobs will get very angry...");
                BlockTakeAggroModule = new BlockBreakEvent(_cfgManager);
                MinecraftForge.EVENT_BUS.register(BlockTakeAggroModule);
            }

            if (_cfgManager.PlayerSpawnProtection) {
                Logger.info("Spawn-Protection is enabled. Players will be protected until they attack");
                SpawnProtectionModule = new PlayerSpawnProtection(_cfgManager/* , _mPersistedConfig */);
                MinecraftForge.EVENT_BUS.register(SpawnProtectionModule);
                FMLCommonHandler.instance().bus().register(SpawnProtectionModule);
            }

            if (_cfgManager.FriendlyMobRevenge) {
                Logger.info("FriendlyMobRevenge is enabled. Be careful what you eat...");
                EatCookedAnimalsModule = new EatCookedAnimalsEvent();
                MinecraftForge.EVENT_BUS.register(EatCookedAnimalsModule);
            }

            if (_cfgManager.KamikazeMobRevenge) {
                Logger.info("KamikazeMobRevenge is enabled. Have fun :P");
                KamikazeRevengeModule = new KamikazeRevenge(_cfgManager);
                MinecraftForge.EVENT_BUS.register(KamikazeRevengeModule);
            }
        } else Logger.warn(
                String.format(
                        "%s will NOT do anything as there where errors due the preInit event. Check the logfile!",
                        Reference.MODID));
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    /**
     * Do some stuff once the server starts
     * 
     * @param pEvent
     */
    @EventHandler
    public void serverLoad(FMLServerStartingEvent pEvent) {
        if (_cfgManager.PlayerSpawnProtection) {
            pEvent.registerServerCommand(new AngerProtectionCommand());
        }
    }
}
