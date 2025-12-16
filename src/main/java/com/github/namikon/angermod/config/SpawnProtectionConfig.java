package com.github.namikon.angermod.config;

import net.minecraft.item.Item;

import com.github.namikon.angermod.AngerMod;
import com.github.namikon.angermod.auxiliary.ItemSet;
import com.gtnewhorizon.gtnhlib.config.Config;

import cpw.mods.fml.common.registry.GameRegistry;

@Config(modid = AngerMod.MODID, category = "spawn-protection")
@Config.LangKey("angermod.config.spawn-protection")
@Config.Comment("Players cannot be damaged immediately after respawning or crossing dimensions.")
public final class SpawnProtectionConfig {

    @Config.Ignore
    public static final ItemSet WhitelistedProtectionBaubles = new ItemSet();

    @Config.Comment("The maximum number of seconds a player will be protected from damage if he is just standing still and doing nothing.")
    @Config.DefaultInt(10)
    @Config.RangeInt(min = 1, max = 2048)
    public static int maxDuration;

    @Config.Comment("The number of blocks the player is able to move away from their initial spawn location before their protection fades.")
    @Config.DefaultInt(5)
    @Config.RangeInt(min = 1, max = 2048)
    public static int moveTolerance;

    @Config.Comment("New / respawned players will be ignored by monsters until they attack something, move, or their timer runs out.")
    @Config.DefaultBoolean(false)
    public static boolean enabled;

    @Config.Comment("Set items here which change players invulnerability to prevent this mod from conflicting with them. You will notice those, as they will spam the console with *protection fades* messages.")
    @Config.DefaultStringList({ "EMT:BaseBaubles" })
    public static String[] protectionAffectingItems;

    public static void reloadConfigs() {
        WhitelistedProtectionBaubles.clear();
        for (String itemString : protectionAffectingItems) {
            String[] parts = itemString.split(":");

            Item item;
            if (parts.length < 2) {
                AngerMod.LOGGER.error(
                        "Item {} in list of protection-affecting items is invalid. Format: [mod-id]:[item-name] or [mod-id]:[item-name]:[meta]",
                        itemString);
                continue;
            } else {
                item = GameRegistry.findItem(parts[0], parts[1]);
            }

            if (item == null) {
                AngerMod.LOGGER.warn("Item {} in list of protection-affecting items could not be found.", itemString);
                continue;
            }

            if (parts.length >= 3) {
                if (parts.length >= 4) AngerMod.LOGGER.error(
                        "Item {} in list of protection-affecting items has too many parts, ignoring extra parts.",
                        itemString);

                try {
                    WhitelistedProtectionBaubles.add(item, Integer.parseInt(parts[2]));
                } catch (NumberFormatException e) {
                    AngerMod.LOGGER.error(
                            "Could not parse metadata value of item {} in list of protection-affecting items",
                            itemString);
                }
            } else {
                WhitelistedProtectionBaubles.add(item);
            }
        }
    }
}
