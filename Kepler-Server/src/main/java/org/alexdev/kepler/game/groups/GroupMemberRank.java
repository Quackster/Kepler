package org.alexdev.kepler.game.groups;

public enum GroupMemberRank {
    MEMBER(1, 3),
    ADMINISTRATOR(2, 2),
    OWNER(3, 1);

    private final int rankId;
    private final int clientRank;

    GroupMemberRank(int rankId, int clientRank) {
        this.rankId = rankId;
        this.clientRank = clientRank;
    }

    public static GroupMemberRank getByRankId(int rankId) {
        for (var rank : values()) {
            if (rank.getRankId() == rankId) {
                return rank;
            }
        }

        return null;
    }

    public int getRankId() {
        return rankId;
    }

    public int getClientRank() {
        return clientRank;
    }
}