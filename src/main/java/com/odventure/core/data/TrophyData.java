package com.odventure.core.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class TrophyData {
    public enum ContentType { NONE, BLOCK, ITEM, ENTITY }

    private ContentType contentType;
    @Nullable private ResourceLocation contentId;
    private String customName;
    private boolean contentLocked;
    private boolean nameLocked;

    private float displayScale;
    private float displayYaw;
    private float displayY;

    public TrophyData() {
        this(ContentType.NONE, null, "", false, false, 1f, 0f, 0f);
    }

    public TrophyData(ContentType contentType, @Nullable ResourceLocation contentId,
                      String customName, boolean contentLocked, boolean nameLocked,
                      float displayScale, float displayYaw, float displayY) {
        this.contentType = contentType == null ? ContentType.NONE : contentType;
        this.contentId = contentId;
        this.customName = customName == null ? "" : customName;
        this.contentLocked = contentLocked;
        this.nameLocked = nameLocked;
        this.displayScale = displayScale;
        this.displayYaw = displayYaw;
        this.displayY = displayY;
    }

    public ContentType getContentType() { return contentType; }
    @Nullable public ResourceLocation getContentId() { return contentId; }
    public String getCustomName() { return customName; }
    public boolean isContentLocked() { return contentLocked; }
    public boolean isNameLocked() { return nameLocked; }
    public float getDisplayScale() { return displayScale; }
    public float getDisplayYaw() { return displayYaw; }
    public float getDisplayY() { return displayY; }

    public void setContent(ContentType type, @Nullable ResourceLocation id) {
        this.contentType = type == null ? ContentType.NONE : type;
        this.contentId = id;
    }
    public void setCustomName(String name) { this.customName = name == null ? "" : name; }
    public void setContentLocked(boolean v) { this.contentLocked = v; }
    public void setNameLocked(boolean v) { this.nameLocked = v; }
    public void setDisplayScale(float v) { this.displayScale = v; }
    public void setDisplayYaw(float v) { this.displayYaw = v; }
    public void setDisplayY(float v) { this.displayY = v; }

    public boolean isEmpty() {
        return contentType == ContentType.NONE && !contentLocked && !nameLocked && customName.isEmpty();
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putString("ContentType", contentType.name());
        if (contentId != null) tag.putString("ContentId", contentId.toString());
        tag.putString("CustomName", customName);
        tag.putBoolean("ContentLocked", contentLocked);
        tag.putBoolean("NameLocked", nameLocked);
        tag.putFloat("DisplayScale", displayScale);
        tag.putFloat("DisplayYaw", displayYaw);
        tag.putFloat("DisplayY", displayY);
        return tag;
    }

    public static TrophyData load(CompoundTag tag) {
        ContentType type = ContentType.NONE;
        if (tag.contains("ContentType")) {
            try {
                type = ContentType.valueOf(tag.getString("ContentType"));
            } catch (IllegalArgumentException ignored) {}
        }
        ResourceLocation id = tag.contains("ContentId")
                ? ResourceLocation.tryParse(tag.getString("ContentId"))
                : null;
        float scale = tag.contains("DisplayScale") ? tag.getFloat("DisplayScale") : 1f;
        float yaw = tag.getFloat("DisplayYaw");
        float y = tag.getFloat("DisplayY");
        return new TrophyData(
                type, id,
                tag.getString("CustomName"),
                tag.getBoolean("ContentLocked"),
                tag.getBoolean("NameLocked"),
                scale, yaw, y
        );
    }
}
