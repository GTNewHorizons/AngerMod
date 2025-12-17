package com.github.namikon.angermod.config;

import net.minecraftforge.common.config.Configuration;

/**
 * Loader for the old configuration files.
 *
 * @author Namikon
 */
public final class LegacyAngerModConfig {

    public static void loadLegacyConfig(Configuration legacyConfig) {
        BlockBreakAngerConfig.endBlacklist = legacyConfig.getStringList(
                "EndBlocks",
                "Blacklist",
                BlockBreakAngerConfig.endBlacklist,
                "Define all Blocks here where Enderman should become angry when you break them");
        BlockBreakAngerConfig.netherBlacklist = legacyConfig.getStringList(
                "NetherBlocks",
                "Blacklist",
                BlockBreakAngerConfig.endBlacklist,
                "Define all Blocks here where Pigmen should become angry when you break them");
        KamikazeConfig.butcherItems = legacyConfig.getStringList(
                "KamikazeItemBlacklist",
                "Blacklist",
                KamikazeConfig.butcherItems,
                "If the player is using one of these items, entities will not explode if they are killed");

        BlockBreakAngerConfig.endermanAggroRange = legacyConfig.getInt(
                "Enderman",
                "Limits",
                BlockBreakAngerConfig.endermanAggroRange,
                2,
                128,
                "The maximum range where Enderman shall become angry");
        BlockBreakAngerConfig.pigmanAggroRange = legacyConfig.getInt(
                "Pigmen",
                "Limits",
                BlockBreakAngerConfig.pigmanAggroRange,
                2,
                128,
                "The maximum range where Pigmen shall become angry");

        KamikazeConfig.chance = legacyConfig.getInt(
                "KamikazeChance",
                "Limits",
                KamikazeConfig.chance,
                1,
                100,
                "Chance, in percent, how often a Kamikaze event will happen");
        FriendlyAnimalRevengeConfig.revengeRadius = legacyConfig.getInt(
                "FriendlyMobRevengeRadius",
                "Limits",
                FriendlyAnimalRevengeConfig.revengeRadius,
                2,
                128,
                "The maximum range where animals will flee/become angry once the food-trigger is.. triggered");
        SpawnProtectionConfig.maxDuration = legacyConfig.getInt(
                "SpawnProtectionTimeout",
                "Limits",
                SpawnProtectionConfig.maxDuration,
                1,
                2048,
                "The maximum number of seconds a player will be protected from damage if he is just standing still and doing nothing");
        SpawnProtectionConfig.moveTolerance = legacyConfig.getInt(
                "SpawnProtectionMoveTolerance",
                "Limits",
                SpawnProtectionConfig.moveTolerance,
                1,
                2048,
                "How many Blocks will the player be able to move away from his initial spawn location until his protection fades");

        SpawnProtectionConfig.enabled = legacyConfig.getBoolean(
                "ProtectionEnabled",
                "ModuleControl",
                false,
                "Define if new players / respawned players shall be ignored from monsters until they attack something, move or the timer runs out");
        BlockBreakAngerConfig.enabled = legacyConfig.getBoolean(
                "BlockBreakEnabled",
                "ModuleControl",
                false,
                "Enable/disable block-breaking-makes-mobs-angry module");
        FriendlyAnimalRevengeConfig.enabled = legacyConfig.getBoolean(
                "FriendlyMobRevenge",
                "ModuleControl",
                false,
                "If set to true, sheep will attack/flee if you eat mutton, pigs if you eat pork,... The attack/flee is based on additional mods you have installed");
        KamikazeConfig.enabled = legacyConfig
                .getBoolean("KamikazeMobRevenge", "ModuleControl", false, "Guess what it is ...");

        KamikazeConfig.doTerrainDamage = legacyConfig.getBoolean(
                "KamikazeMobsDoTerrainDamage",
                "Protection",
                false,
                "If set to true, the kamikaze event will cause terrain damage (but still follow gamerule 'mobGriefing')");

        FriendlyAnimalRevengeConfig.pigFoodTrigger = legacyConfig.getStringList(
                "PigFoodTrigger",
                "MobRevengeTrigger",
                FriendlyAnimalRevengeConfig.pigFoodTrigger,
                "If the food eaten by the player contains these keywords, all PIGS around will become angry (or flee)");
        FriendlyAnimalRevengeConfig.cowFoodTrigger = legacyConfig.getStringList(
                "CowFoodTrigger",
                "MobRevengeTrigger",
                FriendlyAnimalRevengeConfig.cowFoodTrigger,
                "If the food eaten by the player contains these keywords, all COWS around will become angry (or flee)");
        FriendlyAnimalRevengeConfig.chickenFoodTrigger = legacyConfig.getStringList(
                "ChickenFoodTrigger",
                "MobRevengeTrigger",
                FriendlyAnimalRevengeConfig.chickenFoodTrigger,
                "If the food eaten by the player contains these keywords, all CHICKEN around will become angry (or flee)");
        FriendlyAnimalRevengeConfig.sheepFoodTrigger = legacyConfig.getStringList(
                "SheepFoodTrigger",
                "MobRevengeTrigger",
                FriendlyAnimalRevengeConfig.sheepFoodTrigger,
                "If the food eaten by the player contains these keywords, all SHEEP around will become angry (or flee)");

        SpawnProtectionConfig.protectionAffectingItems = legacyConfig.getStringList(
                "WhitelistedProtectionItems",
                "Whitelist",
                SpawnProtectionConfig.protectionAffectingItems,
                "Set items here which change players invulnerability. You will notice those, as they will spam the console with *protection fades* messages");
    }

}
