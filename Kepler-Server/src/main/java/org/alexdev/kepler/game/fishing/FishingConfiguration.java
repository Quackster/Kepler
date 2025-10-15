package org.alexdev.kepler.game.fishing;

import org.alexdev.kepler.game.pathfinder.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FishingConfiguration {

    private final Position fishSign;
    private final FishingArea[] fishArea;

    public FishingConfiguration(Position fishSign, FishingArea[] fishAreas) {
        this.fishSign = fishSign;
        this.fishArea = fishAreas;
    }

    public Position getFishSign() {
        return fishSign;
    }

    public List<Position> getRandomFishAreas() {
        var rand = ThreadLocalRandom.current();
        var result = new HashSet<Position>();

        // Get up to SPOTS_PER_AREA random spots from each area.
        for (FishingArea area : fishArea) {
            final Position[] spots = area.getSpots();

            // Optimize for small areas.
            if (spots.length < area.getAmount()) {
                Collections.addAll(result, spots);
                continue;
            }

            // Get random spots.
            for (int i = 0; i < area.getAmount(); i++) {
                while (true) {
                    var pos = spots[rand.nextInt(spots.length)];

                    if (result.add(pos)) {
                        break;
                    }
                }
            }
        }

        return new ArrayList<>(result);
    }
}
