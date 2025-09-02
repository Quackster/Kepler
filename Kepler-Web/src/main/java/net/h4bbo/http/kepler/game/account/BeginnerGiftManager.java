package net.h4bbo.http.kepler.game.account;

import net.h4bbo.kepler.game.alerts.AlertType;
import net.h4bbo.kepler.game.item.ItemManager;
import net.h4bbo.kepler.game.item.base.ItemDefinition;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;
import net.h4bbo.kepler.game.player.statistics.PlayerStatisticManager;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.kepler.dao.mysql.AlertsDao;

import java.util.concurrent.TimeUnit;

public class BeginnerGiftManager {
    public static boolean progress(PlayerDetails playerDetails, PlayerStatisticManager statistics) {
        if (!(statistics.getIntValue(PlayerStatistic.NEWBIE_ROOM_LAYOUT) > 0 && statistics.getIntValue(PlayerStatistic.NEWBIE_GIFT) > 0)) {
            return false;
        }

        if (statistics.getIntValue(PlayerStatistic.NEWBIE_GIFT) > 2) {
            return false;
        }

        if (statistics.getIntValue(PlayerStatistic.NEWBIE_GIFT_TIME) > DateUtil.getCurrentTimeSeconds()) {
            return false;
        }

        int roomLayout = statistics.getIntValue(PlayerStatistic.NEWBIE_ROOM_LAYOUT);
        int gift = statistics.getIntValue(PlayerStatistic.NEWBIE_GIFT);

        ItemDefinition itemGift = null;

        switch (gift) {
            case 1:
                itemGift = ItemManager.getInstance().getDefinitionBySprite("noob_stool*" + roomLayout);
                break;
            case 2:
                itemGift = ItemManager.getInstance().getDefinitionBySprite("noob_plant");
                break;
        }

        if (itemGift == null) {
            return false;
        }

        String presentLabel = GameConfiguration.getInstance().getString("alerts.gift.message").replace("%item_name%", itemGift.getName());
        AlertsDao.createAlert(playerDetails.getId(), AlertType.PRESENT, presentLabel);

        ItemManager.getInstance().createGift(playerDetails.getId(), playerDetails.getName(), itemGift.getSprite(), presentLabel, "");

        int nextGift = gift + 1;

        if (nextGift < 3) {
            statistics.setLongValue(PlayerStatistic.NEWBIE_GIFT, nextGift);
            statistics.setLongValue(PlayerStatistic.NEWBIE_GIFT_TIME, DateUtil.getCurrentTimeSeconds() + TimeUnit.DAYS.toSeconds(1));
        } else {
            statistics.setLongValue(PlayerStatistic.NEWBIE_GIFT, 3);
            statistics.setLongValue(PlayerStatistic.NEWBIE_GIFT_TIME, 0);
        }

        return true;
    }
}
