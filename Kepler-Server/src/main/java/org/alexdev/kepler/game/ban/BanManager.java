package org.alexdev.kepler.game.ban;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.server.netty.NettyPlayerNetwork;

import java.util.ArrayList;
import java.util.Map;

public class BanManager {
    private static BanManager instance;

    /**
     * Disconnect ban accounts based on the following ban criteria.
     * 
     * @param criteria the criteria to ban
     */
    public void disconnectBanAccounts(Map<BanType, String> criteria) {
        for (Player player : new ArrayList<>(PlayerManager.getInstance().getPlayers())) {
            if (criteria.containsKey(BanType.USER_ID)) {
                if (player.getDetails().getId() == Integer.parseInt(criteria.get(BanType.USER_ID))) {
                    player.getNetwork().getChannel().close();
                    break;
                }
            }

            /*
            if (criteria.containsKey(BanType.MACHINE_ID)) {
                if (player.getDetails().getMachineId() != null && player.getDetails().getMachineId().equals(criteria.get(BanType.MACHINE_ID))) {
                    player.getNetwork().getChannel().close();
                    break;
                }
            }
*/
            if (criteria.containsKey(BanType.IP_ADDRESS)) {
                if (NettyPlayerNetwork.getIpAddress(player.getNetwork().getChannel()).equals(criteria.get(BanType.IP_ADDRESS))) {
                    player.getNetwork().getChannel().close();
                    break;
                }
            }
        }
    }

    /**
     * Gets the instance
     *
     * @return the instance
     */
    public static BanManager getInstance() {
        if (instance == null) {
            instance = new BanManager();
        }

        return instance;
    }
}
