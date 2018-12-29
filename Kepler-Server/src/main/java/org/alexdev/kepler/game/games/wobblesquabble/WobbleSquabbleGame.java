package org.alexdev.kepler.game.games.wobblesquabble;

import org.alexdev.kepler.dao.mysql.CurrencyDao;
import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.messages.outgoing.user.currencies.TICKET_BALANCE;
import org.alexdev.kepler.messages.outgoing.wobblesquabble.*;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.util.DateUtil;

import java.util.List;
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
            WobbleSquabbleManager.getInstance().updatePlayer(wsPlayer1);
            WobbleSquabbleManager.getInstance().updatePlayer(wsPlayer2);

            this.send(new PT_STATUS(wsPlayer1, wsPlayer2));

            wsPlayer1.resetActions();
            wsPlayer2.resetActions();
        }

        // If the game has timed out, too bad!
        if (DateUtil.getCurrentTimeSeconds() > this.gameStarted + WobbleSquabbleManager.WS_GAME_TIMEOUT_SECS) {
            this.send(new PT_TIMEOUT());
            this.endGame(-1); // Tied!
            return;
        }

        int loser = -1;
        int winner = -1;

        // If one of us gets a bit tipsy and off-balance, end the game!
        if (!wsPlayer1.isBalancing()) {
            loser = wsPlayer1.getOrder();
            winner = wsPlayer2.getOrder();
        }

        if (!wsPlayer2.isBalancing()) {
            loser = wsPlayer2.getOrder();
            winner = wsPlayer1.getOrder();
        }

        // If we have a loser and a winner, end the game!
        if (loser != -1 && winner != -1) {
            this.endGame(winner);
            return;
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

        // Send end game
        this.send(new PT_WIN(winner));

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
            player.getRoomUser().setStatus(StatusType.SWIM, "");

            int newX = player.getRoomUser().getPosition().getX() + ((wsPlayer.getBalance() < 0 ? -1 : 1));
            int newY = player.getRoomUser().getPosition().getY();

            Position position = new Position(newX, newY);
            position.setRotation(player.getRoomUser().getPosition().getRotation());

            player.getRoomUser().warp(position, true);
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
