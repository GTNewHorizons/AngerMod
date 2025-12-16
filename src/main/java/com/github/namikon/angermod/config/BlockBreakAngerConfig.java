package com.github.namikon.angermod.config;

import net.minecraft.block.Block;

import com.github.namikon.angermod.AngerMod;
import com.github.namikon.angermod.auxiliary.BlockSet;
import com.gtnewhorizon.gtnhlib.config.Config;

import cpw.mods.fml.common.registry.GameRegistry;

@Config(modid = AngerMod.MODID, category = "block-break-anger")
@Config.LangKey("angermod.config.block-break-anger")
@Config.Comment("Breaking blocks in the Nether or End angers the resident mobs.")
public class BlockBreakAngerConfig {

    @Config.Ignore
    public static final BlockSet netherBlacklistSet = new BlockSet();

    @Config.Ignore
    public static final BlockSet endBlacklistSet = new BlockSet();

    @Config.Comment("Breaking certain blocks will anger mobs.")
    @Config.DefaultBoolean(false)
    public static boolean enabled;

    @Config.Comment("The range at which endermen will get angered by broken blocks in the End.")
    @Config.DefaultInt(16)
    @Config.RangeInt(min = 2, max = 128)
    public static int endermanAggroRange;

    @Config.Comment("The range at which zombie pigmen will get angered by broken blocks in the Nether.")
    @Config.DefaultInt(16)
    @Config.RangeInt(min = 2, max = 128)
    public static int pigmanAggroRange;

    @Config.Comment("Define all Blocks here where Enderman should become angry when you break them.")
    @Config.DefaultStringList({ "gregtech:gt.blockores" })
    public static String[] endBlacklist;

    @Config.Comment("Define all Blocks here where Zombie Pigmen should become angry when you break them.")
    @Config.DefaultStringList({ "gregtech:gt.blockores" })
    public static String[] netherBlacklist;

    public static void reloadConfigs() {
        parseBlacklistedBlocks(BlockBreakAngerConfig.endBlacklist, BlockBreakAngerConfig.endBlacklistSet, "the End");
        parseBlacklistedBlocks(
                BlockBreakAngerConfig.netherBlacklist,
                BlockBreakAngerConfig.netherBlacklistSet,
                "the Nether");
    }

    private static void parseBlacklistedBlocks(String[] blockNames, BlockSet collection, String dimension) {
        collection.clear();

        for (String blockName : blockNames) {
            BlockBreakAngerConfig.parseBlockString(blockName, collection, dimension);
        }
    }

    private static void parseBlockString(String blockDataString, BlockSet blockSet, String dimension) {
        String[] blockInfo = blockDataString.split(":");

        if (blockInfo.length < 2) {
            AngerMod.LOGGER.error(
                    "BlockName {} in config for {} is invalid. Make sure you use full [domain]:[blockname] notation!",
                    blockDataString,
                    dimension);
            return;
        }

        final Block block = GameRegistry.findBlock(blockInfo[0], blockInfo[1]);

        if (blockInfo.length >= 3) {
            // Meta-aware entry
            if (blockInfo.length >= 4) AngerMod.LOGGER.error(
                    "BlockName {} in config for {} has too many parts. Extra parts have been ignored.",
                    blockDataString,
                    dimension);

            try {
                blockSet.add(block, Integer.parseInt(blockInfo[2]));
            } catch (NumberFormatException e) {
                AngerMod.LOGGER.error(
                        "BlockName {} in config for {} has unparseable metadata. The metadata must be a number.",
                        blockDataString,
                        dimension);
            }
        } else {
            // Wildcard entry
            blockSet.add(block);
        }
    }
}
