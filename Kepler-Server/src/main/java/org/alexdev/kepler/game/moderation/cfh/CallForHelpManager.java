package org.alexdev.kepler.game.moderation.cfh;

import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.moderation.CALL_FOR_HELP;
import org.alexdev.kepler.messages.outgoing.moderation.DELETE_CRY;
import org.alexdev.kepler.messages.outgoing.moderation.PICKED_CRY;
import org.alexdev.kepler.messages.outgoing.user.CRY_RECEIVED;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.util.DateUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CallForHelpManager {
    private static CallForHelpManager instance;
    private Map<Integer, CallForHelp> callsForHelp;
    private AtomicInteger latestCallId;

    public CallForHelpManager() {
        this.callsForHelp = new ConcurrentHashMap<>();
        this.latestCallId = new AtomicInteger();
    }

    /**
     * Add Call for Help to queue
     *
     * @param caller  The person submitting the CFH
     * @param message The message attached to the CFH
     */
    public void submitCall(Player caller, String message) {
        int callId = this.latestCallId.getAndIncrement();
        int callerId = caller.getDetails().getId();
        Room room = caller.getRoomUser().getRoom();

        CallForHelp cfh = new CallForHelp(callId, callerId, room, message);
        this.callsForHelp.put(callId, cfh);

        sendToModerators(new CALL_FOR_HELP(cfh));
        caller.send(new CRY_RECEIVED());
    }

    /**
     * Get open Call for Help by Player Username
     *
     * @param id the ID to fetch
     * @return the Call for Help, null if  no open Calls for Help
     */
    public CallForHelp getCall(int id) {
        return this.callsForHelp.get(id);
    }

    /**
     * Get Call for Help by Player Username
     *
     * @param userId the user id to retrieve for
     * @return the open Call for Help, null if  no open Calls for Help
     */
    public CallForHelp getPendingCall(int userId) {
        for (CallForHelp cfh : this.callsForHelp.values()) {
            if (cfh.isOpen() && cfh.getCaller() == userId) {
                return cfh;
            }
        }

        return null;
    }

    /**
     * Get Call for Help by Player
     *
     * @param player the player to check for
     * @return boolean indicating if there are open calls for this player
     */
    public boolean hasPendingCall(Player player) {
        return this.getPendingCall(player.getDetails().getId()) != null;
    }

    /**
     * Send a packet to all online moderators
     *
     * @param message the MessageComposer to send
     */
    private void sendToModerators(MessageComposer message) {
        for (Player p : PlayerManager.getInstance().getPlayers()) {
            if (p.hasFuse(Fuseright.RECEIVE_CALLS_FOR_HELP)) {
                p.send(message);
            }
        }
    }

    /**
     * Pick up a call for help
     *
     * @param cfh the CFH to pick up
     * @param moderator the moderator who is picking it up
     */
    public void pickUp(CallForHelp cfh, Player moderator) {
        cfh.setPickedUpBy(moderator);

        // Send the updated CallForHelp to all moderators
        sendToModerators(new PICKED_CRY(cfh));
    }

    /**
     * Chnage catgeory of Call
     *
     * @param cfh the CFH to change
     * @param newCategory the new category
     */
    public void changeCategory(CallForHelp cfh, int newCategory) {
        if (!this.callsForHelp.containsKey(cfh.getCryId())) {
            return;
        }

        cfh.updateCategory(newCategory);
        sendToModerators(new CALL_FOR_HELP(cfh));
    }

    /**
     * Deletes the cfh to all moderators and marks it for deletion in 30 minutes.
     *
     * @param cfh the cfh to delete
     */
    public void deleteCall(CallForHelp cfh) {
        cfh.setDeleted(true);
        sendToModerators(new DELETE_CRY(cfh.getCryId()));
    }

    /**
     * Purges expired cfhs, server remembers them for atleast 30 minutes
     */
    public void purgeExpiredCfh() {
        Predicate<CallForHelp> filter = cfh -> !cfh.isOpen() || DateUtil.getCurrentTimeSeconds() > cfh.getExpireTime();

        this.callsForHelp.values().stream().filter(filter).collect(Collectors.toList()).forEach(x -> {
            sendToModerators(new DELETE_CRY(x.getCryId()));
        });

        this.callsForHelp.values().removeIf(filter);

    }

    /**
     * Gets the instance
     *
     * @return the instance
     */
    public static CallForHelpManager getInstance() {
        if (instance == null) {
            instance = new CallForHelpManager();
        }

        return instance;
    }

}
