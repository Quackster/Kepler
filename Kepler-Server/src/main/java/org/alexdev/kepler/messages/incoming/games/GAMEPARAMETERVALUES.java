package org.alexdev.kepler.messages.incoming.games;

import org.alexdev.kepler.game.games.Game;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.triggers.GameLobbyTrigger;
import org.alexdev.kepler.messages.outgoing.games.JOINFAILED;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class GAMEPARAMETERVALUES implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        // BcPA@IfieldTypeHI@HnumTeamsHJ@OallowedPowerupsI@O1,2,3,4,5,6,7,8@DnameI@DtestH
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (!(room.getModel().getModelTrigger() instanceof GameLobbyTrigger)) {
            return;
        }

        GameLobbyTrigger gameLobbyTrigger = (GameLobbyTrigger) room.getModel().getModelTrigger();

        Map<String, Object> gameParameters = new HashMap<>();

        int parameters = reader.readInt();

        for (int i = 0; i < parameters; i++) {
            String parameter = reader.readString();
            boolean isTextValue = reader.readBoolean();
            Object value;

            if (isTextValue) {
                value = StringUtil.filterInput(reader.readString(), true);
            } else {
                value = reader.readInt();
            }

            gameParameters.put(parameter, value);
        }

        gameLobbyTrigger.createGame(player, gameParameters);
        
        room.send(gameLobbyTrigger.getInstanceList());
    }
}
