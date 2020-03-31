package org.alexdev.kepler.game.club;

import org.alexdev.kepler.dao.mysql.ClubGiftDao;
import org.alexdev.kepler.dao.mysql.CurrencyDao;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.dao.mysql.PurseDao;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.badges.AVAILABLE_BADGES;
import org.alexdev.kepler.messages.outgoing.user.CLUB_INFO;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.TimeUnit;

public class ClubSubscription {
    private static String[] giftOrder = new String[]{
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
                sinceMonths = (int) (now - player.getDetails().getFirstClubSubscription()) / 60 / 60 / 24 / 31;
            }
        }

        player.send(new CLUB_INFO(remainingDaysThisMonth, sinceMonths, prepaidMonths));
    }

    /**
     * Subscribe to Habbo club with credits and days indicated, if 0 days, the function will not proceed.
     * If the credits amount is 0 or less then no credits will be charged.
     *
     * @param player the player to subscribe to club
     * @param days the amount of days to subscribe for
     * @param credits the amount of credits cost
     */
    public static void subscribeClub(Player player, int days, int credits) throws Exception {
        if (days <= 0) {
            return;
        }

        if (player.getDetails().getCredits() < credits) {
            return;
        }

        long now = DateUtil.getCurrentTimeSeconds();

        long daysInSeconds = 24 * 60 * 60;
        long secondsToAdd = (daysInSeconds * days);

        if (player.getDetails().getFirstClubSubscription() == 0) {
            player.getDetails().setFirstClubSubscription(now);

            Item item = ItemManager.getInstance().createGift(player.getDetails(), player.getDetails(), "club_sofa", GameConfiguration.getInstance().getString("club.gift.present.label"), "");

            player.getDetails().setClubGiftDue(DateUtil.getCurrentTimeSeconds() + getClubGiftSeconds());
            ClubGiftDao.saveNextGiftDate(player.getDetails());

            player.getInventory().addItem(item);
            player.getInventory().getView("new");
        }

        if (player.getDetails().getClubExpiration() - now <= 0) {
            player.getDetails().setClubExpiration(now + secondsToAdd + 1);
        } else {
            player.getDetails().setClubExpiration(player.getDetails().getClubExpiration() + secondsToAdd);
        }

        player.refreshClub();
        player.refreshFuserights();

        PlayerDao.saveSubscription(player.getDetails());

        if (credits > 0) {
            PurseDao.logCreditSpend("club_habbo", player.getDetails(), -credits);
            CurrencyDao.decreaseCredits(player.getDetails(), credits);
            player.send(new CREDIT_BALANCE(player.getDetails()));
        }
    }

    public static boolean isGiftDue(Player player) {
        if (!player.getDetails().hasClubSubscription()) {
            return false;
        }

        if (player.getDetails().getFirstClubSubscription() == 0) {
            return true;
        }

        if (DateUtil.getCurrentTimeSeconds() < player.getDetails().getClubGiftDue()) {
            return false;
        }

        return true;
    }

    public static void tryNextGift(Player player) throws Exception {
        if (!isGiftDue(player)) {
            return;
        }

        Item item = null;

        if (player.getDetails().getFirstClubSubscription() == 0) {
            player.getDetails().setFirstClubSubscription(DateUtil.getCurrentTimeSeconds());
            item = ItemManager.getInstance().createGift(player.getDetails(), player.getDetails(), "club_sofa", GameConfiguration.getInstance().getString("club.gift.present.label"), "");

            PlayerDao.saveSubscription(player.getDetails());
        } else {
            var giftData = ClubGiftDao.getLastGift(player.getDetails().getId());
            String nextSpriteGift;

            if (giftData == null) {
                nextSpriteGift = giftOrder[0];
            } else {
                int position = 0;

                for (String nextGift : giftOrder) {
                    position++;

                    if (nextGift.equals(giftData.getValue())) {
                        break;
                    }
                }

                if (position >= giftOrder.length) {
                    position = 0;
                }

                nextSpriteGift = giftOrder[position];
            }

            ClubGiftDao.addGift(player.getDetails().getId(), nextSpriteGift);
            item = ItemManager.getInstance().createGift(player.getDetails(), player.getDetails(), nextSpriteGift, GameConfiguration.getInstance().getString("club.gift.present.label"), "");
        }

        player.getDetails().setClubGiftDue(DateUtil.getCurrentTimeSeconds() + getClubGiftSeconds());
        ClubGiftDao.saveNextGiftDate(player.getDetails());

        player.getInventory().addItem(item);
        player.getInventory().getView("new");
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
     * Get the offset of seconds required until the next gift is allowed.
     *
     * @return the offset seconds
     */
    private static long getClubGiftSeconds() {
        return TimeUnit.valueOf(GameConfiguration.getInstance().getString("club.gift.timeunit")).toSeconds(GameConfiguration.getInstance().getInteger("club.gift.interval"));
    }

    /**
     * Refresh club page for user.
     *
     * @param player the player to check against
     */
    public static void refreshBadge(Player player) {
        if (!player.getDetails().hasClubSubscription()) {
            // If the database still thinks we have Habbo club even after it expired, reset it back to 0.
            if (player.getDetails().getClubExpiration() > 0) {
                //player.getDetails().setFirstClubSubscription(0);
                player.getDetails().setClubExpiration(0);
                player.getDetails().getBadges().remove("HC1"); // If their HC ran out, remove badge.
                player.getDetails().getBadges().remove("HC2"); // No gold badge when not subscribed.

                player.refreshFuserights();
                PlayerDao.saveSubscription(player.getDetails());
            }
        } else {
            if (!player.getDetails().getBadges().contains("HC1")) {
                player.getDetails().getBadges().add("HC1");
            }

            if (player.getDetails().hasGoldClubSubscription()) {
                if (!player.getDetails().getBadges().contains("HC2")) {
                    player.getDetails().getBadges().add("HC2");
                }
            }
        }

        player.send(new AVAILABLE_BADGES(player.getDetails()));
    }
}
