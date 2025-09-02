package net.h4bbo.kepler.game.games.snowstorm;

import net.h4bbo.kepler.game.games.GameObject;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SnowStormTurn {
    private List<GameObject> events;

    public SnowStormTurn() {
        this.events = new CopyOnWriteArrayList<>();
    }

    public List<GameObject> getSubTurns() {
        return events;
    }
}
