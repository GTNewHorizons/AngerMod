package com.github.namikon.angermod;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.namikon.angermod.auxiliary.Tags;
import com.github.namikon.angermod.command.AngerProtectionCommand;
import com.github.namikon.angermod.config.BlockBreakAngerConfig;
import com.github.namikon.angermod.config.FriendlyAnimalRevengeConfig;
import com.github.namikon.angermod.config.KamikazeConfig;
import com.github.namikon.angermod.config.LegacyAngerModConfig;
import com.github.namikon.angermod.config.SpawnProtectionConfig;
import com.github.namikon.angermod.events.BlockBreakEvent;
import com.github.namikon.angermod.events.EatCookedAnimalsEvent;
import com.github.namikon.angermod.events.KamikazeRevenge;
import com.github.namikon.angermod.events.PlayerSpawnProtection;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * @author Namikon
 *
 */
@Mod(
        modid = AngerMod.MODID,
        name = "AngerMod. Makes your Mobs angry!",
        version = Tags.VERSION,
        dependencies = "required-after:Forge@[10.13.4.1614,);required-after:Baubles@[1.0.1.10,);required-after:gtnhlib@[0.8.34,)")
public class AngerMod {

    public static final String MODID = "angermod";
    public static final Logger LOGGER = LogManager.getLogger("AngerMod");

    public static PlayerSpawnProtection spawnProtectionEventHandler = null;
    public static BlockBreakEvent blockBreakEventHandler = null;
    public static EatCookedAnimalsEvent eatCookedAnimalsEventHandler = null;
    public static KamikazeRevenge kamikazeRevengeEventHandler = null;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        try {
            ConfigurationManager.registerConfig(BlockBreakAngerConfig.class);
            ConfigurationManager.registerConfig(FriendlyAnimalRevengeConfig.class);
            ConfigurationManager.registerConfig(KamikazeConfig.class);
            ConfigurationManager.registerConfig(SpawnProtectionConfig.class);
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }

        // Load legacy configs, if any exist
        final File legacyFile = new File(event.getModConfigurationDirectory(), "GTNewHorizons/angermod.cfg");
        if (legacyFile.canRead()) {
            LegacyAngerModConfig.loadLegacyConfig(new Configuration(legacyFile));
            ConfigurationManager.save(
                    BlockBreakAngerConfig.class,
                    FriendlyAnimalRevengeConfig.class,
                    KamikazeConfig.class,
                    SpawnProtectionConfig.class);

            if (!legacyFile.delete()) {
                LOGGER.warn(
                        "Unable to delete legacy config settings. Please delete the file in config/GTNewHorizons/angermod.cfg to stop the new config file from being overwritten on load.");
            }
        }

        FMLCommonHandler.instance().bus().register(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        applyConfigs();
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new AngerProtectionCommand());
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (!AngerMod.MODID.equals(event.modID)) return;
        applyConfigs();
    }

    private static void applyConfigs() {
        if (BlockBreakAngerConfig.enabled) {
            LOGGER.info("BlockBreak module is enabled. Some mobs will get very angry...");
            BlockBreakAngerConfig.reloadConfigs();
            if (blockBreakEventHandler == null) {
                blockBreakEventHandler = new BlockBreakEvent();
                MinecraftForge.EVENT_BUS.register(blockBreakEventHandler);
            }
        } else if (blockBreakEventHandler != null) {
            MinecraftForge.EVENT_BUS.unregister(blockBreakEventHandler);
            blockBreakEventHandler = null;
        }

        if (SpawnProtectionConfig.enabled) {
            LOGGER.info("Spawn-Protection is enabled. Players will be protected until they attack");
            SpawnProtectionConfig.reloadConfigs();
            if (spawnProtectionEventHandler == null) {
                spawnProtectionEventHandler = new PlayerSpawnProtection();
                MinecraftForge.EVENT_BUS.register(spawnProtectionEventHandler);
                FMLCommonHandler.instance().bus().register(spawnProtectionEventHandler);
            }
        } else if (spawnProtectionEventHandler != null) {
            MinecraftForge.EVENT_BUS.unregister(spawnProtectionEventHandler);
            FMLCommonHandler.instance().bus().unregister(spawnProtectionEventHandler);
            spawnProtectionEventHandler = null;
        }

        if (FriendlyAnimalRevengeConfig.enabled) {
            LOGGER.info("FriendlyMobRevenge is enabled. Be careful what you eat...");
            if (eatCookedAnimalsEventHandler == null) {
                eatCookedAnimalsEventHandler = new EatCookedAnimalsEvent();
                MinecraftForge.EVENT_BUS.register(eatCookedAnimalsEventHandler);
            }
        } else if (eatCookedAnimalsEventHandler != null) {
            MinecraftForge.EVENT_BUS.unregister(eatCookedAnimalsEventHandler);
            eatCookedAnimalsEventHandler = null;
        }

        if (KamikazeConfig.enabled) {
            LOGGER.info("KamikazeMobRevenge is enabled. Have fun :P");
            if (kamikazeRevengeEventHandler == null) {
                kamikazeRevengeEventHandler = new KamikazeRevenge();
                MinecraftForge.EVENT_BUS.register(kamikazeRevengeEventHandler);
            }
        } else if (kamikazeRevengeEventHandler != null) {
            MinecraftForge.EVENT_BUS.unregister(kamikazeRevengeEventHandler);
            kamikazeRevengeEventHandler = null;
        }
    }
}
