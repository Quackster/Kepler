package org.alexdev.kepler.game.games.wobblesquabble;


import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;

import java.util.concurrent.ThreadLocalRandom;

public class WobbleSquabbleManager {
    private static WobbleSquabbleManager instance;

    public static int WS_GAME_TICKET_COST = 1;
    public static int WS_BALANCE_POINTS = 35;
    public static int WS_HIT_POINTS = 13;
    public static int WS_HIT_BALANCE_POINTS = 10;
    public static int WS_GAME_TIMEOUT_SECS = 30;

    /**
     * Returns true or false if the user is in a game of wobble squabble.
     *
     * @param player the player to check
     * @return true, if they are
     */
    public boolean isPlaying(Player player) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return false;
        }

        if (!room.getTaskManager().hasTask(this.getName())) {
            return false;
        }

        WobbleSquabbleGame wsGame = (WobbleSquabbleGame) room.getTaskManager().getTask(this.getName());

        WobbleSquabblePlayer wsPlayer = wsGame.getPlayerById(player.getDetails().getId());
        return wsPlayer != null;
    }

    /**
     * Gets the wobble squabble player instance
     *
     * @param player the player to get for
     * @return the ws player instance, if found
     */
    public WobbleSquabblePlayer getPlayer(Player player) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return null;
        }

        if (!room.getTaskManager().hasTask(this.getName())) {
            return null;
        }

        WobbleSquabbleGame wsGame = (WobbleSquabbleGame) room.getTaskManager().getTask(this.getName());
        WobbleSquabblePlayer wsPlayer = wsGame.getPlayerById(player.getDetails().getId());

        return wsPlayer;
    }

    /**
     * Update wobble squabble player.
     *
     * @param wsPlayer the player to update
     */
    public void updatePlayer(WobbleSquabblePlayer wsPlayer) {
        int opponentDistance = 2;
        WobbleSquabblePlayer wsOpponent = wsPlayer.getGame().getPlayer(wsPlayer.getOrder() == 1 ? 0 : 1);

        switch (wsPlayer.getMove()) {
            case BALANCE_LEFT:
            {
                int balanceCalculated = WobbleSquabbleManager.WS_BALANCE_POINTS + ThreadLocalRandom.current().nextInt(10);
                wsPlayer.setBalance(wsPlayer.getBalance() - balanceCalculated);
                break;
            }

            case BALANCE_RIGHT:
            {
                int balanceCalculated = WobbleSquabbleManager.WS_BALANCE_POINTS + ThreadLocalRandom.current().nextInt(10);
                wsPlayer.setBalance(wsPlayer.getBalance() + balanceCalculated);
                break;
            }

            case HIT_LEFT:
            {
                // Are we standing next to our opponent?
                if ((wsPlayer.getPosition() + opponentDistance) <= wsOpponent.getPosition() || (wsPlayer.getPosition() - opponentDistance) <= wsOpponent.getPosition()) {
                    wsOpponent.setHit(true);

                    int balanceCalculated = WobbleSquabbleManager.WS_HIT_POINTS + ThreadLocalRandom.current().nextInt(10);
                    wsOpponent.setBalance(wsOpponent.getBalance() + balanceCalculated);
                } else {
                    int balanceCalculated = WobbleSquabbleManager.WS_HIT_BALANCE_POINTS + ThreadLocalRandom.current().nextInt(10);
                    wsPlayer.setBalance(wsPlayer.getBalance() + balanceCalculated);
                }

                break;
            }

            case HIT_RIGHT:
            {
                // Are we standing next to our opponent?
                if ((wsPlayer.getPosition() + opponentDistance) <= wsOpponent.getPosition() || (wsPlayer.getPosition() - opponentDistance) <= wsOpponent.getPosition()) {
                    wsOpponent.setHit(true);

                    int balanceCalculated = WobbleSquabbleManager.WS_HIT_POINTS + ThreadLocalRandom.current().nextInt(10);
                    wsOpponent.setBalance(wsOpponent.getBalance() - balanceCalculated);
                } else {
                    int balanceCalculated = WobbleSquabbleManager.WS_HIT_BALANCE_POINTS + ThreadLocalRandom.current().nextInt(10);
                    wsPlayer.setBalance(wsPlayer.getBalance() - balanceCalculated);
                }

                break;
            }

            case WALK_FORWARD:
            {
                // Calculate new position
                int newPosition = wsPlayer.getPosition() - 1;

                if (newPosition >= -3 && newPosition <= 4) {
                    if (newPosition != wsOpponent.getPosition()) {
                        wsPlayer.setPosition(newPosition);
                    }
                }

                break;
            }

            case WALK_BACKWARD:
            {
                // Calculate new position
                int newPosition = wsPlayer.getPosition() + 1;

                if (newPosition >= -3 && newPosition <= 4) {
                    if (newPosition != wsOpponent.getPosition()) {
                        wsPlayer.setPosition(newPosition);
                    }
                }

                break;
            }

            case REBALANCE:
            {
                if (!wsPlayer.isRebalanced()) {
                    wsPlayer.setRebalanced(true);
                    wsPlayer.setBalance(0);
                }
                break;
            }
        }
    }

    /**
     * Get the static instance of the wobble squabble manager.
     *
     * @return the static instance
     */
    public static WobbleSquabbleManager getInstance() {
        if (instance == null) {
            instance = new WobbleSquabbleManager();
        }

        return instance;
    }

    /**
     * Gets the name of the wobble squabble game task.
     *
     * @return the game task
     */
    public String getName() {
        return "WobbleGameTask";
    }
}
