package org.alexdev.kepler.game.player;

public enum PlayerRank {
    RANKLESS(0),
    NORMAL(1),
    COMMUNITY_MANAGER(2),
    GUIDE(3),
    HOBBA(4),
    SUPERHOBBA(5),
    MODERATOR(6),
    ADMINISTRATOR(7);

    private final int rankId;

    PlayerRank(int rankId) {
        this.rankId = rankId;
    }

    public String getName() {
        return this.name();
    }

    public int getRankId() {
        return this.rankId;
    }

    public static PlayerRank getRankForId(int rankId) {
        for (PlayerRank rank : PlayerRank.values()) {
            if (rank.getRankId() == rankId) {
                return rank;
            }
        }

        return null;
    }
}
