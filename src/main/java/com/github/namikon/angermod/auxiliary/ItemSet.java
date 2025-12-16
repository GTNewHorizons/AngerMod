package com.github.namikon.angermod.auxiliary;

import java.util.HashSet;

import net.minecraft.item.Item;

import com.gtnewhorizon.gtnhlib.util.data.ItemMeta;

public class ItemSet {

    private final HashSet<Item> wildcards = new HashSet<>();
    private final HashSet<ItemMeta> metaAware = new HashSet<>();

    public void add(Item item) {
        wildcards.add(item);
    }

    public void add(Item item, int metadata) {
        metaAware.add(new ItemMeta(item, metadata));
    }

    public boolean contains(Item item, int metadata) {
        return wildcards.contains(item) || metaAware.contains(new ItemMeta(item, metadata));
    }

    public void clear() {
        wildcards.clear();
        metaAware.clear();
    }
}
