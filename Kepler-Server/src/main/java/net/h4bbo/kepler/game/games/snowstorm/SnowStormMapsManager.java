package org.alexdev.kepler.game.games.snowstorm;

import org.alexdev.kepler.game.games.snowstorm.mapping.SnowStormItem;
import org.alexdev.kepler.game.games.snowstorm.mapping.SnowStormMap;
import org.alexdev.kepler.game.games.snowstorm.util.SnowStormItemProperties;
import org.alexdev.kepler.log.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SnowStormMapsManager {
    private static SnowStormMapsManager instance;
    private Map<Integer, SnowStormMap> snowStormMapMaps;

    public SnowStormMapsManager() {
        this.snowStormMapMaps = new HashMap<>();

        for (int i = 1; i <= 7; i++) {
            parseMap(i);
        }
    }

    private void parseMap(int mapId) {
        var filePath = Path.of("tools", "snowstorm_maps", "arena_" + mapId + ".dat");

        if (!filePath.toFile().exists()) {
            return;
        }

        try {
            var mapData = Files.readString(filePath);
            var itemList = new ArrayList<SnowStormItem>();

            for (var itemLine : mapData.split(Character.toString(13))) {
                var itemData = itemLine.split(" ");

                var item = new SnowStormItem(
                        itemData[0],
                        itemData[1],
                        Integer.parseInt(itemData[2]),
                        Integer.parseInt(itemData[3]),
                        Integer.parseInt(itemData[4]),
                        Integer.parseInt(itemData[5]),
                        getItemHeight(itemData[1])
                );

                itemList.add(item);
            }

            var snowmachineDataPath = Path.of("tools", "snowstorm_maps", "arena_" + mapId + "_snowmachines.dat");

            if (snowmachineDataPath.toFile().exists()) {
                var snowmachineFileContents = Files.readString(snowmachineDataPath);

                for (var snowmachineData : snowmachineFileContents.split(Character.toString(13))) {
                    var itemData = snowmachineData.split(" ");

                    var item = new SnowStormItem("", "snowball_machine", Integer.parseInt(itemData[0]), Integer.parseInt(itemData[1]), 0, 0, 1);
                    itemList.add(item);

                    item = new SnowStormItem("", "snowball_machine_hidden", Integer.parseInt(itemData[0]) + 1, Integer.parseInt(itemData[1]), 0, 0, 1);
                    itemList.add(item);

                    item = new SnowStormItem("", "snowball_machine_hidden", Integer.parseInt(itemData[0]) + 2, Integer.parseInt(itemData[1]), 0, 0, 1);
                    itemList.add(item);
                }
            }

            var spawnClusters = new ArrayList<SnowStormMap.SpawnCluster>();
            var spawnClusterPath = Path.of("tools", "snowstorm_maps", "arena_" + mapId + "_spawn_clusters.dat");

            if (spawnClusterPath.toFile().exists()) {
                for (String spawnClusterData : Files.readString(spawnClusterPath).split(Pattern.quote("|"))) {//Character.toString(13))) {
                    var spawnData = spawnClusterData.split(" ");

                    var x = Integer.parseInt(spawnData[0]);
                    var y = Integer.parseInt(spawnData[1]);
                    var radius = Integer.parseInt(spawnData[2]);
                    var minDistance = Integer.parseInt(spawnData[3]);

                    spawnClusters.add(new SnowStormMap.SpawnCluster(x, y, radius, minDistance));
                }
            }

            this.snowStormMapMaps.put(mapId, new SnowStormMap(mapId, mapData, itemList, getHeightMap(mapId), spawnClusters));
        } catch (IOException ex) {
            Log.getErrorLogger().error("Error when parsing map " + mapId + ": ", ex);
        }
    }

    private int getItemHeight(String spriteName) {
        return SnowStormItemProperties.getWalkableHeight(spriteName);
    }

    public static SnowStormMapsManager getInstance() {
        if (instance == null) {
            instance = new SnowStormMapsManager();
        }

        return instance;
    }

    public static void reset() {
        instance = null;
        getInstance();
    }

    public String getHeightMap(int mapId) {
        // Try loading from external file first
        String heightMap = loadHeightMapFromFile(mapId);
        if (heightMap != null) {
            return heightMap;
        }

        // Fallback to hardcoded values
        return getDefaultHeightMap(mapId);
    }

    private String loadHeightMapFromFile(int mapId) {
        var filePath = Path.of("tools", "snowstorm_maps", "arena_" + mapId + "_heightmap.txt");

        if (!filePath.toFile().exists()) {
            return null;
        }

        try {
            return Files.readString(filePath);
        } catch (IOException ex) {
            Log.getErrorLogger().error("Error loading height map for arena " + mapId + ": ", ex);
            return null;
        }
    }

    private String getDefaultHeightMap(int mapId) {
        switch (mapId) {
            case 1:
                return "xxxxxxxxxxxxxxxxxxxxx000xxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxx00000xxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxx0000000xxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxx000000000xxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxx00000000000xxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxx0000000000000xxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxx000000000000000xxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxx00000000000000000xxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxx0000000000000000000xxxxxxxxxxxxxxxxxx|xxxxxxxxxxxx000000000000000000000xxxxxxxxxxxxxxxxx|xxxxxxxxxxx00000000000000000000000xxxxxxxxxxxxxxxx|xxxxxxxxxx0000000000000000000000000xxxxxxxxxxxxxxx|xxxxxxxxx000000000000000000000000000xxxxxxxxxxxxxx|xxxxxxxx00000000000000000000000000000xxxxxxxxxxxxx|xxxxxxx0000000000000000000000000000000xxxxxxxxxxxx|xxxxxx000000000000000000000000000000000xxxxxxxxxxx|xxxxxxx000000000000000000000000000000000xxxxxxxxxx|xxxxxxx0000000000000000000000000000000000xxxxxxxxx|xxxxx0000000000000000000000000000000000000xxxxxxxx|x0000000000000000000xxxx0xxxxxx000000000000xxxxxxx|x0000000000000000000xxxx0xxxxxxx000000000000xxxxxx|x0000000000000000000xxxx0xxxxxxx0000000000000xxxxx|x0000000000000000000xxx000000xxx00000000000000xxxx|x0000000000000000000xxx000000xxx000000000000000xxx|xx000000000000000000xxx0000000000000000000000000xx|xxx00000000000000000xxx000000xxx00000000000000000x|xxxx0000000000000000000000000xxx000000000000000000|xxxxx000000000000000xxx000000xxx000000000000000000|xxxxxx00000000000000xxxxxxx0xxxx000000000000000000|xxxxxxx0000000000000xxxxxxx0xxxx000000000000000000|xxxxxxxx0000000000000xxxxxx0xxx0000000000000000000|xxxxxxxxx00000000000000000000000000000000000000000|xxxxxxxxxx000000000000000000000000000000000000000x|xxxxxxxxxxx0000000000000000000000000000000000000xx|xxxxxxxxxxxx00000000000000000000000000000000xxxxxx|xxxxxxxxxxxxxx00000000000000000000000000000xxxxxxx|xxxxxxxxxxxxxxx000000000000000000000000000xxxxxxxx|xxxxxxxxxxxxxxxx000000000000000000000000000xxxxxxx|xxxxxxxxxxxxxxxxx00000000000000000000000xxxxxxxxxx|xxxxxxxxxxxxxxxxx0000000000000000000000xxxxxxxxxxx|xxxxxxxxxxxxxxxxxx00000000000000000000xxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxx000000000000000000xxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxx0000000000000000xxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxx00000000000000xxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxx000000000000xxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxx0000000000xxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx00000000xxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxx000000xxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxx000xxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxx00xxxxxxxxxxxxxxxxxxxxx|";
            case 2:
                return "xxxxxxxxxxxxxxxxxxxx0000xxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxx00000xxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxx0000000xxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxx000000000xxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxx00000000000xxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxx0000000000000xxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxx000000000000000xxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxx00000000000000000xxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxx0000000000000000000xxxxxxxxxxxxxxxxxx|xxxxxxxxxxxx000000000000000000000xxxxxxxxxxxxxxxxx|xxxxxxxxxxx00000000000000000000000xxxxxxxxxxxxxxxx|xxxxxxxxxx0000000000000000000000000xxxxxxxxxxxxxxx|xxxxxxxxx000000000000000000000000000xxxxxxxxxxxxxx|xxxxxxxx00000000000000000000000000000xxxxxxxxxxxxx|xxxxxxx0000000000000000000000000000000xxxxxxxxxxxx|xxxxxx000000000000000000000000000000000xxxxxxxxxxx|xxxxx00000000000000000000000000000000000xxxxxxxxxx|xxxx0000000000000000000000000000000000000xxxxxxxxx|xxx000000000000000000000000000000000000000xxxxxxxx|xx00000000000000000000000000000000000000000xxxxxxx|xxx0000000000000000xxxxxxxxxx0xxxxxx00000000xxxxxx|xx00000000000000000xxxxxxxxxx0xxxxxxx00000000xxxxx|x000000000000000000xxxxxxxxxx0xxxxxxx000000000xxxx|xx00000000000000000xxx000000000000xxx0000000000xxx|xxx0000000000000000xxx000000000000xxx00000000000xx|xxxx000000000000000xxx000000000000xxx000000000000x|xxxxx00000000000000xxx000000000000xxx0000000000000|xxxxxx0000000000000000000000000000xxx0000000000000|xxxxxxx000000000000xxx000000000000xxxx000000000000|xxxxxxxx00000000000xxx000000000000xxxxxxxxxx0xxxxx|xxxxxxxxx0000000000xxx000000000000xxxxxxxxxx0xxxxx|xxxxxxxxxx000000000xxx0000000000000xxxxxxxxx0xxxxx|xxxxxxxxxxx00000000xxxxxxxx0000000000000000000xxxx|xxxxxxxxxxxx0000000xxxxxxxxx00000000000000000xxxxx|xxxxxxxxxxxxx0000000xxxxxxxx0000000000000000xxxxxx|xxxxxxxxxxxxxx00000000000xxx0000000000000000xxxxxx|xxxxxxxxxxxxxxx0000000000xxx00000000000000000xxxxx|xxxxxxxxxxxxxxxx000000000xxx0000000000000000xxxxxx|xxxxxxxxxxxxxxxxx00000000xxx0000000000000xxxxxxxxx|xxxxxxxxxxxxxxxxxx0000000xxx00000000000xxxxxxxxxxx|xxxxxxxxxxxxxxxxxxx000000xxx0000000000xxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxx00000xxx000000000xxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxx0000xxx00000000xxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxx000xxx0000000xxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxx00xxx000000xxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx0xxx00000xxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxx0000xxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxx000xxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxx00xxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxx0xxxxxxxxxxxxxxxxxxxxx|";
            case 3:
                return "xxxxxxxxxxxxxxxxxxx00000xxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxx0000000xxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxx000000000xxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxx00000000000xxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxx000000000000xxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxx0000000000000xxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxx0000000000xxxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxx00000000000000xxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxx0000000000000000xxx00xxxxxxxxxxxxxxxxxx|xxxxxxxxxx00000000000000000xxx0000xxxxxxxxxxxxxxxx|xxxxxxxxx000000000000000000xxx00000xxxxxxxxxxxxxxx|xxxxxxxx0000000000000000000xxx00000xxxxxxxxxxxxxxx|xxxxxxx00000000000000000000xxx000000xxxxxxxxxxxxxx|xxxxxx000000000000000000000xxx0000000xxxxxxxxxxxxx|xxxxx0000000000000000000000xxx00000000xxxxxxxxxxxx|xxxx00000000000000000000000xxx000000000xxxxxxxxxxx|xxx000000000000000000000000xxx0000000000xxxxxxxxxx|xx0000000000000000000000000xxx00000000000xxxxxxxxx|x00000000000000000000000000xxx000000000000xxxxxxxx|000000000000000000000000000xxx0000000000000xxxxxxx|000000000000000000000000000xxx00000000000000xxxxxx|000000000000000000000000000xxx000000000000000xxxxx|0000000000000000000000000000000000000000000000xxxx|x0000000000000000000000000000000000000000000000xxx|xx00000000000000000000000000x0000000000000000000xx|xxx000000000000000000000000xxx0000000000000000000x|xxxx00000000000000000000000x0000000000000000000000|xxxxx000000000000000000000000000000000000000000000|xxxxxx00000000000000000000000000000000000000000000|xxxxxxx00000000000000000000xxx0000000000000000000x|xxxxxxxx0000000000000000000xxx000000000000000000xx|xxxxxxxxx000000000000000000xxx00000000000000000xxx|xxxxxxxxxx00000000000000000xxx0000000000000000xxxx|xxxxxxxxxxx0000000000000000xxx000000000000000xxxxx|xxxxxxxxxxxx000000000000000xxx00000000000000xxxxxx|xxxxxxxxxxxxx00000000000000xxx0000000000000xxxxxxx|xxxxxxxxxxxxxx0000000000000xxx000000000000xxxxxxxx|xxxxxxxxxxxxxxx000000000000xxx00000000000xxxxxxxxx|xxxxxxxxxxxxxxxx00000000000xxx0000000000xxxxxxxxxx|xxxxxxxxxxxxxxxxx0000000000xxx000000000xxxxxxxxxxx|xxxxxxxxxxxxxxxxxx000000000xxx00000000xxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxx00000000xxx0000000xxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxx0000000xxx000000xxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxx000000xxx00000xxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxx00000xxx0000xxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx000xxx0000xxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxx00xxx00000xxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxx0xxx0000xxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx000xxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|";
            case 5:
                return "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxx00000000xxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxx00000000000xxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxx0000000000000xxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxx000000000000000xxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxx00000000000000000xxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxx0000000000000000000xxxxxxxxxxxxxxxxxx|xxxxxxxxxxxx000000000000000000000xxxxxxxxxxxxxxxxx|xxxxxxxxxxx00000000000000000000000xxxxxxxxxxxxxxxx|xxxxxxxxxxx000000000000000000000000xxxxxxxxxxxxxxx|xxxxxxxxxx00000000000000000000000000xxxxxxxxxxxxxx|xxxxxxxxxx000000000000000000000000000xxxxxxxxxxxxx|xxxxxxx0000000000000000000000000000000xxxxxxxxxxxx|xxxxx0000000000000000000000000000000000xxxxxxxxxxx|xxxxx00000000000000000000000000000000000xxxxxxxxxx|xxxxx000000000000000000000000000000000000xxxxxxxxx|xxxx00000000000000000000000000000000000000xxxxxxxx|xxxx000000000000000000000000000000000000000xxxxxxx|xxxx0000000000000000000000000000000000000000xxxxxx|xxxx00000000000000000000000000000000000000000xxxxx|0xxx000000000000000000000000000000000000000000xxxx|xxxx000000000000000000000000000000000000000000xxxx|xxxx0000000000000000000000000000000000000000000000|xxxx0000000000000000000000000000000000000000000000|xxxx0000000000000000000000000000000000000000000000|xxxxx000000000000000000000000000000000000000000000|xxxxxx00000000000000000000000000000000000000000000|xxxxxxx00000000000000000000000000000000000000000xx|xxxxxxxx0000000000000000000000000000000000000000xx|xxxxxxxxx0000000000000000000000000000000000000xxxx|xxxxxxxxxx000000000000000000000000000000000000xxxx|xxxxxxxxxxx0000000000000000000000000000000000xxxxx|xxxxxxxxxxxx00000000000000000000000000000000xxxxxx|xxxxxxxxxxxxx000000000000000000000000000000xxxxxxx|xxxxxxxxxxxxxx0000000000000000000000000000xxxxxxxx|xxxxxxxxxxxxxxx00000000000000000000000000xxxxxxxxx|xxxxxxxxxxxxxxxx0000000000000000000000000xxxxxxxxx|xxxxxxxxxxxxxxxxx00000000000000000000000xxxxxxxxxx|xxxxxxxxxxxxxxxxxx0000000000000000000000xxxxxxxxxx|xxxxxxxxxxxxxxxxxxx00000000000000000000xxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxx000000000000000000xxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxx0000000000000000xxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxx0000000000000xxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx000000000xxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxx0000000xxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxx00000xxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|";
            case 6:
                return "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxx00000000xxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxx0000000000xxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxx00000000000000xxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxx00000000000000000xxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxx000000000000000000xxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxx00000000000000000000xxxxxxxxxxxxxxxxxx|xxxxxxxxxxx0000000000000000000000xxxxxxxxxxxxxxxxx|xxxxxxxxxx000000000000000000000000xxxxxxxxxxxxxxxx|xxxxxxxxx00000000000000000000000000xxxxxxxxxxxxxxx|xxxxxxxx0000000000000000000000000000xxxxxxxxxxxxxx|xxxxxxx000000000000000000000000000000xxxxxxxxxxxxx|xxxxxx00000000000000000000000000000000xxxxxxxxxxxx|xxxxxx000000000000000000000000000000000xxxxxxxxxxx|xxxxxx0000000000000000000000000000000000xxxxxxxxxx|xxxxx000000000000000000000000000000000000xxxxxxxxx|xxxx00000000000000000000000000000000000000xxxxxxxx|xxxx000000000000000000000000000000000000000xxxxxxx|xxxx0000000000000000000000000000000000000000xxxxxx|xxxx00000000000000000000000000000000000000000xxxxx|0xxx000000000000000000000000000000000000000000xxxx|xxxx000000000000000000000000000000000000000000xxxx|xxxx0000000000000000000000000000000000000000000xxx|xxxxx000000000000000000000000000000000000000000xxx|xxxx000000000000000000000000000000000000000000000x|xxxxxx0000000000000000000000000000000000000000000x|xxxxxxx000000000000000000000000000000000000000000x|xxxxxxxx0000000000000000000000000000000000000000xx|xxxxxxxxx000000000000000000000000000000000000000xx|xxxxxxxxxx000000000000000000000000000000000000xxxx|xxxxxxxxxxx0000000000000000000000000000000000xxxxx|xxxxxxxxxxxx000000000000000000000000000000000xxxxx|xxxxxxxxxxxx00000000000000000000000000000000xxxxxx|xxxxxxxxxxxxx000000000000000000000000000000xxxxxxx|xxxxxxxxxxxxxx0000000000000000000000000000xxxxxxxx|xxxxxxxxxxxxxx000000000000000000000000000xxxxxxxxx|xxxxxxxxxxxxxxxx000000000000000000000000xxxxxxxxxx|xxxxxxxxxxxxxxxxxx000000000000000000000xxxxxxxxxxx|xxxxxxxxxxxxxxxxxxx000000000000000000000xxxxxxxxxx|xxxxxxxxxxxxxxxxxxxx0000000000000000000xxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxx0000000000000000xxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxx0000000000000xxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxx0000000000xxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx00000000xxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxx000000xxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxx0000xxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|";
            case 7:
                return "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx0xxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx00xxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx000xxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx0000xxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxx0xxxxxxx00000xxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxx00xxxxxxx000000xxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxx000xxxxxxx0000000xxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxx0000xxxxxxx00000000xxxxxxxxxxxxxxxxxx|xxxxxxxxxxxx00000xxxxxxx000000000xxxxxxxxxxxxxxxxx|xxxxxxxxxxx000000xxxxxxx0000000000xxxxxxxxxxxxxxxx|xxxxxxxxxx0000000xxxxxxx00000000000xxxxxxxxxxxxxxx|xxxxxxxxx00000000xxxxxxx00000000000xxxxxxxxxxxxxxx|xxxxxxxx000000000xxxxxxx00000000000xxxxxxxxxxxxxxx|xxxxxxx0000000000xxxxxxx00000000000xxxxxxxxxxxxxxx|xxxxxxxx000000000xxxxxxx00000000000xxx0xxxxxxxxxxx|xxxxxx00000000000xxxxxxx00000000000xxx00xxxxxxxxxx|xxxxxx00000000000xxxxxxx00000000000xxx000xxxxxxxxx|xxxxxx00000000000000000000000000000xxx0000xxxxxxxx|xxxxx00000000000000000000000000000000000000xxxxxxx|xxxxxx00000000000xxx0xxx00000000000xxx000000xxxxxx|xxxxxx00000000000xx000xx00000000000xxx0000000xxxxx|xxxxxx00000000000000x00000000000000xxx00000000xxxx|xxxxxxx000000000000xxx0000000000000xxx000000000xxx|xxxxxx00000000000000000000000000000xxx0000000000xx|xxxx0000000000000000000000000000000xxx00000000000x|xxxxx00000000000000x0x0000000000000xxx000000000000|xxxxxx0000000000000xxx0000000000000xxx000000000000|xxxxxxx000000000000xxx0000000xxx0xxxxx000000000000|xxxxxxxx00000000000xxx000000xxxx0xxxxx00000000000x|xxxxxxxxx0000000000xxx000000xxxx0xxxx00000000000xx|xxxxxxxxxx000000000xxx000000xxx0000000000000000xxx|xxxxxxxxxxx00000000xxx000000xxx000000000000000xxxx|xxxxxxxxxxxx0000000xxx000000xxx00000000000000xxxxx|xxxxxxxxxxxxx000000xxx000000xxx0000000000000xxxxxx|xxxxxxxxxxxxxx00000xxxxxxxxxxxx0000000000000xxxxxx|xxxxxxxxxxxxxxx0000xxxxxxxxxxxx00000000000000xxxxx|xxxxxxxxxxxxxxxx0000xxxxxxxxxxx0000000000000xxxxxx|xxxxxxxxxxxxxxxxx00000000000xxx00000000000xxxxxxxx|xxxxxxxxxxxxxxxxxx00000000000000000000000xxxxxxxxx|xxxxxxxxxxxxxxxxxxx000000000000000000000xxxxxxxxxx|xxxxxxxxxxxxxxxxxxxx000000000x000000000xxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxx0000000xxx0000000xxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxx000000xxx000000xxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxx00000x00000xxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx000000000xxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxx0000000xxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxx00xx00xxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxx0xxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|";
            default:
                // Default map (map 4)
                return "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxx00000000xxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxx0000000000xxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxx000000000000xxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxx0000000000000000xxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxx000000000000000000xxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxx00000000000000000000xxxxxxxxxxxxxxxxxx|xxxxxxxxxxx0000000000000000000000xxxxxxxxxxxxxxxxx|xxxxxxxxxx000000000000000000000000xxxxxxxxxxxxxxxx|xxxxxxxxx00000000000000000000000000xxxxxxxxxxxxxxx|xxxxxxxx0000000000000000000000000000xxxxxxxxxxxxxx|xxxxxxx000000000000000000000000000000xxxxxxxxxxxxx|xxxxxx00000000000000000000000000000000xxxxxxxxxxxx|xxxxxx000000000000000000000000000000000xxxxxxxxxxx|xxxxxx0000000000000000000000000000000000xxxxxxxxxx|xxxxx000000000000000000000000000000000000xxxxxxxxx|xxxx00000000000000000000000000000000000000xxxxxxxx|xxxx000000000000000000000000000000000000000xxxxxxx|xxxx0000000000000000000000000000000000000000xxxxxx|xxxx00000000000000000000000000000000000000000xxxxx|0xxx000000000000000000000000000000000000000000xxxx|xxxx000000000000000000000000000000000000000000xxxx|xxxx0000000000000000000000000000000000000000000xxx|xxxxx000000000000000000000000000000000000000000xxx|xxxxx000000000000000000000000000000000000000000xxx|xxxxxx00000000000000000000000000000000000000000xxx|xxxxxxx0000000000000000000000000000000000000000xxx|xxxxxxxx000000000000000000000000000000000000000xxx|xxxxxxxxx00000000000000000000000000000000000000xxx|xxxxxxxxxx000000000000000000000000000000000000xxxx|xxxxxxxxxxx00000000000000000000000000000000000xxxx|xxxxxxxxxxxx000000000000000000000000000000000xxxxx|xxxxxxxxxxxxx0000000000000000000000000000000xxxxxx|xxxxxxxxxxxxxx00000000000000000000000000000xxxxxxx|xxxxxxxxxxxxxxx000000000000000000000000000xxxxxxxx|xxxxxxxxxxxxxxxx0000000000000000000000000xxxxxxxxx|xxxxxxxxxxxxxxxxx00000000000000000000000xxxxxxxxxx|xxxxxxxxxxxxxxxxxx000000000000000000000xxxxxxxxxxx|xxxxxxxxxxxxxxxxxxx0000000000000000000xxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxx00000000000000000xxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxx000000000000000xxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxx00000000000000xxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxx00000000000xxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx000000000xxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxx000000xxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|";
        }
    }

    public SnowStormMap getMap(int mapId) {
        return this.snowStormMapMaps.get(mapId);
    }
}
