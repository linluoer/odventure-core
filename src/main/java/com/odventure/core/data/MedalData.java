package com.odventure.core.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class MedalData {
    private ItemStack embeddedItem;
    private String customName;
    private boolean contentLocked;
    private boolean nameLocked;

    public MedalData() {
        this(ItemStack.EMPTY, "", false, false);
    }

    public MedalData(ItemStack embeddedItem, String customName, boolean contentLocked, boolean nameLocked) {
        this.embeddedItem = embeddedItem == null ? ItemStack.EMPTY : embeddedItem;
        this.customName = customName == null ? "" : customName;
        this.contentLocked = contentLocked;
        this.nameLocked = nameLocked;
    }

    public ItemStack getEmbeddedItem() { return embeddedItem; }
    public String getCustomName() { return customName; }
    public boolean isContentLocked() { return contentLocked; }
    public boolean isNameLocked() { return nameLocked; }

    public void setEmbeddedItem(ItemStack stack) { this.embeddedItem = stack == null ? ItemStack.EMPTY : stack; }
    public void setCustomName(String name) { this.customName = name == null ? "" : name; }
    public void setContentLocked(boolean v) { this.contentLocked = v; }
    public void setNameLocked(boolean v) { this.nameLocked = v; }

    public boolean isEmpty() {
        return embeddedItem.isEmpty() && customName.isEmpty() && !contentLocked && !nameLocked;
    }

    public CompoundTag save(CompoundTag tag) {
        if (!embeddedItem.isEmpty()) {
            tag.put("EmbeddedItem", embeddedItem.save(new CompoundTag()));
        }
        tag.putString("CustomName", customName);
        tag.putBoolean("ContentLocked", contentLocked);
        tag.putBoolean("NameLocked", nameLocked);
        return tag;
    }

    public static MedalData load(CompoundTag tag) {
        ItemStack item = tag.contains("EmbeddedItem")
                ? ItemStack.of(tag.getCompound("EmbeddedItem"))
                : ItemStack.EMPTY;
        return new MedalData(
                item,
                tag.getString("CustomName"),
                tag.getBoolean("ContentLocked"),
                tag.getBoolean("NameLocked")
        );
    }
}
