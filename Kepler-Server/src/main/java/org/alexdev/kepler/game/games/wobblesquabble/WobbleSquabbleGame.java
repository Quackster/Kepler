package org.alexdev.kepler.game.games.wobblesquabble;

import org.alexdev.kepler.dao.mysql.CurrencyDao;
import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.interactors.InteractionType;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.messages.outgoing.user.currencies.TICKET_BALANCE;
import org.alexdev.kepler.messages.outgoing.wobblesquabble.*;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.util.DateUtil;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class WobbleSquabbleGame implements Runnable {
    private static int LEFT_X = 21;
    private static int LEFT_Y = 15;

    private Room room;
    private WobbleSquabblePlayer firstPlayer;
    private WobbleSquabblePlayer secondPlayer;
    private boolean hasGameStarted;
    private boolean hasGameEnded;
    private long gameStarted;

    public WobbleSquabbleGame(Player firstPlayer, Player secondPlayer) {
        this.room = firstPlayer.getRoomUser().getRoom();
        this.hasGameStarted = false;
        this.firstPlayer = new WobbleSquabblePlayer(this, firstPlayer, -1);
        this.secondPlayer = new WobbleSquabblePlayer(this, secondPlayer, -1);

        if (this.firstPlayer.getPlayer().getRoomUser().getPosition().equals(new Position(LEFT_X, LEFT_Y))) {
            this.firstPlayer.setOrder(0);
            this.secondPlayer.setOrder(1);
        } else {
            this.secondPlayer.setOrder(0);
            this.firstPlayer.setOrder(1);
        }
    }

    /**
     * Send a message to both clients.
     *
     * @param composer the clients to send to
     */
    public void send(MessageComposer composer) {
        /*this.firstPlayer.send(composer);
        this.secondPlayer.send(composer);*/
        this.room.send(composer);
    }

    @Override
    public void run() {
        if (this.hasGameEnded) {
            return;
        }

        WobbleSquabblePlayer wsPlayer1 = this.getPlayer(0);
        WobbleSquabblePlayer wsPlayer2 = this.getPlayer(1);

        if (!this.hasGameStarted) {
            this.hasGameStarted = true;
            this.gameStarted = DateUtil.getCurrentTimeSeconds();
            this.send(new PT_START(wsPlayer1, wsPlayer2));

            // Set positions
            wsPlayer1.setPosition(-3);
            wsPlayer2.setPosition(4);
            return;
        }

        // If either requires a status update, send the update
        if (wsPlayer1.isRequiresUpdate() || wsPlayer2.isRequiresUpdate()) {
            this.updatePlayer(0);
            this.updatePlayer(1);

            this.send(new PT_STATUS(wsPlayer1, wsPlayer2));

            wsPlayer1.resetActions();
            wsPlayer2.resetActions();
        }

        // If the game has timed out, too bad!
        if (DateUtil.getCurrentTimeSeconds() > this.gameStarted + WobbleSquabbleManager.WS_GAME_TIMEOUT_SECS) {
            this.send(new PT_BOTHLOSE());
            this.endGame(-1); // Tied!
            return;
        }

        int loser = -1;
        int winner = -1;

        // If one of us gets a bit tipsy and off-balance, end the game!
        if (!wsPlayer1.isBalancing()) {
            winner = wsPlayer2.getOrder();
            loser = wsPlayer1.getOrder();
        }

        if (!wsPlayer2.isBalancing()) {
            winner = wsPlayer1.getOrder();
            loser = wsPlayer2.getOrder();
        }

        // If we have a loser and a winner, end the game!
        if (loser != -1 && winner != -1) {
            this.endGame(winner);
            return;
        }
    }

    /**
     * Update wobble squabble player.
     *
     * @param wsPlayerOrder the player to update
     */
    private void updatePlayer(int wsPlayerOrder) {
        WobbleSquabblePlayer wsPlayer = this.getPlayer(wsPlayerOrder);
        WobbleSquabblePlayer wsOpponent = wsPlayer.getGame().getPlayer(wsPlayer.getOrder() == 1 ? 0 : 1);

        int opponentDistance = Math.abs(wsPlayer.getPosition() - wsOpponent.getPosition());

        switch (wsPlayer.getMove()) {
            case BALANCE_LEFT: {
                int balanceCalculated = WobbleSquabbleManager.WS_BALANCE_POINTS + ThreadLocalRandom.current().nextInt(10);
                wsPlayer.setBalance(wsPlayer.getBalance() - balanceCalculated);
                break;
            }

            case BALANCE_RIGHT: {
                int balanceCalculated = WobbleSquabbleManager.WS_BALANCE_POINTS + ThreadLocalRandom.current().nextInt(10);
                wsPlayer.setBalance(wsPlayer.getBalance() + balanceCalculated);
                break;
            }

            case HIT_LEFT: {
                // Are we standing next to our opponent?
                if (opponentDistance <= 2/*(wsPlayer.getPosition() + 1) == wsOpponent.getPosition() || (wsPlayer.getPosition() - 1) == wsOpponent.getPosition()*/) {
                    wsOpponent.setHit(true);

                    int balanceCalculated = WobbleSquabbleManager.WS_HIT_POINTS + ThreadLocalRandom.current().nextInt(10);
                    wsOpponent.setBalance(wsOpponent.getBalance() + balanceCalculated);
                } else {
                    int balanceCalculated = WobbleSquabbleManager.WS_HIT_BALANCE_POINTS + ThreadLocalRandom.current().nextInt(10);
                    wsPlayer.setBalance(wsPlayer.getBalance() + balanceCalculated);
                }

                break;
            }

            case HIT_RIGHT: {
                // Are we standing next to our opponent?
                if (opponentDistance <= 2/*(wsPlayer.getPosition() + 1) == wsOpponent.getPosition() || (wsPlayer.getPosition() - 1) == wsOpponent.getPosition()*/) {
                    wsOpponent.setHit(true);

                    int balanceCalculated = WobbleSquabbleManager.WS_HIT_POINTS + ThreadLocalRandom.current().nextInt(10);
                    wsOpponent.setBalance(wsOpponent.getBalance() - balanceCalculated);
                } else {
                    int balanceCalculated = WobbleSquabbleManager.WS_HIT_BALANCE_POINTS + ThreadLocalRandom.current().nextInt(10);
                    wsPlayer.setBalance(wsPlayer.getBalance() - balanceCalculated);
                }

                break;
            }

            case WALK_FORWARD: {
                // Calculate new position
                int newPosition = wsPlayer.getPosition() - 1;

                if (newPosition >= -3 && newPosition <= 4) {
                    if (newPosition != wsOpponent.getPosition()) {
                        wsPlayer.setPosition(newPosition);
                    }
                }

                break;
            }

            case WALK_BACKWARD: {
                // Calculate new position
                int newPosition = wsPlayer.getPosition() + 1;

                if (newPosition >= -3 && newPosition <= 4) {
                    if (newPosition != wsOpponent.getPosition()) {
                        wsPlayer.setPosition(newPosition);
                    }
                }

                break;
            }

            case REBALANCE: {
                if (!wsPlayer.isRebalanced()) {
                    wsPlayer.setRebalanced(true);
                    wsPlayer.setBalance(0);
                }
                break;
            }
        }
    }

    /**
     * Method for ending the game.
     */
    public void endGame(int winner) {
        if (!this.room.getTaskManager().hasTask(WobbleSquabbleManager.getInstance().getName())) {
            return;
        }

        this.hasGameEnded = true;
        int loser = winner == 1 ? 0 : 1;

        WobbleSquabblePlayer winnerPlayer = this.getPlayer(winner);
        WobbleSquabblePlayer loserPlayer = this.getPlayer(loser);

        // Send end game
        if (winner != -1) {
            this.send(new PT_WIN(winner));
        }

        // Do updates after the players have fallen
        GameScheduler.getInstance().getService().schedule(()-> {
            this.send(new PT_END());

            // If it's a tie then remove both
            if (winner == -1) {
                this.removePlayer(0, true);
                this.removePlayer(1, true);
            } else {
                this.removePlayer(winner, false);
                this.removePlayer(loser, true);
            }

            // Cancel wobble squabble task
            this.room.getTaskManager().cancelTask(WobbleSquabbleManager.getInstance().getName());

            // Make users walk forward
            this.moveQueuedUsers();

        }, 1500, TimeUnit.MILLISECONDS);
        /*this.send(new PT_END());

        // If it's a tie then remove both
        if (winner == -1) {
            this.removePlayer(0);
            this.removePlayer(1);
        } else {
            this.removePlayer(loser);
        }*/

    }

    /**
     * Move users on the queue forward.
     */
    private void moveQueuedUsers() {
        for (Player player : this.room.getEntityManager().getPlayers()) {
            Item item = player.getRoomUser().getCurrentItem();

            if (item != null) {
                if (item.getDefinition().getInteractionType() != InteractionType.WS_QUEUE_TILE) {
                    continue;
                }

                Position front = player.getRoomUser().getPosition().getSquareInFront();
                player.getRoomUser().walkTo(front.getX(), front.getY());
            }
        }
    }

    /**
     * Removes the player from the game.
     *
     * @param playerNum the player id
     */
    public void removePlayer(int playerNum, boolean isThrown) {
        WobbleSquabblePlayer wsPlayer = this.getPlayer(playerNum);

        if (wsPlayer == null) {
            return;
        }

        Player player = wsPlayer.getPlayer();
        player.getRoomUser().setWalkingAllowed(true);

        CurrencyDao.decreaseTickets(player.getDetails(), 1);
        player.send(new TICKET_BALANCE(player.getDetails().getTickets()));

        if (isThrown) {
            //System.out.println("player thrown: " + player.getDetails().getName());
            player.getRoomUser().setStatus(StatusType.SWIM, "");

            int newX = player.getRoomUser().getPosition().getX() + ((wsPlayer.getBalance() < 0 ? -1 : 1));
            int newY = player.getRoomUser().getPosition().getY();

            Position position = new Position(newX, newY);
            position.setRotation(player.getRoomUser().getPosition().getRotation());

            player.getRoomUser().warp(position, true, false);
        }
    }

    /**
     * Gets the room that wobble squabble is running in.
     *
     * @return the room
     */
    public Room getRoom() {
        return room;
    }

    /**
     * Get the player, 1 or 0 is allowed only.
     *
     * @return the player, by integer position.
     */
    public WobbleSquabblePlayer getPlayer(int num) {
        for (WobbleSquabblePlayer wsPlayer : List.of(this.firstPlayer, this.secondPlayer)) {
            if (wsPlayer.getOrder() == num) {
                return wsPlayer;
            }
        }

        return null;
    }

    /**
     * Gets a wobble squabble player by id.
     *
     * @param id to the user id to get by
     * @return the player instance if found
     */
    public WobbleSquabblePlayer getPlayerById(int id) {
        for (int i = 0; i < 2; i++) {
            WobbleSquabblePlayer wsPlayer = this.getPlayer(i);

            if (wsPlayer.getPlayer().getDetails().getId() == id) {
                return wsPlayer;
            }
        }

        return null;
    }
}
