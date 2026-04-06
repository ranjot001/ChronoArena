package common;

import java.io.Serializable;

public class ZoneStateModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String zoneId;
    private int x;
    private int y;
    private int width;
    private int height;
    private ZoneControlState state;
    private Integer ownerPlayerId;
    private Integer contenderPlayerId;
    private long captureStartTimeMs;
    private long graceExpiryTimeMs;
    private long lastPointAwardTimeMs;

    public ZoneStateModel() {
    }

    public ZoneStateModel(String zoneId, int x, int y, int width, int height) {
        this.zoneId = zoneId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.state = ZoneControlState.UNCLAIMED;
        this.ownerPlayerId = null;
        this.contenderPlayerId = null;
        this.captureStartTimeMs = 0L;
        this.graceExpiryTimeMs = 0L;
        this.lastPointAwardTimeMs = 0L;
    }

    public ZoneStateModel copy() {
        ZoneStateModel copy = new ZoneStateModel();
        copy.zoneId = this.zoneId;
        copy.x = this.x;
        copy.y = this.y;
        copy.width = this.width;
        copy.height = this.height;
        copy.state = this.state;
        copy.ownerPlayerId = this.ownerPlayerId;
        copy.contenderPlayerId = this.contenderPlayerId;
        copy.captureStartTimeMs = this.captureStartTimeMs;
        copy.graceExpiryTimeMs = this.graceExpiryTimeMs;
        copy.lastPointAwardTimeMs = this.lastPointAwardTimeMs;
        return copy;
    }

    public boolean contains(Position position) {
        if (position == null) {
            return false;
        }
        return position.getX() >= x
                && position.getX() <= x + width
                && position.getY() >= y
                && position.getY() <= y + height;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ZoneControlState getState() {
        return state;
    }

    public void setState(ZoneControlState state) {
        this.state = state;
    }

    public Integer getOwnerPlayerId() {
        return ownerPlayerId;
    }

    public void setOwnerPlayerId(Integer ownerPlayerId) {
        this.ownerPlayerId = ownerPlayerId;
    }

    public Integer getContenderPlayerId() {
        return contenderPlayerId;
    }

    public void setContenderPlayerId(Integer contenderPlayerId) {
        this.contenderPlayerId = contenderPlayerId;
    }

    public long getCaptureStartTimeMs() {
        return captureStartTimeMs;
    }

    public void setCaptureStartTimeMs(long captureStartTimeMs) {
        this.captureStartTimeMs = captureStartTimeMs;
    }

    public long getGraceExpiryTimeMs() {
        return graceExpiryTimeMs;
    }

    public void setGraceExpiryTimeMs(long graceExpiryTimeMs) {
        this.graceExpiryTimeMs = graceExpiryTimeMs;
    }

    public long getLastPointAwardTimeMs() {
        return lastPointAwardTimeMs;
    }

    public void setLastPointAwardTimeMs(long lastPointAwardTimeMs) {
        this.lastPointAwardTimeMs = lastPointAwardTimeMs;
    }
}