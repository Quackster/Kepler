package net.h4bbo.kepler.game.groups;

import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.PlayerManager;

public class GroupMember {
    private int userId;
    private int groupId;
    private boolean isPending;
    private GroupMemberRank memberRank;
    private PlayerDetails playerData;

    public GroupMember(int userId, int groupId, boolean isPending, int memberRank) {
        this.userId = userId;
        this.groupId = groupId;
        this.isPending = isPending;
        this.memberRank = GroupMemberRank.getByRankId(memberRank);
        this.playerData = PlayerManager.getInstance().getPlayerData(this.userId);
    }

    public int getUserId() {
        return userId;
    }

    public PlayerDetails getUser() {
        return playerData;
    }

    public int getGroupId() {
        return groupId;
    }

    public boolean isPending() {
        return isPending;
    }

    public boolean isFavourite(int groupId) {
        return this.playerData.getFavouriteGroupId() == groupId;
    }

    public GroupMemberRank getMemberRank() {
        return memberRank;
    }
}
