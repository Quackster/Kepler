package org.alexdev.kepler.game.moderation;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.moderation.CALL_FOR_HELP;
import org.alexdev.kepler.messages.outgoing.moderation.DELETE_CRY;
import org.alexdev.kepler.messages.outgoing.moderation.PICKED_CRY;
import org.alexdev.kepler.messages.outgoing.user.CRY_RECEIVED;
import org.alexdev.kepler.messages.types.MessageComposer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
        int roomId = caller.getRoomUser().getRoom().getId();

        CallForHelp cfh = new CallForHelp(callId, callerId, roomId, message);

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
    void sendToModerators(MessageComposer message) {
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
        cfh.updateCategory(newCategory);

        // Send the updated CallForHelp to Moderators
        // TODO: make sure this is the right way to notify the client about category change
        sendToModerators(new CALL_FOR_HELP(cfh));
    }

    public void deleteCall(CallForHelp cfh) {
        this.callsForHelp.remove(cfh.getCallId());

        sendToModerators(new DELETE_CRY(cfh));
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
