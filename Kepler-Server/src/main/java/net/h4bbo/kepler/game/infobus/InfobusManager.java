package net.h4bbo.kepler.game.infobus;
import net.h4bbo.kepler.dao.mysql.InfobusDao;
import net.h4bbo.kepler.game.GameScheduler;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.RoomManager;
import net.h4bbo.kepler.log.Log;
import net.h4bbo.kepler.messages.outgoing.infobus.BUS_DOOR;
import net.h4bbo.kepler.messages.outgoing.infobus.CANNOT_ENTER_BUS;
import net.h4bbo.kepler.messages.outgoing.infobus.POLL_QUESTION;
import net.h4bbo.kepler.messages.outgoing.infobus.VOTE_RESULTS;

import java.util.concurrent.TimeUnit;

public class InfobusManager {
    private static InfobusManager instance;
    private boolean canUpdateResults;
    private boolean isDoorOpen;
    private boolean isEventActive;
    private InfobusPoll currentPoll;

    public InfobusManager() {
    }

    public void stopEvent() {
        var room = RoomManager.getInstance().getRoomByModel("park_b");

        if (room != null) {
            for (Player player : room.getEntityManager().getPlayers()) {
                var composer = new CANNOT_ENTER_BUS("The Infobus event has now ended. Please check the site for updates in future.");

                player.send(composer);
                //player.getRoomUser().getPacketQueueAfterRoomLeave().add(composer);

                player.getRoomUser().kick(true);
                //player.getRoomUser().setBeingKicked(false);
            }
        }

        this.updateDoorStatus(false);
        this.currentPoll = null;
    }

    public void updateDoorStatus(boolean doorStatus) {
        this.isDoorOpen = doorStatus;

        var park = RoomManager.getInstance().getRoomByModel("park_a");

        if (park != null) {
            park.send(new BUS_DOOR(this.isDoorOpen));
        }
    }

    /**
     * Initiate polling to collect poll data.
     */
    public void startPolling(int pollId) {
        this.canUpdateResults = false;
        this.currentPoll = InfobusDao.get(pollId);

        if (currentPoll == null) {
            return;
        }

        var room = RoomManager.getInstance().getRoomByModel("park_b");

        if (room == null) {
            return;
        }

        for (Player player : room.getEntityManager().getPlayers()) {
            //if (player.getNetwork().isFlashConnected()) {
            //    player.send(new ALERT("Polling has started, unfortunately, flash clients can't vote as it is unsupported by the client."));
            //    continue;
            //}

            if (!InfobusDao.hasAnswer(currentPoll.getId(), player.getDetails().getId())) {
                player.send(new POLL_QUESTION(currentPoll.getPollData().getQuestion(), currentPoll.getPollData().getAnswers()));
            }

        }

        // Polling timer
        /*this.pollRunnable = new FutureRunnable() {
            public void run() {
                try {
                    if (!getDoorStatus() || (currentPoll != null && currentPoll.getPollData().getAnswers().isEmpty())) {
                        cancelFuture();
                        return;
                    }

                    if (pollSeconds.getAndDecrement() == 0) {
                        canUpdateResults = true;
                        showPollResults();
                        cancelFuture();
                    }

                } catch (Exception ex) {
                    Log.getErrorLogger().error("Error occurred in polling: ", ex);
                }
            }
        };

        var future = GameScheduler.getInstance().getService().scheduleAtFixedRate(this.pollRunnable, 0, 1, TimeUnit.SECONDS);
        this.pollRunnable.setFuture(future);*/

        GameScheduler.getInstance().getService().schedule(() -> {
            try {
                showPollResults(currentPoll.getId());
            } catch (Exception ex) {
                Log.getErrorLogger().error("Error occurred in polling: ", ex);
            }
        }, 30, TimeUnit.SECONDS);
    }

    public void showPollResults(int pollId) {
        var currentPoll = InfobusDao.get(pollId);

        if (currentPoll == null) {
            return;
        }

        this.canUpdateResults = true;

        var room = RoomManager.getInstance().getRoomByModel("park_b");

        if (room != null) {
            var answerResults = InfobusDao.getAnswers(currentPoll.getId());
            int totalAnswers = answerResults.values().stream().mapToInt(Integer::intValue).sum();

            room.send(new VOTE_RESULTS(currentPoll.getPollData().getQuestion(), currentPoll.getPollData().getAnswers(), answerResults, totalAnswers));
        }
    }

    public boolean isDoorOpen() {
        return isDoorOpen;
    }

    public void setDoorOpen(boolean doorOpen) {
        isDoorOpen = doorOpen;
    }

    public boolean isEventActive() {
        return isEventActive;
    }

    public void setEventActive(boolean eventActive) {
        isEventActive = eventActive;
    }

    public InfobusPoll getCurrentPoll() {
        return currentPoll;
    }

    /**
     * Get the infobus manager instance.
     *
     * @return the infobus manager
     */
    public static InfobusManager getInstance() {
        if (instance == null) {
            instance = new InfobusManager();
        }

        return instance;
    }

    public boolean canUpdateResults() {
        return canUpdateResults;
    }

    public int getDoorX() {
        return 28;
    }

    public int getDoorY() {
        return 4;
    }

}
