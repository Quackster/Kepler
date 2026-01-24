package net.h4bbo.kepler.game.player.statistics;

public enum PlayerStatistic {
    DAYS_LOGGED_IN_ROW("days_logged_in_row"),
    GUESTBOOK_UNREAD_MESSAGES("guestbook_unread_messages"),
    ONLINE_TIME("online_time"),
    BATTLEBALL_POINTS_ALL_TIME("battleball_score_all_time"),
    SNOWSTORM_POINTS_ALL_TIME("snowstorm_score_all_time"),
    WOBBLE_SQUABBLE_POINTS_ALL_TIME("wobble_squabble_score_all_time"),
    BATTLEBALL_MONTHLY_SCORES("battleball_score_month"),
    SNOWSTORM_MONTHLY_SCORES("snowstorm_score_month"),
    WOBBLE_SQUABBLE_MONTHLY_SCORES("wobble_squabble_score_month"),
    XP_EARNED_MONTH("xp_earned_month"),
    XP_ALL_TIME("xp_all_time"),
    BATTLEBALL_GAMES_WON("battleball_games_won"),
    SNOWSTORM_GAMES_WON("snowstorm_games_won"),
    WOBBLE_SQUABBLE_GAMES_WON("wobble_squabble_games_won"),
    GUIDED_BY("guided_by"),
    HAS_TUTORIAL("has_tutorial"),
    IS_GUIDABLE("is_guidable"),
    PLAYERS_GUIDED("players_guided"),
    NEWBIE_ROOM_LAYOUT("newbie_room_layout"),
    NEWBIE_GIFT("newbie_gift"),
    NEWBIE_GIFT_TIME("newbie_gift_time"),
    GIFTS_DUE("gifts_due"),
    CLUB_GIFT_DUE("club_gift_due", true),
    CLUB_MEMBER_TIME("club_member_time"),
    CLUB_MEMBER_TIME_UPDATED("club_member_time_updated"),
    ACTIVATION_CODE("activation_code"),
    FORGOT_PASSWORD_CODE("forgot_password_code"),
    FORGOT_RECOVERY_REQUESTED_TIME("forgot_recovery_requested_time"),
    MUTE_EXPIRES_AT("mute_expires_at");

    private final String column;
    private final boolean isDateTime;

    PlayerStatistic(String column) {
        this.column = column;
        this.isDateTime = false;
    }

    PlayerStatistic(String column, boolean isDateTime) {
        this.column = column;
        this.isDateTime = isDateTime;
    }

    public boolean isDateTime() {
        return isDateTime;
    }

    public String getColumn() {
        return column;
    }
}