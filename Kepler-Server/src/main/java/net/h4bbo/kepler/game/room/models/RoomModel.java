package net.h4bbo.kepler.game.room.models;

import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.room.mapping.RoomTileState;
import net.h4bbo.kepler.game.triggers.GenericTrigger;
import net.h4bbo.kepler.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class RoomModel {
    private String modelId;
    private String modelName;
    private int doorX;
    private int doorY;
    private double doorZ;
    private int doorRotation;
    private int mapSizeX;
    private int mapSizeY;
    private String heightmap;

    private RoomTileState[][] tileStates;
    private double[][] tileHeights;

    private RoomModelTriggerType modelTrigger;

    public RoomModel(String modelId, String modelName, int doorX, int doorY, double doorZ, int doorRotation, String heightmap, String triggerClass) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.doorX = doorX;
        this.doorY = doorY;
        this.doorZ = doorZ;
        this.doorRotation = doorRotation;
        this.heightmap = heightmap;

        if (!StringUtil.isNullOrEmpty(triggerClass)) {
            try {
                this.modelTrigger = RoomModelTriggerType.valueOf(triggerClass.toUpperCase());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.parse();
    }

    /**
     * Parse heightmap, add invalid tiles and the tile heights used
     * for walking, stairs, etc.
     */
    private void parse() {
        String[] lines = this.heightmap.split(Pattern.quote("|"));

        this.mapSizeY = lines.length;
        this.mapSizeX = lines[0].length();

        this.tileStates = new RoomTileState[this.mapSizeX][this.mapSizeY];
        this.tileHeights = new double[this.mapSizeX][this.mapSizeY];

        StringBuilder temporaryHeightmap = new StringBuilder();

        for (int y = 0; y < this.mapSizeY; y++) {
            String line = lines[y];

            for (int x = 0; x < this.mapSizeX; x++) {
                String tile = Character.toString(line.charAt(x));

                if (StringUtils.isNumeric(tile)) {
                    this.tileStates[x][y] = RoomTileState.OPEN;
                    this.tileHeights[x][y] = Double.parseDouble(tile);
                } else {
                    this.tileStates[x][y] = RoomTileState.CLOSED;
                    this.tileHeights[x][y] = 0;
                }

                if (x == this.doorX && y == this.doorY) {
                    this.tileStates[x][y] = RoomTileState.OPEN;
                    this.tileHeights[x][y] = this.doorZ;
                }

                temporaryHeightmap.append(tile);
            }

            temporaryHeightmap.append("\r");
        }

        this.heightmap = temporaryHeightmap.toString();
    }

    /**
     * Get the tile state by given coordinates. This
     * doesn't include room furniture.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the room state
     */
    public RoomTileState getTileState(int x, int y) {
        if (x < 0 || y < 0) {
            return RoomTileState.CLOSED;
        }

        if (x >= this.mapSizeX || y >= this.mapSizeY) {
            return RoomTileState.CLOSED;
        }

        return tileStates[x][y];
    }

    /**
     * Get the tile height, this doesn't include
     * furniture heights.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the room height
     */
    public double getTileHeight(int x, int y) {
        if (x < 0 || y < 0) {
            return 0;
        }

        if (x >= this.mapSizeX || y >= this.mapSizeY) {
            return 0;
        }

        return tileHeights[x][y];
    }


    public String getId() {
        return modelId;
    }

    public String getName() {
        return modelName;
    }

    public Position getDoorLocation() {
        return new Position(this.doorX, this.doorY, this.doorZ, this.doorRotation, this.doorRotation);
    }

    public int getMapSizeX() {
        return mapSizeX;
    }

    public int getMapSizeY() {
        return mapSizeY;
    }

    public String getHeightmap() {
        return heightmap;
    }

    public RoomModelTriggerType getModelTrigger() {
        return modelTrigger;
    }

    public GenericTrigger getRoomTrigger() {
        if (modelTrigger != null )
            return modelTrigger.getRoomTrigger();
        else
            return null;
    }

    public int getRandomBound(int boundId) {
        if (boundId == 0) {
            return ThreadLocalRandom.current().nextInt(0, this.mapSizeX);
        }

        if (boundId == 1) {
            return ThreadLocalRandom.current().nextInt(0, this.mapSizeY);
        }

        return -1;
    }
}