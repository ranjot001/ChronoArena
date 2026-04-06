package common;

public final class Constants {

    private Constants() {
    }

    public static final int MAP_WIDTH = 1000;
    public static final int MAP_HEIGHT = 700;

    public static final int PLAYER_SIZE = 28;
    public static final int PLAYER_SPEED = 8;

    public static final int SERVER_TICK_RATE = 20; // 20 ticks/sec
    public static final long TICK_MILLIS = 1000L / SERVER_TICK_RATE;

    public static final int MATCH_DURATION_SECONDS = 180;

    public static final int ZONE_CAPTURE_TIME_MS = 3000;
    public static final int ZONE_GRACE_TIME_MS = 5000;
    public static final int ZONE_POINT_AWARD_INTERVAL_MS = 1000;
    public static final int ZONE_POINT_AWARD = 5;

    public static final int ENERGY_ITEM_SCORE = 10;
    public static final int FREEZE_RANGE = 90;
    public static final int FREEZE_DURATION_MS = 2000;
    public static final int FREEZE_COOLDOWN_MS = 5000;

    public static final int ITEM_SPAWN_INTERVAL_MS = 4000;
    public static final int MAX_ITEMS_ON_MAP = 6;

    public static final int SNAPSHOT_BROADCAST_INTERVAL_MS = 100;

    public static final int UDP_PACKET_BUFFER_SIZE = 8192;
    public static final int TCP_SOCKET_TIMEOUT_MS = 0;

    public static final String MESSAGE_TYPE_JOIN_REQUEST = "JOIN_REQUEST";
    public static final String MESSAGE_TYPE_JOIN_RESPONSE = "JOIN_RESPONSE";
    public static final String MESSAGE_TYPE_GAME_SNAPSHOT = "GAME_SNAPSHOT";
    public static final String MESSAGE_TYPE_SERVER_NOTICE = "SERVER_NOTICE";
    public static final String MESSAGE_TYPE_CLIENT_DISCONNECT = "CLIENT_DISCONNECT";
}