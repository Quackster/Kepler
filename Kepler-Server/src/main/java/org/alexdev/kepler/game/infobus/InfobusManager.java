package org.alexdev.kepler.game.infobus;


import com.google.gson.Gson;
import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.rooms.infobus.VOTE_QUESTION;
import org.alexdev.kepler.messages.outgoing.rooms.infobus.VOTE_RESULTS;
import org.alexdev.kepler.messages.outgoing.rooms.items.SHOWPROGRAM;
import org.alexdev.kepler.server.rabbitmq.HabboActivityQueueSingleton;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.schedule.FutureRunnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class InfobusManager {
    private static InfobusManager instance;
    private boolean isDoorOpen;
    private List<Integer> playersInBus;
    private FutureRunnable gameTimerRunnable;
    private AtomicInteger pollTimeLeft;
    private String question;
    private List<String> options;
    private List<Integer> votes;


    public static InfobusManager getInstance() {
        if (instance == null) {
            instance = new InfobusManager();
        }

        return instance;
    }

    InfobusManager() {
        this.question = null;
        this.playersInBus = new CopyOnWriteArrayList<>();
        this.options = new CopyOnWriteArrayList<>();
        this.votes = new CopyOnWriteArrayList<>();
    }

    private int getVotes(int optionIndex)
    {
        return Collections.frequency(this.votes, optionIndex);
    }

    public void startPoll() {
        this.pollTimeLeft = new AtomicInteger(30);
        this.gameTimerRunnable = new FutureRunnable() {
            public void run() {
                try {
                    if (pollTimeLeft.getAndDecrement() == 0) {
                        this.cancelFuture();
                        pollEnded();
                    }
                    SendStatusToQueue();
                } catch (Exception ex) {
                    Log.getErrorLogger().error("Error occurred in infobus runnable: ", ex);
                }
            }
        };

        var future = GameScheduler.getInstance().getService().scheduleAtFixedRate(this.gameTimerRunnable, 0, 1, TimeUnit.SECONDS);
        this.gameTimerRunnable.setFuture(future);

        // Send question to players in infobus
        for (int playerId : this.getPlayers()) {
            PlayerManager.getInstance().getPlayerById(playerId).send(new VOTE_QUESTION(constructVoteQuestion()));
        }
        SendStatusToQueue();
    }

    // Constructs the string shown in the status modal
    public String constructStatus() {
        StringBuilder msg = new StringBuilder().append("Users in bus: " + this.playersInBus + "\r");

        msg.append("\r Question: " + this.question);
        msg.append("\r Options: \r");
        for(int i=0; i < options.size(); i++){
            int optionNumber = i + 1;
            msg.append("\r" + optionNumber + ":" + options.get(i));
        }
        return msg.toString();
    }

    // Constructs the message sent to the client
    public String constructVoteQuestion() {
        StringBuilder msg = new StringBuilder().append(this.question);
        for(int i=0; i < options.size(); i++){
            int optionNumber = i + 1;
            msg.append("\r" + optionNumber + ":" + options.get(i));
        }
        return msg.toString();
    }

    // Construct vote result
    public String constructVoteResult() {
        StringBuilder msg = new StringBuilder().append("/" + this.votes.size());
        for(int i=0; i < options.size(); i++){
            msg.append("/" + getVotes(i));
        }

        return msg.toString();
    }

    public void setQuestion(String question) {
        this.question = question;
        SendStatusToQueue();
    }

    public void addOption(String option) {
        this.options.add(option);
        SendStatusToQueue();
    }

    public void removeOption(int option) {
        // minus one, so it makes sense when using status
        if(this.options.indexOf(this.options.get(option)) != -1) {
            this.options.remove(this.options.get(option));
        }
        SendStatusToQueue();
    }

    public void editOption(int option, String newOption) {
        this.options.set(option, newOption);
        SendStatusToQueue();
    }

    public void addVote(int option) {
        this.votes.add(option-1);
        SendStatusToQueue();
    }


    public void reset() {
        //this.question = null;
        this.votes.clear();
        //this.options.clear();
        SendStatusToQueue();
    }

    public void pollEnded() {
        for (int playerId : this.getPlayers()) {
            PlayerManager.getInstance().getPlayerById(playerId).send(new VOTE_RESULTS(constructVoteResult()));
        }

        this.reset();
    }

    public String getQuestion() {
        return this.question;
    }

    public List<String> getOptions() {
        return this.options;
    }

    public List<Integer> getPlayers() {
        return this.playersInBus;
    }

    public boolean isDoorOpen() {
        return this.isDoorOpen;
    }

    public void openDoor(Room room) {
        this.isDoorOpen = true;
        room.send(new SHOWPROGRAM(new String[] { "bus", "open" }));
        SendStatusToQueue();
    }

    public void closeDoor(Room room) {
        this.isDoorOpen = false;
        room.send(new SHOWPROGRAM(new String[] { "bus", "close" }));
        SendStatusToQueue();
    }

    public int getDoorX() {
        return 28;
    }

    public int getDoorY() {
        return 4;
    }

    public void addPlayer(int player) {
        this.playersInBus.add(player);
        SendStatusToQueue();
    }

    public void removePlayer(int player) {
        if(this.playersInBus.indexOf(player) != -1) {
            this.playersInBus.remove(this.playersInBus.indexOf(player));
        }
        SendStatusToQueue();
    }

    public void SendStatusToQueue() {

        Map<String, Object> msg = new HashMap<>();
        msg.put("players", this.playersInBus);
        msg.put("time", pollTimeLeft);
        msg.put("question", this.question);
        msg.put("options", this.options);
        msg.put("votes", this.votes);
        msg.put("door", this.isDoorOpen);
        // Using gson to convert the object to a json string
        String messageJson = new Gson().toJson(msg);
        HabboActivityQueueSingleton.getInstance().publishMessage("infobus", messageJson);
    }

}
