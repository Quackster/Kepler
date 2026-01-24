package net.h4bbo.kepler.game.messenger;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.PlayerManager;

public class MessengerManager {
    private static MessengerManager instance;

    public Messenger getMessengerData(int userId) {
        Player player = PlayerManager.getInstance().getPlayerById(userId);

        if (player != null) {
            return player.getMessenger();
        }

        return new Messenger(PlayerManager.getInstance().getPlayerData(userId));
    }

    public Messenger getMessengerData(String username) {
        Player player = PlayerManager.getInstance().getPlayerByName(username);

        if (player != null) {
            return player.getMessenger();
        }

        PlayerDetails details = PlayerManager.getInstance().getPlayerData(username);

        if (details == null) {
            return null;
        }

        return new Messenger(details);
    }

    public static MessengerManager getInstance() {
        if (instance == null) {
            instance = new MessengerManager();
        }

        return instance;
    }
}
