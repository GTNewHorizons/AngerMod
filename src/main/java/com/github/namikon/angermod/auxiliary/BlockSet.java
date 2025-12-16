package com.github.namikon.angermod.auxiliary;

import java.util.HashSet;

import net.minecraft.block.Block;

import com.gtnewhorizon.gtnhlib.util.data.BlockMeta;

public class BlockSet {

    private final HashSet<Block> wildcards = new HashSet<>();
    private final HashSet<BlockMeta> metaAware = new HashSet<>();

    public void add(Block block) {
        wildcards.add(block);
    }

    public void add(Block block, int metadata) {
        metaAware.add(new BlockMeta(block, metadata));
    }

    public boolean contains(Block block, int metadata) {
        return wildcards.contains(block) || metaAware.contains(new BlockMeta(block, metadata));
    }

    public void clear() {
        wildcards.clear();
        metaAware.clear();
    }
}
