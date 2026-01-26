package net.h4bbo.kepler.game.achievements;

import net.h4bbo.kepler.game.achievements.progressions.*;

public enum AchievementType {
    ACHIEVEMENT_LOOKS("ACH_AvatarLooks", new AchievementAvatarLooks(), true),
    ACHIEVEMENT_HC("HC", new AchievementHabboClub(), true),
    ACHIEVEMENT_MOTTO("ACH_Motto", new AchievementMotto(), true),
    // ACHIEVEMENT_RESPECT_GIVEN("ACH_RespectGiven", new AchievementRespectGiven(), true),
    ACHIEVEMENT_TAGS("ACH_AvatarTags", new AchievementTags(), true),
    //ACHIEVEMENT_MGM("ACH_MGM", new AchievementMGM(), true),
    ACHIEVEMENT_GRADUATE("ACH_Graduate", new AchievementGraduate(), true),
    ACHIEVEMENT_HAPPYHOUR("ACH_HappyHour", new AchievementHappyHour(), true),
    ACHIEVEMENT_REGISTRATION_DURATION("ACH_RegistrationDuration", new AchievementRegistrationDuration(), true),
    ACHIEVEMENT_ROOMENTRY("ACH_RoomEntry", new AchievementRoomEntry(), true),
    // ACHIEVEMENT_TRADERPASS("ACH_TraderPass", new AchievementTraderPass(), true),
    // ACHIEVEMENT_AIPERFORMANCEVOTE("ACH_AIPerformanceVote", new AchievementAIPerformanceVote(), true),
    // ACHIEVEMENT_RESPECT_EARNED("ACH_RespectEarned", new AchievementRespectEarned(), true),
    ACHIEVEMENT_LOGIN("ACH_Login", new AchievementLogin(), true),
    // ACHIEVEMENT_ALL_TIME_HOTEL_PRESENCE("ACH_AllTimeHotelPresence", new AchievementAllTimeHotelPresence(), true),
    ACHIEVEMENT_GAME_PLAYED("ACH_GamePlayed", new AchievementGamePlayed(), true),
    // ACHIEVEMENT_GUIDE("GL", new AchievementGuide(), false),
    ACHIEVEMENT_STUDENT("ACH_Student", new AchievementStudent(), true),
    ACHIEVEMENT_EMAIL_VERIFICATION("ACH_EmailVerification", new AchievementEmailVerification(), true);

    private final String avchievementName;
    private final AchievementProgress achievementProgressor;
    private final boolean removePreviousAchievement;

    AchievementType(String achievementName, AchievementProgress achievementProgressor, boolean removePreviousAchievement) {
        this.avchievementName = achievementName;
        this.achievementProgressor = achievementProgressor;
        this.removePreviousAchievement = removePreviousAchievement;
    }

    public static AchievementType getByName(String name) {
        for (AchievementType achievementType : values()) {
            if (achievementType.getName().equals(name)) {
                return achievementType;
            }
        }

        return null;
    }

    public String getName() {
        return avchievementName;
    }

    public AchievementProgress getProgressor() {
        return achievementProgressor;
    }

    public boolean hasRemovePreviousAchievement() {
        return removePreviousAchievement;
    }
}
