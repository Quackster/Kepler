package net.h4bbo.kepler.game.player.guides;

import net.h4bbo.kepler.dao.mysql.GuideDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.guides.INVITATION;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerGuideManager {
    private Player player;
    private boolean hasTutorial;
    private boolean isGuide;
    private boolean isGuidable;
    private boolean isGuidedBy;
    private boolean isWaitingForInvitations;
    private boolean isWaitingForGuide;

    private boolean canUseTutorial;
    private boolean blockTutorial;
    private boolean cancelTutorial;

    private List<GuidingData> guiding;
    private List<Integer> invites;
    private List<Integer> invited;

    private int invitedBy;
    private int startedForWaitingGuidesTime;

    public PlayerGuideManager(Player player) {
        this.player = player;
        this.guiding = new CopyOnWriteArrayList<>();
        this.invites = new CopyOnWriteArrayList<>();
        this.invited = new CopyOnWriteArrayList<>();
    }

    /**
     * Add a guide invite from newbie.
     *
     * @param userId the user id from
     * @param username the username from
     */
    public void addInvite(Integer userId, String username) {
        if (this.invites.contains(userId)) {
            return;
        }

        this.player.send(new INVITATION(userId, username));
        this.invites.add(userId);
    }

    /**
     * Get if the guide contains an invite from this user id
     *
     * @param id the user id to check
     * @return true, if successful
     */
    public boolean hasInvite(int id) {
        return this.invites.contains(id);
    }

    /**
     * Remove user id who invited the user
     *
     * @param userId the user id
     */
    public void removeInvite(int userId) {
        this.invites.remove(Integer.valueOf(userId));
    }

    /**
     * Get if this user was invitied by a newb
     *
     * @param id the user id to check
     * @return true, if successful
     */
    public boolean hasInvited(int id) {
        return this.invited.contains(id);
    }

    /**
     * Remove user id who was invited
     *
     * @param userId the user id
     */
    public void removeInvited(int userId) {
        this.invited.remove(Integer.valueOf(userId));
    }

    /**
     * Add a guide invited by newbie
     *
     * @param userId the user id from
     */
    public void addInvited(Integer userId) {
        if (this.invited.contains(userId)) {
            return;
        }

        this.invited.add(userId);
    }

    /**
     * Get whether the player is a guide.
     *
     * @return true, if successful
     */
    public boolean isGuide() {
        return isGuide;
    }

    /**
     * Set whether the player is guide.
     *
     * @param guide the flag on whether player is guide or not
     */
    public void setGuide(boolean guide) {
        isGuide = guide;
    }

    /**
     * Get the player who may or may not be a guide.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get is waiting for invitations.
     *
     * @return the value whether they're waiting for not
     */
    public boolean isWaitingForInvitations() {
        return isWaitingForInvitations;
    }

    /**
     * Set is waiting for invitations.
     *
     * @param waitingForInvitations the value whether they're watiting for not
     */
    public void setWaitingForInvitations(boolean waitingForInvitations) {
        isWaitingForInvitations = waitingForInvitations;
    }

    /**
     * Set if the user is waiting for a guide to pickup invite.
     *
     * @return true, if successful
     */
    public boolean isWaitingForGuide() {
        return isWaitingForGuide;
    }

    /**
     * Set if a player is waiting for a guide to pick up.
     *
     * @param waitingForGuide true, if successful
     */
    public void setWaitingForGuide(boolean waitingForGuide) {
        isWaitingForGuide = waitingForGuide;
    }

    /**
     * Get whether the player is guidable
     * @return true, if successful
     */
    public boolean isGuidable() {
        return isGuidable;
    }

    /**
     * Set whether the player is guidable.
     *
     * @param guidable true, if successful
     */
    public void setGuidable(boolean guidable) {
        isGuidable = guidable;
    }

    /**
     * Get the list of users the user is currently guiding.
     *
     * @return the users the player is guiding
     */
    public List<GuidingData> getGuiding() {
        return guiding;
    }

    /**
     * Get if the user uses the tutorial
     *
     * @return true, if successful
     */
    public boolean hasTutorial() {
        return hasTutorial;
    }

    /**
     * Set if the user has tutorial.
     *
     * @param hasTutorial true, if successful
     */
    public void setHasTutorial(boolean hasTutorial) {
        this.hasTutorial = hasTutorial;
    }

    /**
     * Get if the player can use tutorial.
     *
     * @return true, if successful
     */
    public boolean canUseTutorial() {
        return canUseTutorial;
    }

    /**
     * Set if the player can use tutorial.
     *
     * @param canUseTutorial the flag to set
     */
    public void setCanUseTutorial(boolean canUseTutorial) {
        this.canUseTutorial = canUseTutorial;
    }

    /**
     * Get if the player blocks tutorial
     *
     * @return true, if successful
     */
    public boolean isBlockingTutorial() {
        return blockTutorial;
    }

    /**
     * Set if the player blocks tutorial.
     *
     * @param blockTutorial the flag to set
     */
    public void setBlockingTutorial(boolean blockTutorial) {
        this.blockTutorial = blockTutorial;
    }

    /**
     * Get if the player has cancelled the tutorial
     *
     * @return true, if successful
     */
    public boolean isCancelTutorial() {
        return cancelTutorial;
    }

    /**
     * Set if the player cancelled tutorial.
     *
     * @param cancelTutorial the flag to set
     */
    public void setCancelTutorial(boolean cancelTutorial) {
        this.cancelTutorial = cancelTutorial;
    }

    /**
     * Get the list of user ids who have invited the guide.
     *
     * @return the list of users
     */
    public List<Integer> getInvites() {
        return invites;
    }

    /**
     * Get the list of users this newb has invited.
     *
     * @return the list of users invited
     */
    public List<Integer> getInvited() {
        return invited;
    }

    /**
     * Get the user id who last invited the tutor.
     *
     * @return the user id
     */
    public int getInvitedBy() {
        return invitedBy;
    }

    /**
     * Set the user id who invited the tutor
     *
     * @param invitedBy the user id
     */
    public void setInvitedBy(int invitedBy) {
        this.invitedBy = invitedBy;
    }

    /**
     * Refresh the guiding users.
     */
    public void refreshGuidingUsers() {
        this.guiding = GuideDao.getGuidedBy(this.player.getDetails().getId());
    }

    public int getStartedForWaitingGuidesTime() {
        return startedForWaitingGuidesTime;
    }

    public void setStartedForWaitingGuidesTime(int startedForWaitingGuidesTime) {
        this.startedForWaitingGuidesTime = startedForWaitingGuidesTime;
    }
}
