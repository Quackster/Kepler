package org.alexdev.kepler.messages.outgoing.games;

import org.alexdev.kepler.game.games.GameManager;
import org.alexdev.kepler.game.games.enums.GameType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class GAMEPLAYERINFO extends MessageComposer {
    private final List<Player> players;
    private final GameType type;

    public GAMEPLAYERINFO(GameType type, List<Player> players) {
        this.type = type;
        this.players = players;
    }


    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.players.size());

        for (Player player : this.players) {
            response.writeInt(player.getRoomUser().getInstanceId());
            response.writeString(player.getDetails().getGamePoints(this.type));
            response.writeString(GameManager.getInstance().getRankByPoints(this.type, player).getTitle());
        }
    }

    @Override
    public short getHeader() {
        return 250; // "Cz"
    }
}
