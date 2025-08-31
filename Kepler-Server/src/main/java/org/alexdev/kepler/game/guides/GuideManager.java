package org.alexdev.kepler.game.guides;

import org.alexdev.kepler.dao.mysql.AlertsDao;
import org.alexdev.kepler.dao.mysql.MessengerDao;
import org.alexdev.kepler.dao.mysql.PlayerStatisticsDao;
import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.achievements.AchievementManager;
import org.alexdev.kepler.game.achievements.AchievementType;
import org.alexdev.kepler.game.alerts.AlertType;
import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.player.statistics.PlayerStatistic;
import org.alexdev.kepler.messages.outgoing.tutorial.INVITING_COMPLETED;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GuideManager {
    public static int MAX_SIMULTANEOUS_GUIDING = 10;
    private static GuideManager instance;
    private final GuideInviteTask guideTask;

    public GuideManager() {
        this.guideTask = new GuideInviteTask();
        GameScheduler.getInstance().getService().scheduleAtFixedRate(this.guideTask, 0, 10, TimeUnit.SECONDS);
    }

    /**
     * Get if guides is disabled.
     *
     * @return true, if successful
     */
    public boolean isDisabled() {
        if (!GameConfiguration.getInstance().getBoolean("tutorial.enabled")) {
            return true;
        }

        int guideGroupId = GameConfiguration.getInstance().getInteger("guides.group.id");

        if (guideGroupId < 1) {
            return true;
        }

        return false;
    }

    /**
     * Get if the user is a guide or not.
     *
     * @param player the player to check
     * @return true, if successful
     */
    public boolean isGuide(Player player) {
        int guideGroupId = GameConfiguration.getInstance().getInteger("guides.group.id");

        if (guideGroupId < 1) {
            return false;
        }

        var guideGroup = player.getJoinedGroup(guideGroupId);

        if (guideGroup == null) {
            return false;
        }

        int daysSinceJoined = (int) Math.floor(TimeUnit.SECONDS.toDays((long) (DateUtil.getCurrentTimeSeconds() - Math.floor(player.getDetails().getJoinDate()))));

        if (!(daysSinceJoined >= 30)) {
            return false;
        }

        return guideGroup.isMember(player.getDetails().getId());
    }

    /**
     * Called when the tutor enters the room.
     *
     * @param guide the guide
     * @param newb the new player
     */
    public void tutorEnterRoom(Player guide, Player newb) {
        if (newb.getStatisticManager().getIntValue(PlayerStatistic.GUIDED_BY) > 0) {
            return;
        }

        //guide.getGuideManager().setHasBubble(true);

        /*newb.getRoomUser().getRoom().send(new MessageComposer() {
            @Override
            public void compose(NettyResponse response) {
                response.writeInt(newb.getRoomUser().getRoom().getId());
            }

            @Override
            public short getHeader() {
                return 424;
            }
        });*/

        newb.send(new INVITING_COMPLETED(INVITING_COMPLETED.InvitationResult.SUCCESS));
        newb.getMessenger().addFriend(new MessengerUser(guide.getDetails()));
        newb.getGuideManager().setGuidable(false);

        newb.getStatisticManager().setLongValue(PlayerStatistic.GUIDED_BY, guide.getDetails().getId());
        newb.getStatisticManager().setLongValue(PlayerStatistic.HAS_TUTORIAL, 0);

        guide.getGuideManager().refreshGuidingUsers();
    }

    /**
     * Try progress guide task upon login.
     *
     * @param player the player to check
     */
    public void tryProgress(Player player) {
        /*if (player.getGuideManager().isGuide()) {
            for (var user : player.getGuideManager().getGuiding()) {
                if (!player.getMessenger().hasFriend(user.getUserId())) {
                    PlayerStatisticsDao.updateStatistic(user.getUserId(), PlayerStatistic.GUIDED_BY, "0");
                }
            }
        } else {
            */
        var statisticsManager = player.getStatisticManager();
        int guideId = statisticsManager.getIntValue(PlayerStatistic.GUIDED_BY);

        if (guideId <= 0) {
            return;
        }

        long onlineTime = TimeUnit.SECONDS.toMinutes(statisticsManager.getLongValue(PlayerStatistic.ONLINE_TIME));
        long timeRequired = GameConfiguration.getInstance().getLong("guide.completion.minutes");
        boolean hasMetOnlineRequirement = onlineTime >= timeRequired;

        if (!hasMetOnlineRequirement) {
            if (!MessengerDao.friendExists(player.getDetails().getId(), guideId)/*player.getMessenger().hasFriend(guideId)*/) {
                statisticsManager.setLongValue(PlayerStatistic.GUIDED_BY, 0);
                statisticsManager.setLongValue(PlayerStatistic.HAS_TUTORIAL, 1);
                statisticsManager.setLongValue(PlayerStatistic.IS_GUIDABLE, 1);
            }
        } else {
            statisticsManager.setLongValue(PlayerStatistic.GUIDED_BY, 0);
            statisticsManager.setLongValue(PlayerStatistic.HAS_TUTORIAL, 0);
            statisticsManager.setLongValue(PlayerStatistic.IS_GUIDABLE, 0);

            PlayerStatisticsDao.incrementStatistic(guideId, PlayerStatistic.PLAYERS_GUIDED, 1);
            int totalGuided = (int) PlayerStatisticsDao.getStatisticLong(guideId, PlayerStatistic.PLAYERS_GUIDED);

            AlertsDao.createAlert(guideId, AlertType.TUTOR_SCORE, "You have just completed guiding another new player! You have now guided a total of " + totalGuided + " players.");
            AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_STUDENT, player);
        }
        //}
    }

    public void checkGuidingFriends(Player player) {
        if (player.getGuideManager().isGuide()) {
            for (var user : player.getGuideManager().getGuiding()) {
                if (!player.getMessenger().hasFriend(user.getUserId())) {
                    PlayerStatisticsDao.updateStatistic(user.getUserId(), PlayerStatistic.GUIDED_BY, "0");
                }
            }
        }
    }

    /**
     * Try and clear tutorial after user doesn't want to do the tutoral nor guides.
     *
     * @param player the player
     */
    public void tryClearTutorial(Player player) {
        player.getGuideManager().setHasTutorial(false);
        player.getGuideManager().setCanUseTutorial(false);
        player.getGuideManager().setCancelTutorial(false);
        player.getStatisticManager().setLongValue(PlayerStatistic.HAS_TUTORIAL, 0);

        if (player.getGuideManager().isGuidable()) {
            player.getGuideManager().setGuidable(false);
        }
    }

    /**
     * Get the list of guides available.
     *
     * @return the list of guides
     */
    public List<Player> getGuidesAvailable() {
        List<Player> guides = new ArrayList<>();

        for (Player player : PlayerManager.getInstance().getPlayers()) {
            if (!player.getGuideManager().isGuide()) {
                continue;
            }

            if (player.getGuideManager().getGuiding().size() >= MAX_SIMULTANEOUS_GUIDING) {
                continue;
            }

            guides.add(player);
        }

        return guides;
    }

    /**
     * Get a list of people waiting for a guide.
     *
     * @return the list of players
     */
    public List<Player> getAvaliableBeginners() {
        List<Player> guides = new ArrayList<>();

        for (Player player : PlayerManager.getInstance().getPlayers()) {
            if (player.getGuideManager().isGuide()) {
                continue;
            }

            if (!player.getGuideManager().isGuidable()) {
                continue;
            }

            if (!player.getGuideManager().isWaitingForGuide()) {
                continue;
            }

            if (player.getRoomUser().getRoom() == null ||
                    player.getRoomUser().getRoom().isPublicRoom() ||
                    !player.getRoomUser().getRoom().isOwner(player.getDetails().getId())) {
                continue;
            }

            guides.add(player);
        }

        return guides;
    }

    /**
     * Gets the instance
     *
     * @return the instance
     */
    public static GuideManager getInstance() {
        if (instance == null) {
            instance = new GuideManager();
        }

        return instance;
    }
}
