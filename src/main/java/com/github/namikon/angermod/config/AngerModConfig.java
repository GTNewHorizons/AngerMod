package com.github.namikon.angermod.config;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import com.github.namikon.angermod.AngerMod;
import com.github.namikon.angermod.auxiliary.BlockSet;
import com.github.namikon.angermod.auxiliary.ItemSet;
import com.gtnewhorizon.gtnhlib.config.Config;

import cpw.mods.fml.common.registry.GameRegistry;
import eu.usrv.yamcore.config.ConfigManager;

/**
 * Specific configuration for >this< mod
 *
 * @author Namikon
 *
 */
@Config(modid = AngerMod.MODID)
public class AngerModConfig extends ConfigManager {

    @Config.Ignore
    public static final BlockSet BlacklistedNetherBlocks = new BlockSet();

    @Config.Ignore
    public static final BlockSet BlacklistedEndBlocks = new BlockSet();

    @Config.Ignore
    public static final ItemSet WhitelistedProtectionBaubles = new ItemSet();

    @Config.Comment("The range at which endermen will get angered by broken blocks in the End.")
    @Config.DefaultInt(16)
    @Config.RangeInt(min = 2, max = 128)
    public static int EndermanAggrorange;

    @Config.Comment("The range at which zombie pigmen will get angered by broken blocks in the Nether.")
    @Config.DefaultInt(16)
    @Config.RangeInt(min = 2, max = 128)
    public static int PigmenAggrorange;

    @Config.Comment("The maximum number of seconds a player will be protected from damage if he is just standing still and doing nothing.")
    @Config.DefaultInt(10)
    @Config.RangeInt(min = 1, max = 2048)
    public static int SpawnProtectionTimeout;

    @Config.Comment("The number of blocks the player is able to move away from their initial spawn location before their protection fades.")
    @Config.DefaultInt(5)
    @Config.RangeInt(min = 1, max = 2048)
    public static int SpawnProtectionMoveTolerance;

    @Config.Comment("Chance, in percent, how often a Kamikaze event will happen.")
    @Config.DefaultInt(5)
    @Config.RangeInt(min = 0, max = 100)
    public static int KamikazeChance;

    @Config.DefaultInt(16)
    @Config.RangeInt(min = 2, max = 128)
    public static int FriendlyMobRevengeRadius;

    @Config.Comment("New / respawned players will be ignored by monsters until they attack something, move, or their timer runs out.")
    @Config.DefaultBoolean(false)
    public static boolean PlayerSpawnProtection;

    @Config.Comment("Breaking certain blocks will anger mobs.")
    @Config.DefaultBoolean(false)
    public static boolean MakeMobsAngryOnBlockBreak;

    @Config.Comment("If set to true, sheep will attack/flee if you eat mutton, pigs if you eat pork,... The attack/flee is based on additional mods you have installed.")
    @Config.DefaultBoolean(false)
    public static boolean FriendlyMobRevenge;

    @Config.Comment("Killed passive mobs have a chance to explode unless killed with the right tool.")
    @Config.DefaultBoolean(false)
    public static boolean KamikazeMobRevenge;

    @Config.Comment("If set to true, the kamikaze event will cause terrain damage (but still follow gamerule 'mobGriefing')")
    @Config.DefaultBoolean(false)
    public static boolean KamikazeMobsDoTerrainDamage;

    @Config.Comment("If the food eaten by the player contains these keywords, all PIGS around will become angry (or flee)")
    @Config.DefaultStringList({ "pork" })
    public static String[] PigFoodTrigger;

    @Config.Comment("If the food eaten by the player contains these keywords, all COWS around will become angry (or flee)")
    @Config.DefaultStringList({ "beef" })
    public static String[] CowFoodTrigger;

    @Config.Comment("If the food eaten by the player contains these keywords, all CHICKENS around will become angry (or flee)")
    @Config.DefaultStringList({ "chicken", "egg" })
    public static String[] ChickenFoodTrigger;

    @Config.Comment("If the food eaten by the player contains these keywords, all SHEEP around will become angry (or flee)")
    @Config.DefaultStringList({ "mutton" })
    public static String[] SheepFoodTrigger;

    @Config.Comment("If the player is using one of these items, entities will not explode if they are killed.")
    @Config.DefaultStringList({ "flint" })
    public static String[] ButcherItems;

    @Config.Comment("Set items here which change players invulnerability. You will notice those, as they will spam the console with *protection fades* messages.")
    @Config.DefaultStringList({ "EMT:BaseBaubles" })
    public static String[] WhitelistedProtectionItems;

    @Config.Comment("Define all Blocks here where Enderman should become angry when you break them.")
    @Config.DefaultStringList({ "gregtech:gt.blockores" })
    private static String[] tCfgBlacklistedEndBlocks;

    @Config.Comment("Define all Blocks here where Zombie Pigmen should become angry when you break them.")
    @Config.DefaultStringList({ "gregtech:gt.blockores" })
    private static String[] tCfgBlacklistedNetherBlocks;

    public AngerModConfig(File pConfigBaseDirectory, String pModCollectionDirectory, String pModID) {
        super(pConfigBaseDirectory, pModCollectionDirectory, pModID);
    }

    /**
     * PreInit default values and lists
     */
    @Override
    protected void PreInit() {
        EndermanAggrorange = 16;
        PigmenAggrorange = 16;
        SpawnProtectionMoveTolerance = 5;
        SpawnProtectionTimeout = 10;
        KamikazeChance = 5;
        PigFoodTrigger = new String[] { "pork" };
        CowFoodTrigger = new String[] { "beef" };
        ChickenFoodTrigger = new String[] { "chicken", "egg" };
        SheepFoodTrigger = new String[] { "mutton" };
        ButcherItems = new String[] { "flint" };
        FriendlyMobRevengeRadius = 16;
        WhitelistedProtectionItems = new String[] { "EMT:BaseBaubles" };
    }

    @Override
    protected void PostInit() {
        parseBlacklistedBlocks(tCfgBlacklistedEndBlocks, BlacklistedEndBlocks, "End");
        parseBlacklistedBlocks(tCfgBlacklistedNetherBlocks, BlacklistedNetherBlocks, "Nether");

        WhitelistedProtectionBaubles.clear();
        for (String itemString : WhitelistedProtectionItems) {
            String[] parts = itemString.split(":");

            Item item;
            if (parts.length == 0) {
                continue;
            } else if (parts.length == 1) {
                item = GameRegistry.findItem("minecraft", parts[0]);
            } else {
                item = GameRegistry.findItem(parts[0], parts[1]);
            }

            if (item == null) {
                AngerMod.Logger.error("Item {} in config could not be found.", itemString);
                continue;
            }

            if (parts.length >= 3) {
                if (parts.length >= 4)
                    AngerMod.Logger.error("Item {} in config has too many parts, ignoring extra parts.", itemString);

                try {
                    WhitelistedProtectionBaubles.add(item, Integer.parseInt(parts[2]));
                } catch (NumberFormatException e) {
                    AngerMod.Logger.error("Could not parse metadata value of item {} in config", itemString);
                }
            } else {
                WhitelistedProtectionBaubles.add(item);
            }
        }
    }

    @Override
    protected void Init() {
        tCfgBlacklistedEndBlocks = _mainConfig.getStringList(
                "EndBlocks",
                "Blacklist",
                new String[] { "gregtech:gt.blockores" },
                "Define all Blocks here where Enderman should become angry when you break them");
        tCfgBlacklistedNetherBlocks = _mainConfig.getStringList(
                "NetherBlocks",
                "Blacklist",
                new String[] { "gregtech:gt.blockores" },
                "Define all Blocks here where Pigmen should become angry when you break them");
        ButcherItems = _mainConfig.getStringList(
                "KamikazeItemBlacklist",
                "Blacklist",
                ButcherItems,
                "If the player is using one of these items, entities will not explode if they are killed");

        EndermanAggrorange = _mainConfig.getInt(
                "Enderman",
                "Limits",
                EndermanAggrorange,
                2,
                128,
                "The maximum range where Enderman shall become angry");
        PigmenAggrorange = _mainConfig.getInt(
                "Pigmen",
                "Limits",
                PigmenAggrorange,
                2,
                128,
                "The maximum range where Pigmen shall become angry");
        // SleepingThreshold = _mainConfig.getInt("MaxSleepTimes", "Limits", SleepingThreshold, 1, Integer.MAX_VALUE,
        // "How often can a player sleep until his protection bubble will fade on every world-interaction (except
        // breaking blocks with his bare hands)");
        KamikazeChance = _mainConfig.getInt(
                "KamikazeChance",
                "Limits",
                KamikazeChance,
                1,
                100,
                "Chance, in percent, how often a Kamikaze event will happen");
        FriendlyMobRevengeRadius = _mainConfig.getInt(
                "FriendlyMobRevengeRadius",
                "Limits",
                FriendlyMobRevengeRadius,
                2,
                128,
                "The maximum range where animals will flee/become angry once the food-trigger is.. triggered");
        SpawnProtectionTimeout = _mainConfig.getInt(
                "SpawnProtectionTimeout",
                "Limits",
                SpawnProtectionTimeout,
                1,
                2048,
                "The maximum number of seconds a player will be protected from damage if he is just standing still and doing nothing");
        SpawnProtectionMoveTolerance = _mainConfig.getInt(
                "SpawnProtectionMoveTolerance",
                "Limits",
                SpawnProtectionMoveTolerance,
                1,
                2048,
                "How many Blocks will the player be able to move away from his initial spawn location until his protection fades");

        PlayerSpawnProtection = _mainConfig.getBoolean(
                "ProtectionEnabled",
                "ModuleControl",
                false,
                "Define if new players / respawned players shall be ignored from monsters until they attack something, move or the timer runs out");
        MakeMobsAngryOnBlockBreak = _mainConfig.getBoolean(
                "BlockBreakEnabled",
                "ModuleControl",
                false,
                "Enable/disable block-breaking-makes-mobs-angry module");
        FriendlyMobRevenge = _mainConfig.getBoolean(
                "FriendlyMobRevenge",
                "ModuleControl",
                false,
                "If set to true, sheep will attack/flee if you eat mutton, pigs if you eat pork,... The attack/flee is based on additional mods you have installed");
        KamikazeMobRevenge = _mainConfig
                .getBoolean("KamikazeMobRevenge", "ModuleControl", false, "Guess what it is ...");

        // RespawnProtectionOnlyOnDeath = _mainConfig.getBoolean("RespawnProtectionOnlyOnDeath", "Protection", false,
        // "If set to true, a player that (re)spawns in any world will only be protected if his score is 0");
        KamikazeMobsDoTerrainDamage = _mainConfig.getBoolean(
                "KamikazeMobsDoTerrainDamage",
                "Protection",
                false,
                "If set to true, the kamikaze event will cause terrain damage (but still follow gamerule 'mobGriefing')");

        PigFoodTrigger = _mainConfig.getStringList(
                "PigFoodTrigger",
                "MobRevengeTrigger",
                PigFoodTrigger,
                "If the food eaten by the player contains these keywords, all PIGS around will become angry (or flee)");
        CowFoodTrigger = _mainConfig.getStringList(
                "CowFoodTrigger",
                "MobRevengeTrigger",
                CowFoodTrigger,
                "If the food eaten by the player contains these keywords, all COWS around will become angry (or flee)");
        ChickenFoodTrigger = _mainConfig.getStringList(
                "ChickenFoodTrigger",
                "MobRevengeTrigger",
                ChickenFoodTrigger,
                "If the food eaten by the player contains these keywords, all CHICKEN around will become angry (or flee)");
        SheepFoodTrigger = _mainConfig.getStringList(
                "SheepFoodTrigger",
                "MobRevengeTrigger",
                SheepFoodTrigger,
                "If the food eaten by the player contains these keywords, all SHEEP around will become angry (or flee)");

        WhitelistedProtectionItems = _mainConfig.getStringList(
                "WhitelistedProtectionItems",
                "Whitelist",
                WhitelistedProtectionItems,
                "Set items here which change players invulnerability. You will notice those, as they will spam the console with *protection fades* messages");
    }

    /**
     * Go ahead and parse the given list of strings to fill the BlockSet ID
     *
     * @param blockNames
     * @param collection
     */
    private void parseBlacklistedBlocks(String[] blockNames, BlockSet collection, String dimension) {
        collection.clear();

        for (String blockName : blockNames) {
            parseBlockString(blockName, collection, dimension);
        }
    }

    private static void parseBlockString(String blockDataString, BlockSet collection, String dimension) {
        String[] blockInfo = blockDataString.split(":");

        if (blockInfo.length < 2) {
            AngerMod.Logger.error(
                    "BlockName {} in config for dimension {} is invalid. Make sure you use full [domain]:[blockname] notation!",
                    blockDataString,
                    dimension);
            return;
        }

        final Block block = GameRegistry.findBlock(blockInfo[0], blockInfo[1]);

        if (blockInfo.length >= 3) {
            // Meta-aware entry
            if (blockInfo.length >= 4) AngerMod.Logger.error(
                    "BlockName {} in config for dimension {} has too many parts. Extra parts have been ignored.",
                    blockDataString,
                    dimension);

            try {
                int meta = Integer.parseInt(blockInfo[2]);
                collection.add(block, meta);
            } catch (NumberFormatException e) {
                AngerMod.Logger.error(
                        "BlockName {} in config for dimension {} has unparseable metadata. The metadata must be a number.",
                        blockDataString,
                        dimension);
            }
        } else {
            // Wildcard entry
            collection.add(block);
        }
    }
}
