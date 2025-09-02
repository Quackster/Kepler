package net.h4bbo.http.kepler.game.housekeeping;

import net.h4bbo.kepler.game.player.PlayerRank;
import net.h4bbo.http.kepler.game.stickers.StickerManager;

import java.util.HashMap;
import java.util.Map;

public class HousekeepingManager {
    private static HousekeepingManager instance;
    private Map<String, PlayerRank> permissions;

    public HousekeepingManager() {
        this.loadPermissions();
    }

    private void loadPermissions() {
        this.permissions = new HashMap<>();
        this.permissions.put("root/login", PlayerRank.MODERATOR);
        this.permissions.put("transaction/lookup", PlayerRank.MODERATOR);
        this.permissions.put("marketplace/log_check", PlayerRank.MODERATOR);
        this.permissions.put("marketplace/user_log", PlayerRank.MODERATOR);
        this.permissions.put("bans", PlayerRank.MODERATOR);
        this.permissions.put("user/search", PlayerRank.ADMINISTRATOR);
        this.permissions.put("user/edit", PlayerRank.ADMINISTRATOR);
        this.permissions.put("user/create", PlayerRank.ADMINISTRATOR);
        this.permissions.put("articles/create", PlayerRank.MODERATOR);
        this.permissions.put("articles/edit_any", PlayerRank.ADMINISTRATOR);
        this.permissions.put("articles/edit_own", PlayerRank.MODERATOR);
        this.permissions.put("articles/delete_any", PlayerRank.ADMINISTRATOR);
        this.permissions.put("articles/delete_own", PlayerRank.MODERATOR);
        this.permissions.put("room_ads", PlayerRank.ADMINISTRATOR);
        this.permissions.put("room_badges", PlayerRank.COMMUNITY_MANAGER);
        this.permissions.put("configuration", PlayerRank.ADMINISTRATOR);
        this.permissions.put("infobus", PlayerRank.COMMUNITY_MANAGER);
        this.permissions.put("infobus/delete_any", PlayerRank.ADMINISTRATOR);
        this.permissions.put("infobus/delete_own", PlayerRank.COMMUNITY_MANAGER);
        this.permissions.put("catalogue/edit_frontpage", PlayerRank.COMMUNITY_MANAGER);
        this.permissions.put("user/imitate", PlayerRank.ADMINISTRATOR);
        this.permissions.put("user/matches", PlayerRank.ADMINISTRATOR);
        this.permissions.put("badges", PlayerRank.COMMUNITY_MANAGER);
    }

    /**
     * Get instance of {@link StickerManager}
     *
     * @return the manager instance
     */
    public static HousekeepingManager getInstance() {
        if (instance == null) {
            instance = new HousekeepingManager();
        }

        return instance;
    }

    public boolean hasPermission(PlayerRank rank, String permission) {
        if (this.permissions.containsKey(permission)) {
            var permissibleRank = this.permissions.get(permission);
            return rank.getRankId() >= permissibleRank.getRankId();
        }

        return false;
    }
}
