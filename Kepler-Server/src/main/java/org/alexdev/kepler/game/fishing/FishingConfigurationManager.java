package org.alexdev.kepler.game.fishing;

import org.alexdev.kepler.game.pathfinder.Position;

import java.util.HashMap;

public class FishingConfigurationManager {

    private static final HashMap<String, FishingConfiguration> CONFIGS = new HashMap<>();

    static {
        CONFIGS.put("park_a", new FishingConfiguration(
                new Position(28, 16, 1),
                new FishingArea[]{
                        new FishingArea(1, new Position[]{
                                new Position(21, 27, 0),
                                new Position(21, 26, 0),
                                new Position(22, 25, 0)
                        }),
                        new FishingArea(2, new Position[]{
                                new Position(21, 18, 0),
                                new Position(22, 18, 0),
                                new Position(21, 17, 0),
                                new Position(23, 17, 0),
                                new Position(21, 16, 0),
                                new Position(23, 16, 0),
                                new Position(22, 13, 0)
                        }),
                        new FishingArea(1, new Position[]{
                                new Position(39, 0, 0),
                                new Position(40, 1, 0),
                                new Position(41, 2, 0),
                        })
                }));
    }

    public static FishingConfiguration get(String model) {
        return CONFIGS.get(model);
    }
}
