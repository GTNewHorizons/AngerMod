package com.github.namikon.angermod;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.namikon.angermod.auxiliary.Tags;
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
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * @author Namikon
 *
 */
@Mod(
        modid = AngerMod.MODID,
        name = "AngerMod. Makes your Mobs angry!",
        version = Tags.VERSION,
        dependencies = "required-after:Forge@[10.13.4.1614,);required-after:Baubles@[1.0.1.10,);"
                + "required-after:YAMCore@[0.3,);")
public class AngerMod {

    public static final String MODID = "angermod";
    public static AngerModConfig _cfgManager = null;
    public static Logger Logger = LogManager.getLogger("AngerMod");

    public static PlayerSpawnProtection SpawnProtectionModule = null;
    public static BlockBreakEvent BlockTakeAggroModule = null;
    public static EatCookedAnimalsEvent EatCookedAnimalsModule = null;
    public static KamikazeRevenge KamikazeRevengeModule = null;

    public static boolean ModInitSuccessful = true;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        _cfgManager = new AngerModConfig(event.getModConfigurationDirectory(), "GTNewHorizons", AngerMod.MODID);
        if (!_cfgManager.LoadConfig()) ModInitSuccessful = false;
    }

    @EventHandler
    public void Init(FMLInitializationEvent event) {
        if (ModInitSuccessful) {
            if (AngerModConfig.MakeMobsAngryOnBlockBreak) {
                Logger.info("BlockBreak module is enabled. Some mobs will get very angry...");
                BlockTakeAggroModule = new BlockBreakEvent();
                MinecraftForge.EVENT_BUS.register(BlockTakeAggroModule);
            }

            if (AngerModConfig.PlayerSpawnProtection) {
                Logger.info("Spawn-Protection is enabled. Players will be protected until they attack");
                SpawnProtectionModule = new PlayerSpawnProtection();
                MinecraftForge.EVENT_BUS.register(SpawnProtectionModule);
                FMLCommonHandler.instance().bus().register(SpawnProtectionModule);
            }

            if (AngerModConfig.FriendlyMobRevenge) {
                Logger.info("FriendlyMobRevenge is enabled. Be careful what you eat...");
                EatCookedAnimalsModule = new EatCookedAnimalsEvent();
                MinecraftForge.EVENT_BUS.register(EatCookedAnimalsModule);
            }

            if (AngerModConfig.KamikazeMobRevenge) {
                Logger.info("KamikazeMobRevenge is enabled. Have fun :P");
                KamikazeRevengeModule = new KamikazeRevenge();
                MinecraftForge.EVENT_BUS.register(KamikazeRevengeModule);
            }
        } else Logger.warn(
                "{} will NOT do anything as there where errors due the preInit event. Check the logfile!",
                AngerMod.MODID);
    }

    /**
     * Do some stuff once the server starts
     *
     * @param pEvent
     */
    @EventHandler
    public void serverLoad(FMLServerStartingEvent pEvent) {
        if (AngerModConfig.PlayerSpawnProtection) {
            pEvent.registerServerCommand(new AngerProtectionCommand());
        }
    }
}
