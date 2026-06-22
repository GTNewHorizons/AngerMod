package com.github.namikon.angermod.config;

import com.github.namikon.angermod.AngerMod;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = AngerMod.MODID, category = "friendly-animal-revenge")
@Config.LangKey("angermod.config.friendly-animal-revenge")
@Config.Comment("Eating animal-based food angers those animals.")
public final class FriendlyAnimalRevengeConfig {

    @Config.Comment("If set to true, sheep will attack/flee if you eat mutton, pigs if you eat pork,... The attack/flee is based on additional mods you have installed.")
    @Config.DefaultBoolean(false)
    public static boolean enabled;

    @Config.DefaultInt(16)
    @Config.RangeInt(min = 2, max = 128)
    public static int revengeRadius;

    @Config.Comment("If the food eaten by the player contains these keywords, all PIGS around will become angry (or flee)")
    @Config.DefaultStringList({ "pork", "bacon", "ham" })
    public static String[] pigFoodTrigger;

    @Config.Comment("Keywords to exclude from pig food triggers")
    @Config.DefaultStringList({ "hamburger" })
    public static String[] pigFoodExclusions;

    @Config.Comment("If the food eaten by the player contains these keywords, all COWS around will become angry (or flee)")
    @Config.DefaultStringList({ "beef" })
    public static String[] cowFoodTrigger;

    @Config.Comment("Keywords to exclude from cow food triggers")
    @Config.DefaultStringList({})
    public static String[] cowFoodExclusions;

    @Config.Comment("If the food eaten by the player contains these keywords, all CHICKENS around will become angry (or flee)")
    @Config.DefaultStringList({ "chicken", "egg", "wing" })
    public static String[] chickenFoodTrigger;

    @Config.Comment("Keywords to exclude from chicken food triggers")
    @Config.DefaultStringList({ "eggplant" })
    public static String[] chickenFoodExclusions;

    @Config.Comment("If the food eaten by the player contains these keywords, all SHEEP around will become angry (or flee)")
    @Config.DefaultStringList({ "mutton", "lamb" })
    public static String[] sheepFoodTrigger;

    @Config.Comment("Keywords to exclude from sheep food triggers")
    @Config.DefaultStringList({})
    public static String[] sheepFoodExclusions;
}
