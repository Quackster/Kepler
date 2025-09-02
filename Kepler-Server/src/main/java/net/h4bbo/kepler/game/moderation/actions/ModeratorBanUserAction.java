package net.h4bbo.kepler.game.moderation.actions;

import net.h4bbo.kepler.dao.mysql.BanDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.GameScheduler;
import net.h4bbo.kepler.game.ban.BanManager;
import net.h4bbo.kepler.game.ban.BanType;
import net.h4bbo.kepler.game.commands.CommandManager;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.moderation.ModerationAction;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.outgoing.alert.ALERT;
import net.h4bbo.kepler.messages.outgoing.moderation.USER_BANNED;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import net.h4bbo.kepler.util.DateUtil;

import java.util.HashMap;
import java.util.Map;
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
            player.send(new ALERT("User is already banned!"));
            return;
        }

        long banTime = DateUtil.getCurrentTimeSeconds() + TimeUnit.HOURS.toSeconds(banHours);
        BanDao.addBan(BanType.USER_ID, String.valueOf(playerDetails.getId()), banTime, alertMessage, player.getDetails().getId());


        if (banIp) {
            BanDao.addBan(BanType.IP_ADDRESS, PlayerDao.getLatestIp(playerDetails.getId()), banTime, alertMessage, player.getDetails().getId());
        }

        Player target = PlayerManager.getInstance().getPlayerById(playerDetails.getId());

        if (target != null) {
            target.send(new USER_BANNED(alertMessage));
            GameScheduler.getInstance().getService().schedule(target::kickFromServer, 1, TimeUnit.SECONDS);
        }

        player.send(new ALERT("The user " + playerDetails.getName() + " has been banned."));
    }

    public static String ban(PlayerDetails banningPlayerDetails, String alertMessage, String notes, String name, long banSeconds, boolean banMachineId, boolean banIp) {
        Map<BanType, String> criteria = new HashMap<>();
        PlayerDetails playerDetails = PlayerManager.getInstance().getPlayerData(name);

        if (playerDetails == null) {
            return "Could not find user: " + name;
        }

        if (playerDetails.getId() == banningPlayerDetails.getId()) {
            return "Cannot ban yourself";
        }

        if (playerDetails.isBanned() != null) {
            return "User is already banned!";
        }

        if (CommandManager.getInstance().hasPermission(playerDetails, "ban"))
            return "Cannot ban a user who has permission to ban";

        long banTime = DateUtil.getCurrentTimeSeconds() + banSeconds;

        BanDao.addBan(BanType.USER_ID, String.valueOf(playerDetails.getId()), banTime, alertMessage, banningPlayerDetails.getId());
        criteria.put(BanType.USER_ID, String.valueOf(playerDetails.getId()));

        if (banMachineId && playerDetails.getMachineId() != null) {
            BanDao.addBan(BanType.MACHINE_ID, playerDetails.getMachineId(), banTime, alertMessage, banningPlayerDetails.getId());
            criteria.put(BanType.MACHINE_ID, playerDetails.getMachineId());
        }

        /*if (banIp) {
            var latestIp = PlayerDao.getLatestIp(playerDetails.getId());
            InetAddressValidator validator = InetAddressValidator.getInstance();

            // Validate an IPv4 address
            if (validator.isValidInet4Address(latestIp)) {
                BanDao.addBan(BanType.IP_ADDRESS, latestIp, banTime, alertMessage);
                criteria.put(BanType.IP_ADDRESS, latestIp);
            }
        }*/

        Player target = PlayerManager.getInstance().getPlayerById(playerDetails.getId());

        if (target != null) {
            target.getNetwork().disconnect();
        }

        BanManager.getInstance().disconnectBanAccounts(criteria);
        return "The user " + playerDetails.getName() + " has been banned.";
    }
}
