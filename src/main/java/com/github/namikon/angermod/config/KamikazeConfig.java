package com.github.namikon.angermod.config;

import com.github.namikon.angermod.AngerMod;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = AngerMod.MODID, category = "kamikaze")
@Config.LangKey("angermod.config.kamikaze")
@Config.Comment("Killed mobs have a chance to explode.")
public class KamikazeConfig {

    @Config.Comment("Killed passive mobs have a chance to explode unless killed with the right tool.")
    @Config.DefaultBoolean(false)
    public static boolean enabled;

    @Config.Comment("Chance, in percent, how often a Kamikaze event will happen.")
    @Config.DefaultInt(5)
    @Config.RangeInt(min = 0, max = 100)
    public static int chance;

    @Config.Comment("If set to true, the kamikaze event will cause terrain damage (but will still follow the 'mobGriefing' gamerule)")
    @Config.DefaultBoolean(false)
    public static boolean doTerrainDamage;

    @Config.Comment("If the player is using one of these items, entities will not explode if they are killed.")
    @Config.DefaultStringList({ "flint" })
    public static String[] butcherItems;
}
