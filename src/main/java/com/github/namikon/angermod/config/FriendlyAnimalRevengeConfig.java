package com.github.namikon.angermod.config;

import com.github.namikon.angermod.AngerMod;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = AngerMod.MODID, category = "friendly-animal-revenge")
public class FriendlyAnimalRevengeConfig {

    @Config.Comment("If set to true, sheep will attack/flee if you eat mutton, pigs if you eat pork,... The attack/flee is based on additional mods you have installed.")
    @Config.DefaultBoolean(false)
    public static boolean enabled;

    @Config.DefaultInt(16)
    @Config.RangeInt(min = 2, max = 128)
    public static int revengeRadius;

    @Config.Comment("If the food eaten by the player contains these keywords, all PIGS around will become angry (or flee)")
    @Config.DefaultStringList({ "pork" })
    public static String[] pigFoodTrigger;

    @Config.Comment("If the food eaten by the player contains these keywords, all COWS around will become angry (or flee)")
    @Config.DefaultStringList({ "beef" })
    public static String[] cowFoodTrigger;

    @Config.Comment("If the food eaten by the player contains these keywords, all CHICKENS around will become angry (or flee)")
    @Config.DefaultStringList({ "chicken", "egg" })
    public static String[] chickenFoodTrigger;

    @Config.Comment("If the food eaten by the player contains these keywords, all SHEEP around will become angry (or flee)")
    @Config.DefaultStringList({ "mutton" })
    public static String[] sheepFoodTrigger;
}
