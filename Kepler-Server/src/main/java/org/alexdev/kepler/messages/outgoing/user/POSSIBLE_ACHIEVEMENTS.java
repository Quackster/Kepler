package org.alexdev.kepler.messages.outgoing.user;

import org.alexdev.kepler.game.achievements.AchievementInfo;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;
import org.alexdev.kepler.util.StringUtil;

import java.util.List;

public class POSSIBLE_ACHIEVEMENTS extends MessageComposer {
    private List<AchievementInfo> possibleAchievements;

    public POSSIBLE_ACHIEVEMENTS(List<AchievementInfo> possibleAchievements) {
        this.possibleAchievements = possibleAchievements;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.possibleAchievements.size());

        for (var achievement : this.possibleAchievements) {
            response.writeInt(achievement.getId());
            response.writeInt(achievement.getLevel());

            if (achievement.getName().equals("GL")) {
                response.writeString(String.format("%s%s", achievement.getName(), StringUtil.toAlphabetic(achievement.getLevel())));
            } else {
                response.writeString(String.format("%s%d", achievement.getName(), achievement.getLevel()));
            }

        }
    }

    @Override
    public short getHeader() {
        return 436;
    }
}
