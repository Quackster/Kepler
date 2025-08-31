package org.alexdev.kepler.game.club;

import org.alexdev.kepler.dao.mysql.ClubGiftDao;
import org.alexdev.kepler.dao.mysql.CurrencyDao;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.dao.mysql.PlayerStatisticsDao;
import org.alexdev.kepler.game.catalogue.CatalogueManager;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.statistics.PlayerStatistic;
import org.alexdev.kepler.game.player.statistics.PlayerStatisticManager;
import org.alexdev.kepler.messages.outgoing.club.CLUB_INFO;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class ClubSubscription {
    public static String[] giftOrder = new String[]{
            "hc_tv",
            "hcamme",
            "hc_crtn",
            "mocchamaster",
            "hc_crpt",
            "edicehc",
            "hc_wall_lamp",
            "doorD",
            "deal_hcrollers",
            "hcsohva",
            "hc_bkshlf",
            "hc_lmp",
            "hc_trll",
            "hc_tbl",
            "hc_machine",
            "hc_chr",
            "hc_rntgn",
            "hc_dsk",
            "hc_djset",
            "hc_lmpst",
            "hc_frplc",
            "hc_btlr"
    };

    /**
     * Refresh the club scription for player.
     * @param player the player to refresh the subscription for
     */
    public static void sendHcDays(Player player) {
        long now = DateUtil.getCurrentTimeSeconds();

        int sinceMonths = 0;
        int totalDays = 0;
        int remainingDaysThisMonth = 0;
        int prepaidMonths = 0;

        if (player.getDetails().getClubExpiration() != 0)
            totalDays = (int)((player.getDetails().getClubExpiration() - now) / 60 / 60 / 24);

        if (totalDays < 0)
            totalDays = 0;

        if (totalDays > 0) {
            remainingDaysThisMonth = ((totalDays - 1) % 31) + 1;
            prepaidMonths = (totalDays - remainingDaysThisMonth) / 31;

            if (player.getDetails().getFirstClubSubscription() > 0) {
                int days = (int) TimeUnit.SECONDS.toDays(player.getStatisticManager().getLongValue(PlayerStatistic.CLUB_MEMBER_TIME));
                sinceMonths = days > 0 ? days / 31 : 0;//(int) (now - player.getDetails().getFirstClubSubscription()) / 60 / 60 / 24 / 31;
            }
        }

        player.send(new CLUB_INFO(remainingDaysThisMonth, sinceMonths, prepaidMonths));
    }

    /**
     * Subscribe to Habbo club with credits and days indicated, if 0 days, the function will not proceed.
     * If the credits amount is 0 or less then no credits will be charged.
     *
     * @param playerDetails the details of the player that subscribed
     * @param choice the subscription choice
     */
    public static boolean subscribeClub(PlayerDetails playerDetails, int choice) throws SQLException {
        var choiceData = getChoiceData(choice);

        int credits = choiceData.getKey();
        int days = choiceData.getValue();

        if (days <= 0) {
            return false;
        }

        if (playerDetails.getCredits() < credits) {
            return false;
        }

        long now = DateUtil.getCurrentTimeSeconds();

        long daysInSeconds = 24 * 60 * 60;
        long secondsToAdd = (daysInSeconds * days);

        if (playerDetails.getFirstClubSubscription() == 0) {
            PlayerStatisticsDao.updateStatistic(playerDetails.getId(), PlayerStatistic.GIFTS_DUE, 1);
            PlayerStatisticsDao.updateStatistic(playerDetails.getId(), PlayerStatistic.CLUB_GIFT_DUE, now);
        }

        /*if (playerDetails.getFirstClubSubscription() == 0) {
            playerDetails.setFirstClubSubscription(now);

            // Club Sofa on first ever HC purchase
            ItemManager.getInstance().createGift(playerDetails, "club_sofa", GameConfiguration.getInstance().getString("club.gift.present.label"));

            // Set new club received date
            playerDetails.setClubGiftDue(DateUtil.getCurrentTimeSeconds() + getClubGiftSeconds());
            ClubGiftDao.saveNextGiftDate(playerDetails);
        }*/

        if (playerDetails.getClubExpiration() - now <= 0) {
            playerDetails.setClubExpiration(now + secondsToAdd + 1);
        } else {
            playerDetails.setClubExpiration(playerDetails.getClubExpiration() + secondsToAdd);
        }

        PlayerDao.saveSubscription(playerDetails.getId(), playerDetails.getFirstClubSubscription(), playerDetails.getClubExpiration());
        CurrencyDao.decreaseCredits(playerDetails, credits);

        /*
        TransactionDao.createTransaction(
                playerDetails.getId(),
                "0",
                "0",
                days,
                "Habbo Club purchase",
                credits,
                0,
                true
        );

         */
        return true;
    }

    public static boolean isGiftDue(Player player) {
        if (!player.getDetails().hasClubSubscription()) {
            return false;
        }

        if (player.getDetails().getFirstClubSubscription() == 0) {
            return true;
        }

        if (player.getStatisticManager().getIntValue(PlayerStatistic.GIFTS_DUE) > 0) {
            return true;
        }

        return true;
    }

    public static void tryNextGift(Player player) throws SQLException {
        if (!isGiftDue(player)) {
            return;
        }

        Item item = null;

        if (player.getDetails().getFirstClubSubscription() == 0) {
            player.getDetails().setFirstClubSubscription(DateUtil.getCurrentTimeSeconds());
            item = ItemManager.getInstance().createGift(player.getDetails().getId(), player.getDetails().getName(), "club_sofa", GameConfiguration.getInstance().getString("club.gift.present.label"), "");

            PlayerDao.saveSubscription(player.getDetails().getId(), player.getDetails().getFirstClubSubscription(), player.getDetails().getClubExpiration());
        } else {
            String giftData = player.getLastGift();

            if (giftData == null) {
                var result = ClubGiftDao.getLastGift(player.getDetails().getId());

                if (result != null) {
                    giftData = result.getValue();
                }
            }

            String nextSpriteGift;

            if (giftData == null) {
                nextSpriteGift = giftOrder[0];
            } else {
                int position = 0;

                for (String nextGift : giftOrder) {
                    position++;

                    if (nextGift.equals(giftData)) {
                        break;
                    }
                }

                if (position >= giftOrder.length) {
                    position = 0;
                }

                nextSpriteGift = giftOrder[position];
            }

            ClubGiftDao.addGift(player.getDetails().getId(), nextSpriteGift);
            player.setLastGift(nextSpriteGift);

            item = ItemManager.getInstance().createGift(player.getDetails().getId(), player.getDetails().getName(), nextSpriteGift, GameConfiguration.getInstance().getString("club.gift.present.label"), "");
        }

        //player.getStatisticManager().setLongValue(PlayerStatistic.CLUB_GIFT_DUE, DateUtil.getCurrentTimeSeconds() + getClubGiftSeconds());
        player.getStatisticManager().incrementValue(PlayerStatistic.GIFTS_DUE, -1);

        //player.getDetails().setClubGiftDue(DateUtil.getCurrentTimeSeconds() + getClubGiftSeconds());
        //ClubGiftDao.saveNextGiftDate(player.getDetails());

        player.getInventory().addItem(item);
        player.getInventory().getView("new");

        /*
        var catalogueItem = CatalogueManager.getInstance().getCatalogueItem(item.getDefinition().getSprite());

        if (catalogueItem != null) {
            TransactionDao.createTransaction(player.getDetails().getId(), item.getDatabaseId() + "", catalogueItem.getId() + "",
                    catalogueItem.getAmount(), "Habbo Club membership gift",  0, 0, true);
        }*/
    }

    /**
     * Get the choice data for HC.
     *
     * @param choice the choice, 1, 2 or 3
     * @return the pair, days/credits
     */
    public static Pair<Integer, Integer> getChoiceData(int choice) {
        int days = -1;
        int credits = -1;

        switch (choice) {
            case 1:
            {
                credits = 25;
                days = 31;
                break;
            }
            case 2:
            {
                credits = 60;
                days = 93;
                break;
            }
            case 3:
            {
                credits = 105;
                days = 186;
                break;
            }
        }

        return Pair.of(credits, days);
    }

    /**
     * Reset figure on HC expiry.
     *
     * @param details the details to set
     */
    public static void resetClothes(PlayerDetails details) {
        if (details.getSex().equals("M")) {
            details.setFigure("hd-180-1.ch-215-62.lg-275-62.hr-100-");
            details.setSex("M");
        } else {
            details.setFigure("hd-600-1.ch-645-62.lg-700-62.sh-730-68.hr-500-45");
            details.setSex("F");
        }

        PlayerDao.saveDetails(details.getId(), details.getFigure(), details.getPoolFigure(), details.getSex());
        //hd-180-1.ch-215-62.lg-275-62.hr-100-
    }

    /**
     * Get the offset of seconds required until the next gift is allowed.
     *
     * @return the offset seconds
     */
    public static long getClubGiftSeconds() {
        return TimeUnit.valueOf(GameConfiguration.getInstance().getString("club.gift.timeunit")).toSeconds(GameConfiguration.getInstance().getInteger("club.gift.interval"));
    }

    /**
     * Add badges if required for Habbo Club.
     *
     * @param player the player to check and add badges to
     */
    public static void checkBadges(Player player) {
        if (player.getDetails().hasClubSubscription()) {
            if (!player.getBadgeManager().hasBadge("HC1")) {
                player.getBadgeManager().tryAddBadge("HC1", null);
            }
        }

        if (hasGoldClubSubscription(player)) {
            if (!player.getBadgeManager().hasBadge("HC2")) {
                player.getBadgeManager().tryAddBadge("HC2", null);
            }
        }

        if (hasPlatinumClubSubscription(player)) {
            if (!player.getBadgeManager().hasBadge("HC3")) {
                player.getBadgeManager().tryAddBadge("HC3", null);
            }
        }
    }

    /**
     * Get the sprites of the gift order.
     *
     * @return the array of gifts in order for each month
     */
    public static String[] getGiftOrder() {
        return giftOrder;
    }

    /**
     * Count member days and increase days.
     *
     * @param player the player to count for
     */
    public static void countMemberDays(Player player) {
        countMemberDays(player.getDetails(), player.getStatisticManager());
    }

    /**
     * Count member days and increase days.
     *
     * @param playerDetails the playerdetails
     */
    public static void countMemberDays(PlayerDetails playerDetails, PlayerStatisticManager playerStatisticManager) {
        if (playerDetails.hasClubSubscription()) {
            long lastUpdated = playerStatisticManager.getLongValue(PlayerStatistic.CLUB_MEMBER_TIME_UPDATED);

            if (lastUpdated > 0) {
                PlayerStatisticsDao.updateStatistic(playerDetails.getId(), PlayerStatistic.CLUB_MEMBER_TIME,
                        String.valueOf(playerStatisticManager.getLongValue(PlayerStatistic.CLUB_MEMBER_TIME) + DateUtil.getCurrentTimeSeconds() - lastUpdated));
            }

            PlayerStatisticsDao.updateStatistic(playerDetails.getId(), PlayerStatistic.CLUB_MEMBER_TIME_UPDATED,
                    String.valueOf(DateUtil.getCurrentTimeSeconds()));
        }
    }

    public static boolean hasGoldClubSubscription(Player player) {
        if (player.getDetails().hasClubSubscription()) {
            int days = (int) TimeUnit.SECONDS.toDays(player.getStatisticManager().getLongValue(PlayerStatistic.CLUB_MEMBER_TIME));
            int sinceMonths = days > 0 ? days / 31 : 0;

            // We are deemed a 'Gold' Club member if the user has been a club subscriber for a year
            // According to the HabboX wiki the badge is to be received on the first day of the 13th subscribed month
            return sinceMonths >= 12;
        }

        return false;
    }

    public static boolean hasPlatinumClubSubscription(Player player) {
        if (player.getDetails().hasClubSubscription()) {
            int days = (int) TimeUnit.SECONDS.toDays(player.getStatisticManager().getLongValue(PlayerStatistic.CLUB_MEMBER_TIME));
            int sinceMonths = days > 0 ? days / 31 : 0;

            // We are deemed a 'Gold' Club member if the user has been a club subscriber for a year
            // According to the HabboX wiki the badge is to be received on the first day of the 13th subscribed month
            return sinceMonths >= 24;
        }

        return false;
    }

}
