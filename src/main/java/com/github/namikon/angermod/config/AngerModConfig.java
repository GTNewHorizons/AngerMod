package com.github.namikon.angermod.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.namikon.angermod.AngerMod;
import com.github.namikon.angermod.auxiliary.MinecraftBlock;

import eu.usrv.yamcore.config.ConfigManager;

/**
 * Specific configuration for >this< mod
 *
 * @author Namikon
 *
 */
public class AngerModConfig extends ConfigManager {

    public List<MinecraftBlock> BlacklistedBlocks = null;

    public int EndermanAggrorange;
    public int PigmenAggrorange;
    // public int SleepingThreshold;
    public int SpawnProtectionTimeout;
    public int SpawnProtectionMoveTolerance;
    public int KamikazeChance;
    public int FriendlyMobRevengeRadius;

    public boolean PlayerSpawnProtection;
    public boolean MakeMobsAngryOnBlockBreak;
    // public boolean RespawnProtectionOnlyOnDeath;
    public boolean FriendlyMobRevenge;
    public boolean KamikazeMobRevenge;
    public boolean KamikazeMobsDoTerrainDamage;

    private String[] _mDefaultBlacklistedEndBlocks = null;
    private String[] _mDefaultBlacklistedNetherBlocks = null;

    public String[] PigFoodTrigger;
    public String[] CowFoodTrigger;
    public String[] ChickenFoodTrigger;
    public String[] SheepFoodTrigger;
    public String[] ButcherItems;
    public String[] WhitelistedProtectionItems;

    private String tCfgBlacklistedEndBlocks[] = null;
    private String tCfgBlacklistedNetherBlocks[] = null;

    public AngerModConfig(File pConfigBaseDirectory, String pModCollectionDirectory, String pModID) {
        super(pConfigBaseDirectory, pModCollectionDirectory, pModID);
    }

    /**
     * PreInit default values and lists
     */
    @Override
    protected void PreInit() {
        BlacklistedBlocks = new ArrayList<>();

        _mDefaultBlacklistedEndBlocks = new String[] { "gregtech:gt.blockores" };
        _mDefaultBlacklistedNetherBlocks = new String[] { "gregtech:gt.blockores" };

        EndermanAggrorange = 16;
        PigmenAggrorange = 16;
        // SleepingThreshold = 20;
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
        ParseBlacklistedBlocks(tCfgBlacklistedEndBlocks, 1);
        ParseBlacklistedBlocks(tCfgBlacklistedNetherBlocks, -1);
    }

    @Override
    protected void Init() {
        tCfgBlacklistedEndBlocks = _mainConfig.getStringList(
                "EndBlocks",
                "Blacklist",
                _mDefaultBlacklistedEndBlocks,
                "Define all Blocks here where Enderman should become angry when you break them");
        tCfgBlacklistedNetherBlocks = _mainConfig.getStringList(
                "NetherBlocks",
                "Blacklist",
                _mDefaultBlacklistedNetherBlocks,
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
     * Go ahead and parse the given list of strings to actual instances of MinecraftBlock classes with bound dimension
     * ID
     *
     * @param pBlockNames
     * @param pDimension
     */
    private void ParseBlacklistedBlocks(String pBlockNames[], int pDimension) {
        try {
            for (String tBlockName : pBlockNames) {
                try {
                    MinecraftBlock tBlock = new MinecraftBlock(tBlockName, pDimension);
                    AngerMod.Logger.info(
                            String.format("New block added for Dimension: %d BlockID: %s", pDimension, tBlockName));
                    BlacklistedBlocks.add(tBlock); // TODO: Make sure we only add each block once...
                } catch (Exception e) {
                    AngerMod.Logger.warn(
                            String.format(
                                    "NetherBlock Definition %s will be ignored. Check your spelling [ModID]:[BlockName] or [ModID]:[BlockName]:[BlockMeta]",
                                    tBlockName));
                    AngerMod.Logger.DumpStack(e);
                }
            }
        } catch (Exception e) {
            AngerMod.Logger.error("Error while parsing Blacklist for Nether blocks");
            AngerMod.Logger.DumpStack(e);
        }
    }
}
