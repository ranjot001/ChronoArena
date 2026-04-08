package server;

import common.Constants;
import common.ItemState;
import common.ItemType;
import common.Position;
import common.ZoneStateModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class SpawnManager {
    private final Random random = new Random();
    private final AtomicInteger itemCounter = new AtomicInteger(1);

    private final List<Position> playerSpawns = new ArrayList<>();

    public SpawnManager() {
        playerSpawns.add(new Position(60, 60));
        playerSpawns.add(new Position(Constants.MAP_WIDTH - 120, 60));
        playerSpawns.add(new Position(60, Constants.MAP_HEIGHT - 120));
        playerSpawns.add(new Position(Constants.MAP_WIDTH - 120, Constants.MAP_HEIGHT - 120));
        playerSpawns.add(new Position(Constants.MAP_WIDTH / 2, 60));
        playerSpawns.add(new Position(Constants.MAP_WIDTH / 2, Constants.MAP_HEIGHT - 120));
        playerSpawns.add(new Position(60, Constants.MAP_HEIGHT / 2));
        playerSpawns.add(new Position(Constants.MAP_WIDTH - 120, Constants.MAP_HEIGHT / 2));
    }

    public Position getSpawnForPlayerIndex(int index) {
        Position base = playerSpawns.get(index % playerSpawns.size());
        return base.copy();
    }

    public ItemState spawnRandomItem(List<ZoneStateModel> zones) {
        for (int attempt = 0; attempt < 25; attempt++) {
            ItemType type = random.nextBoolean() ? ItemType.ENERGY : ItemType.FREEZE;
            int x = 80 + random.nextInt(Math.max(1, Constants.MAP_WIDTH - 160));
            int y = 80 + random.nextInt(Math.max(1, Constants.MAP_HEIGHT - 160));
            Position pos = new Position(x, y);

            if (isSafeItemPosition(pos, zones)) {
                return new ItemState(
                        "ITEM-" + itemCounter.getAndIncrement(),
                        type,
                        pos,
                        true,
                        System.currentTimeMillis()
                );
            }
        }

        return new ItemState(
                "ITEM-" + itemCounter.getAndIncrement(),
                ItemType.ENERGY,
                new Position(Constants.MAP_WIDTH / 2, Constants.MAP_HEIGHT / 2),
                true,
                System.currentTimeMillis()
        );
    }

    private boolean isSafeItemPosition(Position pos, List<ZoneStateModel> zones) {
        for (ZoneStateModel zone : zones) {
            int padding = 30;
            boolean nearZone =
                    pos.getX() >= zone.getX() - padding &&
                            pos.getX() <= zone.getX() + zone.getWidth() + padding &&
                            pos.getY() >= zone.getY() - padding &&
                            pos.getY() <= zone.getY() + zone.getHeight() + padding;

            if (nearZone) {
                return false;
            }
        }
        return true;
    }
}