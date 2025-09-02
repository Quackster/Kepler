package net.h4bbo.kepler.game.games.snowstorm.mapping;

import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormSpawn;
import net.h4bbo.kepler.game.pathfinder.Position;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SnowStormMap {
    private final int mapId;
    private final ArrayList<SnowStormItem> itemList;
    private final ArrayList<SnowStormSpawn> spawnClusters;
    private final String compiledItems;
    private String heightMap;
    private int mapSizeY;
    private int mapSizeX;
    private SnowStormTile[][] tiles;

    public SnowStormMap(int mapId, String compiledItems, ArrayList<SnowStormItem> itemList, String heightMap, ArrayList<SnowStormSpawn> spawnClusters) {
        this.mapId = mapId;
        this.compiledItems = compiledItems;
        this.itemList = itemList;
        this.heightMap = heightMap;
        this.spawnClusters = spawnClusters;
        this.parseHeightMap();
    }

    public void parseHeightMap() {
        String[] lines = this.heightMap.split(Pattern.quote("|"));

        this.mapSizeY = lines.length;
        this.mapSizeX = lines[0].length();

        this.tiles = new SnowStormTile[this.mapSizeX][this.mapSizeY];

        StringBuilder temporaryHeightmap = new StringBuilder();

        for (int y = 0; y < this.mapSizeY; y++) {
            String line = lines[y];

            for (int x = 0; x < this.mapSizeX; x++) {
                String tile = Character.toString(line.charAt(x));

                var position = new Position(x, y);
                var snowStormTile = new SnowStormTile(x, y,
                        tile.equalsIgnoreCase("X"),
                        this.itemList.stream().filter(item ->
                                item.getX() == position.getX()
                                && item.getY() == position.getY())
                                .collect(Collectors.toList()));

                this.tiles[x][y] = snowStormTile;

                if (!snowStormTile.isWalkable()) {
                    tile = "x";
                } else {
                    tile = "0";
                }

                temporaryHeightmap.append(tile);
            }

            temporaryHeightmap.append("\r");
        }

        this.heightMap = temporaryHeightmap.toString();
    }

    public SnowStormTile getTile(Position position) {
        if (position.getX() < 0 || position.getY() < 0) {
            return null;
        }

        if (position.getX() >= this.mapSizeX || position.getY() >= this.mapSizeY) {
            return null;
        }

        return this.tiles[position.getX()][position.getY()];
    }

    public int getMapSizeX() {
        return mapSizeX;
    }

    public int getMapSizeY() {
        return mapSizeY;
    }

    public int getMapId() {
        return mapId;
    }

    public String getHeightMap() {
        return heightMap;
    }

    public ArrayList<SnowStormItem> getItems() {
        return this.itemList;
    }

    public SnowStormSpawn[] getSpawnClusters() {
        return spawnClusters.toArray(new SnowStormSpawn[0]);
    }

    public String getCompiledItems() {
        return compiledItems;
    }
}
