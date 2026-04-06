package common;

import java.io.Serializable;

public class ItemState implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemId;
    private ItemType itemType;
    private Position position;
    private boolean active;
    private long spawnedAtMs;

    public ItemState() {
    }

    public ItemState(String itemId, ItemType itemType, Position position, boolean active, long spawnedAtMs) {
        this.itemId = itemId;
        this.itemType = itemType;
        this.position = position;
        this.active = active;
        this.spawnedAtMs = spawnedAtMs;
    }

    public ItemState copy() {
        return new ItemState(
                itemId,
                itemType,
                position == null ? null : position.copy(),
                active,
                spawnedAtMs
        );
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getSpawnedAtMs() {
        return spawnedAtMs;
    }

    public void setSpawnedAtMs(long spawnedAtMs) {
        this.spawnedAtMs = spawnedAtMs;
    }
}
