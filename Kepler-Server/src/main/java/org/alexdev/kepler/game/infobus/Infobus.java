package org.alexdev.kepler.game.infobus;

import org.alexdev.kepler.dao.mysql.CurrencyDao;
import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.games.wobblesquabble.WobbleSquabblePlayer;
import org.alexdev.kepler.game.infobus.InfobusManager;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.interactors.InteractionType;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.rooms.infobus.POLL_QUESTION;
import org.alexdev.kepler.messages.outgoing.rooms.items.SHOWPROGRAM;
import org.alexdev.kepler.messages.outgoing.user.currencies.TICKET_BALANCE;
import org.alexdev.kepler.messages.outgoing.wobblesquabble.*;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.schedule.FutureRunnable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Infobus implements Runnable {

    private Room room;
    private boolean doorOpen;
    private boolean pollIsActive;
    private boolean pollHasStarted;
    private boolean pollHasEnded;
    private List<Integer> playersInBus;
    private FutureRunnable gameTimerRunnable;
    private AtomicInteger pollTimeLeft;

    public Infobus(Room room) {
        this.room = room;
        this.playersInBus = new CopyOnWriteArrayList<>();
    }

    /**
     * Send a message to both clients.
     *
     * @param composer the clients to send to
     */
    public void send(MessageComposer composer) {

    }

    @Override
    public void run() {
        if (this.pollHasEnded) {
            return;
        }


       /*
        // If the game has timed out, too bad!
        if (DateUtil.getCurrentTimeSeconds() > this.gameStarted + WobbleSquabbleManager.WS_GAME_TIMEOUT_SECS) {
            return;
        }
    }*/

    }

    public void startPoll() {
        this.pollIsActive = true;
        this.pollTimeLeft = new AtomicInteger(30);
        this.gameTimerRunnable = new FutureRunnable() {
            public void run() {
                try {
                    if (pollTimeLeft.getAndDecrement() == 0) {
                        this.cancelFuture();
                        pollEnded();
                    }
                } catch (Exception ex) {
                    Log.getErrorLogger().error("Error occurred in infobus runnable: ", ex);
                }
            }
        };

        var future = GameScheduler.getInstance().getService().scheduleAtFixedRate(this.gameTimerRunnable, 0, 1, TimeUnit.SECONDS);
        this.gameTimerRunnable.setFuture(future);


        for (int playerId : this.getPlayers()) {
            PlayerManager.getInstance().getPlayerById(playerId).send(new POLL_QUESTION("Suck a dick\r1:Yes\r2:No\r3:WAAAT?"));
        }
    }

    public boolean getPollActive() {
        return this.pollIsActive;
    }

    public void pollEnded() {
        this.pollIsActive = false;
    }

    public void openDoor() {
        this.doorOpen = true;
        this.room.send(new SHOWPROGRAM(new String[] { "bus", "open" }));
    }

    public void closeDoor() {
        this.doorOpen = false;
        this.room.send(new SHOWPROGRAM(new String[] { "bus", "close" }));
    }

    public boolean isDoorOpen() {
        return this.doorOpen;
    }
    public void stopPoll() {
        this.pollIsActive = false;
        if(pollTimeLeft.get() != 0) {
            if(this.gameTimerRunnable.getFuture() != null) {
                this.gameTimerRunnable.cancelFuture();
            }
        }
    }

    public void addPlayer(int player) {
        this.playersInBus.add(player);
    }

    public void removePlayer(int player) {
        this.playersInBus.remove(player);
    }

    public List<Integer> getPlayers() {
        return this.playersInBus;
    }

}
