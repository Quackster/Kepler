package org.alexdev.kepler.messages.incoming.games;

import org.alexdev.kepler.game.games.GameParameter;
import org.alexdev.kepler.game.games.enums.GameType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.triggers.GameLobbyTrigger;
import org.alexdev.kepler.messages.outgoing.games.GAMEPARAMETERS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class INITIATECREATEGAME implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (!(room.getModel().getModelTrigger() instanceof GameLobbyTrigger)) {
            return;
        }

        GameLobbyTrigger gameLobbyTrigger = (GameLobbyTrigger) room.getModel().getModelTrigger();
        GameParameter[] parameters = null;

        if (gameLobbyTrigger.getGameType() == GameType.BATTLEBALL) {
            parameters = new GameParameter[]{
                    new GameParameter("fieldType", true, "1", 1, 5),
                    new GameParameter("numTeams", true, "2", 2, 4),
                    new GameParameter("allowedPowerups", true, "1,2,3,4,5,6,7,8"),
                    new GameParameter("name", true, "")
            };
        }

        if (gameLobbyTrigger.getGameType() == GameType.SNOWSTORM) {
            parameters = new GameParameter[]{
                    new GameParameter("fieldType", true, "1", 1, 7),
                    new GameParameter("numTeams", true, "2", 2, 4),
                    new GameParameter("gameLengthChoice", true, "1", 1, 3),
                    new GameParameter("name", true, "")
            };
        }

        if (parameters != null)
            player.send(new GAMEPARAMETERS(parameters));
    }
}
