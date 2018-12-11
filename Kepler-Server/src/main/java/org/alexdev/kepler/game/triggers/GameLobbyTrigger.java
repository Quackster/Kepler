package org.alexdev.kepler.game.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.games.GameManager;
import org.alexdev.kepler.game.games.enums.GameType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.games.GAMEPLAYERINFO;
import org.alexdev.kepler.messages.outgoing.games.INSTANCELIST;
import org.alexdev.kepler.messages.types.MessageComposer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class GameLobbyTrigger extends GenericTrigger {
    public void onRoomEntry(Entity entity, Room room, Object... customArgs) { }
    public void onRoomLeave(Entity entity, Room room, Object... customArgs) { }

    public void showPoints(Player player, Room room) {
        room.send(new GAMEPLAYERINFO(this.getGameType(), List.of(player)));
    }

    public abstract void createGame(Player gameCreator, Map<String, Object> gameParameters);
    public abstract GameType getGameType();

    public MessageComposer getInstanceList() {
        return new INSTANCELIST(
                GameManager.getInstance().getGamesByType(this.getGameType()),
                GameManager.getInstance().getFinishedGames().stream().filter(game -> game.getGameType() == this.getGameType())
                        .collect(Collectors.toList()));
    }
}
