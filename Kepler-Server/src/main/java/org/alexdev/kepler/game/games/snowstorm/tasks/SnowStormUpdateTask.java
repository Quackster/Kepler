package org.alexdev.kepler.game.games.snowstorm.tasks;

import org.alexdev.kepler.game.games.enums.GameState;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.SnowStormObject;
import org.alexdev.kepler.game.games.snowstorm.SnowStormTurn;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.games.SNOWSTORM_GAMESTATUS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SnowStormUpdateTask implements Runnable {
    private final Room room;
    private final SnowStormGame game;
    private List<SnowStormTurn> snowStormTurnList;
    private int lastTurn;

    public SnowStormUpdateTask(Room room, SnowStormGame game) {
        this.room = room;
        this.game = game;
        this.resetTurns();
    }

    private void resetTurns() {
        /*this.snowStormTurnList = new SnowStormTurn[Integer.MAX_VALUE];
        this.lastTurn = 0;*/
        this.snowStormTurnList = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        try {
            if (!this.game.isGameStarted() ||
                    this.game.getGameState() == GameState.ENDED ||
                    this.game.getPlayers().isEmpty()) {
                return; // Don't send any packets or do any logic checks during when the game is finished
            }

            if (this.snowStormTurnList.size() > 0) {
                var turnList = new ArrayList<>(this.snowStormTurnList);

                for (Player player : this.game.getRoom().getEntityManager().getPlayers()) {
                    player.send(new SNOWSTORM_GAMESTATUS(turnList));
                }

                resetTurns();
            }

        } catch (Exception ex) {
            Log.getErrorLogger().error("SnowstormTask crashed: ", ex);

        }
    }

    public void queueSubTurn(int framesAhead, SnowStormObject gameEvent) {
        /*int lastPosition = 0;

        for (SnowStormTurn snowStormTurn : this.snowStormTurnList) {
            if (snowStormTurn.getSubTurns().stream().anyMatch(turn -> turn.getId() == gameEvent.getId())) {
                lastPosition = this.snowStormTurnList.indexOf(snowStormTurn);
            }
        }

        int nextPosition = lastPosition + framesAhead;
        SnowStormTurn snowStormTurn = null;

        try {
            snowStormTurn = this.snowStormTurnList.get(nextPosition);
        } catch (Exception ex) {
            snowStormTurn = new SnowStormTurn();
            this.snowStormTurnList.set(nextPosition, snowStormTurn);
        }

        snowStormTurn.getSubTurns().add(gameEvent);*/

        for (int i = 0; i < framesAhead; i++) {
            this.snowStormTurnList.add(new SnowStormTurn());
        }

        SnowStormTurn snowStormTurn = new SnowStormTurn();
        snowStormTurn.getSubTurns().add(gameEvent);

        this.snowStormTurnList.add(snowStormTurn);
    }

    public List<SnowStormTurn> getTurns() {
        return snowStormTurnList;
    }
}
