package org.alexdev.kepler.game.badges;

import org.alexdev.kepler.dao.mysql.BadgeDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.badges.AVAILABLE_BADGES;
import org.alexdev.kepler.messages.outgoing.user.badges.ACHIEVEMENT_NOTIFICATION;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BadgeManager {
    private int userId;
    private Player player;

    private List<Badge> badges;
    private Set<Badge> badgesToSave;

    public BadgeManager() {
        this.badgesToSave = new HashSet<>();
    }

    /**
     * Load badges by constructor, the player will be null.
     *
     * @param userId the user id
     */
    public BadgeManager(int userId) {
        super();
        this.userId = userId;
        this.badges = BadgeDao.getBadges(userId);
    }

    /**
     * Load players used when client logs into server.
     *
     * @param player the player instance
     */
    public void loadBadges(Player player) {
        this.player = player;
        this.userId = player.getDetails().getId();
        this.badges = BadgeDao.getBadges(this.userId);
    }

    /**
     * Refresh badges, will also try and see if it can progress any achievements.
     */
    public void refreshBadges() {
        this.player.send(new AVAILABLE_BADGES(this.player));
    }

    /**
     * Get if the user has a badge.
     *
     * @param badgeCode the badge code to check
     * @return true, if they do
     */
    public boolean hasBadge(String badgeCode) {
        for (Badge badge : this.badges) {
            if (badge.getBadgeCode().toLowerCase().equals(badgeCode.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the badge instance by badge code.
     *
     * @param badgeCode the badge code to check for.
     * @return the badge instance, if found
     */
    public Badge getBadge(String badgeCode) {
        for (Badge badge : this.badges) {
            if (badge.getBadgeCode().toLowerCase().equals(badgeCode.toLowerCase())) {
                return badge;
            }
        }

        return null;
    }

    /**
     * Change the badge, by setting it as equipped or not, slot id or not, by the badge code.
     *
     * @param badgeCode the badge code of the badge to edit
     * @param equipped whether it's equipped
     * @param slotId the badge slot id (1-5)
     */
    public void changeBadge(String badgeCode, boolean equipped, int slotId) {
        if (!this.hasBadge(badgeCode)) {
            return;
        }

        Badge badge = this.getBadge(badgeCode);
        badge.setEquipped(equipped);
        badge.setSlotId(slotId);

        this.badgesToSave.add(badge);
    }

    public void unequipAllBadges() {
        for (var badge : this.badges) {

        }
    }

    /**
     * Try and add badge to player, if successful, will send a notification that a badge has been added.
     *
     * @param badgeCode the code of the badge to add
     * @param badgeRemove the badge to remove (such as achievements replacing the lower level)
     * @return the badge instance, if successfully added
     */
    public Badge tryAddBadge(String badgeCode, String badgeRemove) {
        return tryAddBadge(badgeCode, badgeRemove, 0);
    }

    /**
     * Try and add badge to player, if successful, will send a notification that a badge has been added.
     *
     * @param badgeCode the code of the badge to add
     * @param badgeRemove the badge to remove (such as achievements replacing the lower level)
     * @param level the level to add
     * @return the badge instance, if successfully added
     */
    public Badge tryAddBadge(String badgeCode, String badgeRemove, int level) {
        if (this.hasBadge(badgeCode)) {
            return null;
        }

        if (badgeRemove != null) {
            this.removeBadge(badgeRemove);
        }

        Badge badge = new Badge(badgeCode, false, 0);
        this.badges.add(badge);

        BadgeDao.newBadge(this.userId, badgeCode);

        if (this.player != null) {
            this.player.send(new ACHIEVEMENT_NOTIFICATION(badgeCode, badgeRemove, level));
        }
        return badge;
    }

    /**
     * Remove the badge by badge code.
     *
     * @param badgeCode the badge code to add
     * @return the instance of the badge removed.
     */
    public Badge removeBadge(String badgeCode) {
        if (!this.hasBadge(badgeCode)) {
            return null;
        }

        Badge badge = this.getBadge(badgeCode);
        this.badges.remove(badge);

        BadgeDao.removeBadge(this.userId, badge.getBadgeCode());
        return badge;
    }

    /**
     * Save badges that have been queued to save.
     */
    public void saveQueuedBadges() {
        BadgeDao.saveBadgeChanges(this.userId, this.badgesToSave);
        this.badgesToSave.clear();
    }

    /**
     * Get the list of all unequipped badges.
     *
     * @return the list of all unequipped badges
     */
    public List<Badge> getUnequippedBadges() {
        return badges.stream().filter(badge -> !badge.isEquipped()).collect(Collectors.toList());
    }

    /**
     * Get the list of all equipped badges.
     *
     * @return the list of equipped badges
     */
    public List<Badge> getEquippedBadges() {
        return badges.stream().filter(Badge::isEquipped).collect(Collectors.toList());
    }

    /**
     * Get all badges.
     *
     * @return the list of all badges
     */
    public List<Badge> getBadges() {
        return badges;
    }
}
