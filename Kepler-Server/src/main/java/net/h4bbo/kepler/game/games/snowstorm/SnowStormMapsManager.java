package net.h4bbo.kepler.game.games.snowstorm;

import net.h4bbo.kepler.game.games.snowstorm.mapping.SnowStormItem;
import net.h4bbo.kepler.game.games.snowstorm.mapping.SnowStormMap;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormSpawn;
import net.h4bbo.kepler.log.Log;

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

            var spawnClusters = new ArrayList<SnowStormSpawn>();
            var spawnClusterPath = Path.of("tools", "snowstorm_maps", "arena_" + mapId + "_spawn_clusters.dat");

            if (spawnClusterPath.toFile().exists()) {
                for (String spawnClusterData : Files.readString(spawnClusterPath).split(Pattern.quote("|"))) {//Character.toString(13))) {
                    var spawnData = spawnClusterData.split(" ");

                    var x = Integer.parseInt(spawnData[0]);
                    var y = Integer.parseInt(spawnData[1]);
                    var radius = Integer.parseInt(spawnData[2]);
                    var minDistance = Integer.parseInt(spawnData[3]);

                    spawnClusters.add(new SnowStormSpawn(x, y, radius, minDistance));
                }
            }

            this.snowStormMapMaps.put(mapId, new SnowStormMap(mapId, mapData, itemList, getHeightMap(mapId), spawnClusters));
        } catch (IOException ex) {
            Log.getErrorLogger().error("Error when parsing map " + mapId + ": ", ex);
        }
    }

    private int getItemHeight(String spriteName) {
        switch (spriteName){
            case "sw_tree1":
            case "sw_tree2":
            case "sw_tree3":
            case "sw_tree4":
            case "block_basic3":
            case "obst_snowman":
            case "block_arch1":
            case "block_arch3":
            case "block_arch1b":
            case "block_arch3b":
                return 3;
            case "block_basic2":
            case "block_ice2":
                return 2;
            case "block_basic":
            //case "block_small":
            case "obst_duck":
            case "sw_fence":
            case "block_ice":
                return 1;
        }

        return 0;
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
        if (mapId == 1) {
            return "xxxxxxxxxxxxxxxxxxx00000xxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxx0000000xxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxx000000000xxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxx00000000000xxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxx0000000000000xxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxx000000000000000xxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxx00000000000000000xxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxx0000000000000000000xxxxxxxxxxxxxxxxxxx|xxxxxxxxxxx000000000000000000000xxxxxxxxxxxxxxxxxx|xxxxxxxxxx00000000000000000000000xxxxxxxxxxxxxxxxx|xxxxxxxxx0000000000000000000000000xxxxxxxxxxxxxxxx|xxxxxxxx000000000000000000000000000xxxxxxxxxxxxxxx|xxxxxxx00000000000000000000000000000xxxxxxxxxxxxxx|xxxxxx0000000000000000000000000000000xxxxxxxxxxxxx|xxxxx000000000000000000000000000000000xxxxxxxxxxxx|xxxx00000000000000000000000000000000000xxxxxxxxxxx|xxx0000000000000000000000000000000000000xxxxxxxxxx|xx000000000000000000000000000000000000000xxxxxxxxx|x00000000000000000000000000000000000000000xxxxxxxx|00000000000000000000xxxx0xxxxxx000000000000xxxxxxx|00000000000000000000xxxx0xxxxxxx000000000000xxxxxx|00000000000000000000xxxx0xxxxxxx0000000000000xxxxx|00000000000000000000xxx000000xxx00000000000000xxxx|x0000000000000000000xxx000000xxx000000000000000xxx|xx000000000000000000xxx0000000000000000000000000xx|xxx00000000000000000xxx000000xxx00000000000000000x|xxxx0000000000000000000000000xxx000000000000000000|xxxxx000000000000000xxx000000xxx000000000000000000|xxxxxx00000000000000xxxxxxx0xxxx000000000000000000|xxxxxxx0000000000000xxxxxxx0xxxx000000000000000000|xxxxxxxx0000000000000xxxxxx0xxx0000000000000000000|xxxxxxxxx00000000000000000000000000000000000000000|xxxxxxxxxx000000000000000000000000000000000000000x|xxxxxxxxxxx0000000000000000000000000000000000000xx|xxxxxxxxxxxx00000000000000000000000000000000000xxx|xxxxxxxxxxxxx000000000000000000000000000000000xxxx|xxxxxxxxxxxxxx0000000000000000000000000000000xxxxx|xxxxxxxxxxxxxxx00000000000000000000000000000xxxxxx|xxxxxxxxxxxxxxxx000000000000000000000000000xxxxxxx|xxxxxxxxxxxxxxxxx0000000000000000000000000xxxxxxxx|xxxxxxxxxxxxxxxxxx00000000000000000000000xxxxxxxxx|xxxxxxxxxxxxxxxxxxx000000000000000000000xxxxxxxxxx|xxxxxxxxxxxxxxxxxxxx0000000000000000000xxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxx00000000000000000xxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxx000000000000000xxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxx0000000000000xxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx00000000000xxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxx000000000xxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxx0000000xxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxx00000xxxxxxxxxxxxxxxxxx|";
        }

        if (mapId == 2) {
            return "xxxxxxxxxxxxxxxxxxx00000xxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxx0000000xxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxx000000000xxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxx00000000000xxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxx0000000000000xxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxx000000000000000xxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxx00000000000000000xxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxx0000000000000000000xxxxxxxxxxxxxxxxxxx|xxxxxxxxxxx000000000000000000000xxxxxxxxxxxxxxxxxx|xxxxxxxxxx00000000000000000000000xxxxxxxxxxxxxxxxx|xxxxxxxxx0000000000000000000000000xxxxxxxxxxxxxxxx|xxxxxxxx000000000000000000000000000xxxxxxxxxxxxxxx|xxxxxxx00000000000000000000000000000xxxxxxxxxxxxxx|xxxxxx0000000000000000000000000000000xxxxxxxxxxxxx|xxxxx000000000000000000000000000000000xxxxxxxxxxxx|xxxx00000000000000000000000000000000000xxxxxxxxxxx|xxx0000000000000000000000000000000000000xxxxxxxxxx|xx000000000000000000000000000000000000000xxxxxxxxx|x00000000000000000000000000000000000000000xxxxxxxx|0000000000000000000000000000000000000000000xxxxxxx|0000000000000000000xxxxxxxxxx0xxxxxx00000000xxxxxx|0000000000000000000xxxxxxxxxx0xxxxxxx00000000xxxxx|0000000000000000000xxxxxxxxxx0xxxxxxx000000000xxxx|x000000000000000000xxx000000000000xxx0000000000xxx|xx00000000000000000xxx000000000000xxx00000000000xx|xxx0000000000000000xxx000000000000xxx000000000000x|xxxx000000000000000xxx000000000000xxx0000000000000|xxxxx00000000000000000000000000000xxx0000000000000|xxxxxx0000000000000xxx000000000000xxxx000000000000|xxxxxxx000000000000xxx000000000000xxxxxxxxxx0xxxxx|xxxxxxxx00000000000xxx000000000000xxxxxxxxxx0xxxxx|xxxxxxxxx0000000000xxx0000000000000xxxxxxxxx0xxxxx|xxxxxxxxxx000000000xxxxxxxx0000000000000000000000x|xxxxxxxxxxx00000000xxxxxxxxx00000000000000000000xx|xxxxxxxxxxxx00000000xxxxxxxx0000000000000000000xxx|xxxxxxxxxxxxx000000000000xxx000000000000000000xxxx|xxxxxxxxxxxxxx00000000000xxx00000000000000000xxxxx|xxxxxxxxxxxxxxx0000000000xxx0000000000000000xxxxxx|xxxxxxxxxxxxxxxx000000000xxx000000000000000xxxxxxx|xxxxxxxxxxxxxxxxx00000000xxx00000000000000xxxxxxxx|xxxxxxxxxxxxxxxxxx0000000xxx0000000000000xxxxxxxxx|xxxxxxxxxxxxxxxxxxx000000xxx000000000000xxxxxxxxxx|xxxxxxxxxxxxxxxxxxxx00000xxx00000000000xxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxx0000xxx0000000000xxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxx000xxx000000000xxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxx00xxx00000000xxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx0xxx0000000xxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxx000000xxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxx00000xxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxx0000xxxxxxxxxxxxxxxxxx|";
        }

        if (mapId == 3) {
            return "xxxxxxxxxxxxxxxxxxx00000xxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxx0000000xxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxx000000000xxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxx00000000000xxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxx000000000000xxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxx0000000000000xxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxx00000000000xxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxx00000000000000xxxx0xxxxxxxxxxxxxxxxxxx|xxxxxxxxxxx0000000000000000xxx00xxxxxxxxxxxxxxxxxx|xxxxxxxxxx00000000000000000xxx0000xxxxxxxxxxxxxxxx|xxxxxxxxx000000000000000000xxx00000xxxxxxxxxxxxxxx|xxxxxxxx0000000000000000000xxx00000xxxxxxxxxxxxxxx|xxxxxxx00000000000000000000xxx000000xxxxxxxxxxxxxx|xxxxxx000000000000000000000xxx0000000xxxxxxxxxxxxx|xxxxx0000000000000000000000xxx00000000xxxxxxxxxxxx|xxxx00000000000000000000000xxx000000000xxxxxxxxxxx|xxx000000000000000000000000xxx0000000000xxxxxxxxxx|xx0000000000000000000000000xxx00000000000xxxxxxxxx|x00000000000000000000000000xxx000000000000xxxxxxxx|000000000000000000000000000xxx0000000000000xxxxxxx|000000000000000000000000000xxx00000000000000xxxxxx|000000000000000000000000000xxx000000000000000xxxxx|0000000000000000000000000000000000000000000000xxxx|x0000000000000000000000000000000000000000000000xxx|xx00000000000000000000000000x0000000000000000000xx|xxx000000000000000000000000xxx0000000000000000000x|xxxx00000000000000000000000x0000000000000000000000|xxxxx000000000000000000000000000000000000000000000|xxxxxx00000000000000000000000000000000000000000000|xxxxxxx00000000000000000000xxx00000000000000000000|xxxxxxxx0000000000000000000xxx00000000000000000000|xxxxxxxxx000000000000000000xxx00000000000000000000|xxxxxxxxxx00000000000000000xxx0000000000000000000x|xxxxxxxxxxx0000000000000000xxx000000000000000000xx|xxxxxxxxxxxx000000000000000xxx00000000000000000xxx|xxxxxxxxxxxxx00000000000000xxx0000000000000000xxxx|xxxxxxxxxxxxxx0000000000000xxx000000000000000xxxxx|xxxxxxxxxxxxxxx000000000000xxx00000000000000xxxxxx|xxxxxxxxxxxxxxxx00000000000xxx0000000000000xxxxxxx|xxxxxxxxxxxxxxxxx0000000000xxx000000000000xxxxxxxx|xxxxxxxxxxxxxxxxxx000000000xxx00000000000xxxxxxxxx|xxxxxxxxxxxxxxxxxxx00000000xxx0000000000xxxxxxxxxx|xxxxxxxxxxxxxxxxxxxx0000000xxx000000000xxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxx000000xxx00000000xxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxx00000xxx0000000xxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxx0000xxx000000xxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx000xxx00000xxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxx00xxx0000xxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxx0xxx000xxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|";
        }

        if (mapId == 7) {
            return "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx0xxxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx00xxxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx000xxxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx0000xxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxx0xxxxxxx00000xxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxx00xxxxxxx000000xxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxx000xxxxxxx0000000xxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxx0000xxxxxxx00000000xxxxxxxxxxxxxxxxxx|xxxxxxxxxxxx00000xxxxxxx000000000xxxxxxxxxxxxxxxxx|xxxxxxxxxxx000000xxxxxxx0000000000xxxxxxxxxxxxxxxx|xxxxxxxxxx0000000xxxxxxx00000000000xxxxxxxxxxxxxxx|xxxxxxxxx00000000xxxxxxx00000000000xxxxxxxxxxxxxxx|xxxxxxxx000000000xxxxxxx00000000000xxxxxxxxxxxxxxx|xxxxxxx0000000000xxxxxxx00000000000xxxxxxxxxxxxxxx|xxxxxx00000000000xxxxxxx00000000000xxx0xxxxxxxxxxx|xxxxx000000000000xxxxxxx00000000000xxx00xxxxxxxxxx|xxxx0000000000000xxxxxxx00000000000xxx000xxxxxxxxx|xxx00000000000000000000000000000000xxx0000xxxxxxxx|0x00000000000000000000000000000000000000000xxxxxxx|x0000000000000000xxx0xxx00000000000xxx000000xxxxxx|00000000000000000xx000xx00000000000xxx0000000xxxxx|00000000000000000000x00000000000000xxx00000000xxxx|x000000000000000000xxx0000000000000xxx000000000xxx|xx000000000000000000000000000000000xxx0000000000xx|xxx00000000000000000000000000000000xxx00000000000x|xxxx000000000000000x0x0000000000000xxx000000000000|xxxxx00000000000000xxx0000000000000xxx000000000000|xxxxxx0000000000000xxx0000000xxx0xxxxx000000000000|xxxxxxx000000000000xxx000000xxxx0xxxxx000000000000|xxxxxxxx00000000000xxx000000xxxx0xxxx0000000000000|xxxxxxxxx0000000000xxx000000xxx0000000000000000000|xxxxxxxxxx000000000xxx000000xxx000000000000000000x|xxxxxxxxxxx00000000xxx000000xxx00000000000000000xx|xxxxxxxxxxxx0000000xxx000000xxx0000000000000000xxx|xxxxxxxxxxxxx000000xxxxxxxxxxxx000000000000000xxxx|xxxxxxxxxxxxxx00000xxxxxxxxxxxx00000000000000xxxxx|xxxxxxxxxxxxxxx00000xxxxxxxxxxx0000000000000xxxxxx|xxxxxxxxxxxxxxxx000000000000xxx000000000000xxxxxxx|xxxxxxxxxxxxxxxxx0000000000000000000000000xxxxxxxx|xxxxxxxxxxxxxxxxxx00000000000000000000000xxxxxxxxx|xxxxxxxxxxxxxxxxxxx0000000000x0000000000xxxxxxxxxx|xxxxxxxxxxxxxxxxxxxx00000000xxx00000000xxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxx0000000xxx0000000xxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxx000000x00000000xxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxx0000000000000xxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxx00000000000xxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxx000xx0000xxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxx00xxxxxxxxxxxxxxxxxxxxxx|xxxxxxxxxxxxxxxxxxxxxxxxxxx0xxxxxxxxxxxxxxxxxxxxxx|";
        }
        
        return "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|" +
                "xxxxxxxxxxxxxxxxxx00000000xxxxxxxxxxxxxxxxxxxxxxxx|" +
                "xxxxxxxxxxxxxxxxx00000000000xxxxxxxxxxxxxxxxxxxxxx|" +
                "xxxxxxxxxxxxxx000000000000000xxxxxxxxxxxxxxxxxxxxx|" +
                "xxxxxxxxxxxxx00000000000000000xxxxxxxxxxxxxxxxxxxx|" +
                "xxxxxxxxxxxx0000000000000000000xxxxxxxxxxxxxxxxxxx|" +
                "xxxxxxxxxxx000000000000000000000xxxxxxxxxxxxxxxxxx|" +
                "xxxxxxxxxx00000000000000000000000xxxxxxxxxxxxxxxxx|" +
                "xxxxxxxxx0000000000000000000000000xxxxxxxxxxxxxxxx|" +
                "xxxxxxxx000000000000000000000000000xxxxxxxxxxxxxxx|" +
                "xxxxxxx00000000000000000000000000000xxxxxxxxxxxxxx|" +
                "xxxxxx0000000000000000000000000000000xxxxxxxxxxxxx|" +
                "xxxxx000000000000000000000000000000000xxxxxxxxxxxx|" +
                "xxxxx0000000000000000000000000000000000xxxxxxxxxxx|" +
                "xxxxx00000000000000000000000000000000000xxxxxxxxxx|" +
                "xxxxx000000000000000000000000000000000000xxxxxxxxx|" +
                "xxxx00000000000000000000000000000000000000xxxxxxxx|" +
                "xxxx000000000000000000000000000000000000000xxxxxxx|" +
                "xxxx0000000000000000000000000000000000000000xxxxxx|" +
                "xxxx00000000000000000000000000000000000000000xxxxx|" +
                "0xxx000000000000000000000000000000000000000000xxxx|" +
                "xxxx000000000000000000000000000000000000000000xxxx|" +
                "xxxx0000000000000000000000000000000000000000000xxx|" +
                "xxxx0000000000000000000000000000000000000000000xxx|" +
                "xxxx0000000000000000000000000000000000000000000xxx|" +
                "xxxxx000000000000000000000000000000000000000000xxx|" +
                "xxxxxx00000000000000000000000000000000000000000xxx|" +
                "xxxxxxx0000000000000000000000000000000000000000xxx|" +
                "xxxxxxxx000000000000000000000000000000000000000xxx|" +
                "xxxxxxxxx0000000000000000000000000000000000000xxxx|" +
                "xxxxxxxxxx000000000000000000000000000000000000xxxx|" +
                "xxxxxxxxxxx0000000000000000000000000000000000xxxxx|" +
                "xxxxxxxxxxxx00000000000000000000000000000000xxxxxx|" +
                "xxxxxxxxxxxxx000000000000000000000000000000xxxxxxx|" +
                "xxxxxxxxxxxxxx0000000000000000000000000000xxxxxxxx|" +
                "xxxxxxxxxxxxxxx00000000000000000000000000xxxxxxxxx|" +
                "xxxxxxxxxxxxxxxx0000000000000000000000000xxxxxxxxx|" +
                "xxxxxxxxxxxxxxxxx00000000000000000000000xxxxxxxxxx|" +
                "xxxxxxxxxxxxxxxxxx0000000000000000000000xxxxxxxxxx|" +
                "xxxxxxxxxxxxxxxxxxx00000000000000000000xxxxxxxxxxx|" +
                "xxxxxxxxxxxxxxxxxxxx000000000000000000xxxxxxxxxxxx|" +
                "xxxxxxxxxxxxxxxxxxxxx0000000000000000xxxxxxxxxxxxx|" +
                "xxxxxxxxxxxxxxxxxxxxxxx0000000000000xxxxxxxxxxxxxx|" +
                "xxxxxxxxxxxxxxxxxxxxxxxx000000000xxxxxxxxxxxxxxxxx|" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxx000000xxxxxxxxxxxxxxxxxx|" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx|";
    }

    public SnowStormMap getMap(int mapId) {
        return this.snowStormMapMaps.get(mapId);
    }
}
