package org.alexdev.kepler.game.moderation.actions;

import org.alexdev.kepler.dao.mysql.BanDao;
import org.alexdev.kepler.dao.mysql.ModerationDao;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.ban.BanType;
import org.alexdev.kepler.game.ban.BannedPlayer;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.moderation.ModerationAction;
import org.alexdev.kepler.game.moderation.ModerationActionType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.moderation.USER_BANNED;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.DateUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ModeratorBanUserAction implements ModerationAction {
    @Override
    public void performAction(Player player, Room room, String alertMessage, String notes, NettyRequest reader) {
        if (!player.hasFuse(Fuseright.BAN)) {
            return;
        }

        String name = reader.readString();
        int banHours = reader.readInt();
        boolean banMachineId = reader.readBoolean();
        boolean banIp = reader.readBoolean();

        if (banHours > 100000) {
            banHours = 100000;
        }

        if (banHours < 2) {
            banHours = 2;
        }

        PlayerDetails playerDetails = PlayerManager.getInstance().getPlayerData(name);

        if (playerDetails == null) {
            player.send(new ALERT("Could not find user: " + name));
            return;
        }

        if (playerDetails.isBanned() != null) {
            if (playerDetails.isBanned().getBanType() == BanType.IP_ADDRESS) {
                player.send(new ALERT("User is already IP banned!"));
                return;
            } else if (!banIp && playerDetails.isBanned().getBanType() == BanType.USER_ID) {
                player.send(new ALERT("User is already banned!"));
                return;
            }
        }
        String notePrefix = banIp ? "IP BAN NOTES: " : "USER BAN NOTES: ";
        ModerationDao.addLog(ModerationActionType.BAN_USER, player.getDetails().getId(), playerDetails.getId(), alertMessage, notePrefix + notes);

        long banTime = DateUtil.getCurrentTimeSeconds() + TimeUnit.HOURS.toSeconds(banHours);

        BanDao.addBan(new BannedPlayer(alertMessage, PlayerDao.getLatestIp(playerDetails.getId()), playerDetails.getId(), banIp ? BanType.IP_ADDRESS : BanType.USER_ID, banTime));

        if(banIp) {
            List<Player> targets = PlayerManager.getInstance().getPlayersByIP(PlayerDao.getLatestIp(playerDetails.getId()));
            for(Player target : targets) {
                target.send(new USER_BANNED(alertMessage));
                GameScheduler.getInstance().getService().schedule(target::kickFromServer, 1, TimeUnit.SECONDS);
            };
        } else {
            Player target = PlayerManager.getInstance().getPlayerById(playerDetails.getId());
            if (target != null) {
                target.send(new USER_BANNED(alertMessage));
                GameScheduler.getInstance().getService().schedule(target::kickFromServer, 1, TimeUnit.SECONDS);
            }
        }

        player.send(new ALERT("The user " + playerDetails.getName() + " has been banned."));
    }
}
