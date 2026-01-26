package net.h4bbo.kepler.game.achievements.user;

import net.h4bbo.kepler.dao.mysql.AchievementDao;
import net.h4bbo.kepler.game.achievements.AchievementInfo;
import net.h4bbo.kepler.game.achievements.AchievementManager;
import net.h4bbo.kepler.game.achievements.AchievementType;
import net.h4bbo.kepler.game.club.ClubSubscription;
import net.h4bbo.kepler.game.groups.GroupMemberRank;
import net.h4bbo.kepler.game.guides.GuideManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UserAchievementManager {
    private int playerId;
    private List<UserAchievement> userAchievements;

    /**
     * Load the user achievements by the player.
     *
     * @param playerId the player to load
     */
    public void loadAchievements(int playerId) {
        this.playerId = playerId;
        this.userAchievements = AchievementDao.getUserAchievements(playerId);
    }

    /**
     * Find user achievement by given name.
     *
     * @param achievementType the achievement type
     * @return the user achievement
     */
    public UserAchievement locateAchievement(AchievementType achievementType) {
        if (this.userAchievements == null) {
            return null;
        }

        var optional = this.userAchievements.stream()
                .filter(ach -> ach.getAchievementInfo().getName().equals(achievementType.getName()))
                .sorted(Comparator.comparingInt(ach -> ach.getAchievementInfo().getLevel()))
                .collect(Collectors.toList());

        UserAchievement latestAchievement = null;

        if (optional.size() > 0) {
            latestAchievement = optional.get(optional.size() - 1);
        }

        if (latestAchievement != null) {
            // User has already completed the achievement? Try add the next achievement else return nothing
            if (latestAchievement.getProgress() >= latestAchievement.getAchievementInfo().getProgressRequired()) {
                var nextAchievement = AchievementManager.getInstance().locateAchievement(achievementType, latestAchievement.getAchievementInfo().getLevel() + 1);

                if (nextAchievement != null) {
                    var userAchievement = new UserAchievement(nextAchievement.getId(), playerId, 0);
                    AchievementDao.newUserAchievement(playerId, userAchievement);

                    this.userAchievements.add(userAchievement);
                    return userAchievement;
                } else {
                    return null;
                }
            } else {
                return latestAchievement;
            }

        } else {
            int achievementId = AchievementManager.getInstance().locateAchievement(achievementType, 1).getId();

            var userAchievement = new UserAchievement(achievementId, playerId, 0);
            AchievementDao.newUserAchievement(playerId, userAchievement);

            this.userAchievements.add(userAchievement);
            return userAchievement;
        }
    }

    /**
     * Generate the list of possible achievements a user can get.
     *
     * @return the list of possible achievements
     */
    public List<AchievementInfo> getPossibleAchievements() {
        List<AchievementInfo> possibleAchievements = new ArrayList<>();

        for (var achievementInfo : AchievementManager.getInstance().getAchievements().values()) {
            if (this.userAchievements.stream().anyMatch(userAchievement ->
                    userAchievement.getAchievementInfo().getName().equals(achievementInfo.getName()) &&
                    userAchievement.getProgress() >= achievementInfo.getProgressRequired())) {
                continue;
            }

            long badgeCount = possibleAchievements.stream().filter(userAchievement -> userAchievement.getName().equals(achievementInfo.getName())).count();

            if (badgeCount >= 5) {
                continue;
            }

            if (this.userAchievements.stream().anyMatch(userAchievement -> userAchievement.getAchievementInfo().getName().equals(achievementInfo.getName()) && userAchievement.getProgress() >= achievementInfo.getProgressRequired())) {
                var optionalUserAchievement = this.userAchievements.stream().filter(userAchievement -> userAchievement.getAchievementInfo().getName().equals(achievementInfo.getName())).findFirst();

                if (optionalUserAchievement.isPresent()) {
                    var foundAchievement = optionalUserAchievement.get();
                    var newAchievement = AchievementManager.getInstance().locateAchievement(AchievementType.getByName(achievementInfo.getName()), foundAchievement.getAchievementInfo().getLevel() + 1);

                    if (newAchievement != null) {
                        possibleAchievements.add(newAchievement);
                    }
                }
            } else {
                possibleAchievements.add(achievementInfo);
            }
        }

        possibleAchievements.sort(Comparator.comparing(achievementInfo -> (achievementInfo.getName() + achievementInfo.getLevel())));
        return possibleAchievements;
    }

    /**
     * Get the user achievements.
     *
     * @return the user achievements
     */
    public List<UserAchievement> getUserAchievements() {
        return userAchievements;
    }

    /**
     * Get the achievement by level.
     */
    public UserAchievement getAchievement(AchievementType achievementType, int level) {
        for (var achievement : userAchievements) {
            if (achievement.getAchievementInfo().getName().equals(achievementType.getName())) {
                if (achievement.getAchievementInfo().getLevel() == level) {
                    return achievement;
                }
            }
        }

        return null;
    }

    /**
     * Get the max completed achievement the user has by achievement name
     */
    public UserAchievement getLatestAchievement(AchievementType achievementType) {
        List<UserAchievement> achievements = this.userAchievements.stream().filter(achievement -> achievement.getAchievementInfo().getName().equals(achievementType.getName()))
                .sorted(Comparator.comparingInt((UserAchievement achievement) -> achievement.getAchievementInfo().getLevel()).reversed())
                .collect(Collectors.toList());

        achievements.removeIf(achievement -> achievement.getAchievementInfo().getProgressRequired() > achievement.getProgress());
        return achievements.size() > 0 ? achievements.get(0) : null;
    }

    /**
     * Process any achievements when user logs in again.
     */
    public void processAchievements(Player player, boolean isLogin) {
        if (isLogin) {
            AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_TAGS, player);
            AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_LOGIN, player);
            AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_HAPPYHOUR, player);
        }

        AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_HC, player);
        // AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_MGM, player);
        AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_REGISTRATION_DURATION, player);
        AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_HC, player);
        // AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_GUIDE, player);
        // AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_ALL_TIME_HOTEL_PRESENCE, player);
        // AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_TRADERPASS, player);
        AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_EMAIL_VERIFICATION, player);

        GuideManager.getInstance().tryProgress(player);
        GuideManager.getInstance().checkGuidingFriends(player);

        ClubSubscription.checkBadges(player);

        // Habbo Guide admins
        if (player.getGuideManager().isGuide()) {
            if (!player.getBadgeManager().hasBadge("GLK")) {
                int guideGroupId = GameConfiguration.getInstance().getInteger("guides.group.id");
                var groupMember = player.getJoinedGroup(guideGroupId).getMember(player.getDetails().getId());

                if (groupMember != null &&
                        (groupMember.getMemberRank() == GroupMemberRank.ADMINISTRATOR ||
                        groupMember.getMemberRank() == GroupMemberRank.OWNER)) {
                    player.getBadgeManager().tryAddBadge("GLK", null);
                } else {
                    player.getGuideManager().setGuide(GuideManager.getInstance().isGuide(player));
                }
            }
        }

        /*

        // Habbo eXperts
        if (GameConfiguration.getInstance().getInteger("habbo.experts.group.id") > 0) {
            if (!player.getBadgeManager().hasBadge("XXX")) {
                int expertsGroupId = GameConfiguration.getInstance().getInteger("habbo.experts.group.id");
                var group = player.getJoinedGroup(expertsGroupId);

                if (group != null) {
                    var expertsMember = group.getMember(player.getDetails().getId());

                    if (expertsMember != null) {
                        player.getBadgeManager().tryAddBadge("XXX", null);
                    }
                }
            }
        }

        // ChildLine
        if (GameConfiguration.getInstance().getInteger("childline.group.id") > 0) {
            if (!player.getBadgeManager().hasBadge("UK176")) {
                int expertsGroupId = GameConfiguration.getInstance().getInteger("childline.group.id");
                var group = player.getJoinedGroup(expertsGroupId);

                if (group != null) {
                    var expertsMember = group.getMember(player.getDetails().getId());

                    if (expertsMember != null) {
                        player.getBadgeManager().tryAddBadge("UK176", null);
                    }
                }
            }
        }

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(player.getDetails().getJoinDate() * 1000);

        //System.out.println("Join date: " + currentCalendar.get(Calendar.YEAR) + " / " + currentCalendar.get(Calendar.MONTH) + " / " + currentCalendar.get(Calendar.DAY_OF_MONTH));

        long currentTime = DateUtil.getCurrentTimeSeconds();
        //long time = DateUtil.getFromFormat(DateUtil.SHORT_DATE, "11-11-2019");

        if ((currentTime > DateUtil.getFromFormat(DateUtil.SHORT_DATE, "1-12-2019")) && (currentTime < DateUtil.getFromFormat(DateUtil.SHORT_DATE, "31-12-2019"))) {
            player.getBadgeManager().tryAddBadge("XM19", null);
        }

        long joinDate = player.getDetails().getJoinDate();

        if ((joinDate > DateUtil.getFromFormat(DateUtil.SHORT_DATE, "10-12-2019")) && (joinDate < DateUtil.getFromFormat(DateUtil.SHORT_DATE, "18-12-2019"))) {
            player.getBadgeManager().tryAddBadge("MRG00", null);
        }

        // If joined opening day
        if ((joinDate > DateUtil.getFromFormat(DateUtil.SHORT_DATE, "10-12-2019")) && (joinDate <= DateUtil.getFromFormat(DateUtil.SHORT_DATE, "12-12-2019"))) {
            player.getBadgeManager().tryAddBadge("Z64", null);
        }

        // Remove special badge.
        //player.getBadgeManager().tryAddBadge("Z64", null);


         */
    }
}
