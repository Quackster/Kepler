package org.alexdev.kepler.messages.incoming.rooms;

import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.battleball.BattleBallGame;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.player.GameTeam;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.object.SnowStormAvatarObject;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.games.FULLGAMESTATUS;
import org.alexdev.kepler.messages.outgoing.games.SNOWSTORM_FULLGAMESTATUS;
import org.alexdev.kepler.messages.outgoing.rooms.HEIGHTMAP;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.ArrayList;
import java.util.List;

public class G_HMAP implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        player.send(new HEIGHTMAP(player.getRoomUser().getRoom().getModel()));
        //player.send(new HEIGHTMAP_UPDATE(player.getRoomUser().getRoom(), player.getRoomUser().getRoom().getModel()));

        GamePlayer gamePlayer = player.getRoomUser().getGamePlayer();

        if (gamePlayer != null) {
            if (gamePlayer.getGame() instanceof BattleBallGame) {
                player.send(new FULLGAMESTATUS(gamePlayer.getGame()));
            } else {
                SnowStormGame game = (SnowStormGame) gamePlayer.getGame();

                List<GameObject> objects = new ArrayList<>();
                //List<GameObject> events = new ArrayList<>();

                for (GameTeam gameTeam : game.getTeams().values()) {
                    for (GamePlayer p : gameTeam.getActivePlayers()) {
                        objects.add(new SnowStormAvatarObject(p));
                        //events.add(new SnowStormObjectEvent(new SnowStormAvatarObject(p)));
                    }
                }

                gamePlayer.getTurnContainer().iterateTurn();
                gamePlayer.getTurnContainer().calculateChecksum(objects);

                player.send(new SNOWSTORM_FULLGAMESTATUS(game, gamePlayer, objects, List.of()));

                //gamePlayer.getTurnContainer().iterateTurn();
                //gamePlayer.getTurnContainer().calculateChecksum(events);

                //player.send(new SNOWSTORM_GAMESTATUS(gamePlayer, events)); // *** TURN 0  - CHECKSUM MISMATCH! server says: 2852459, we say: 2856939 . Previous turn: ["Turn": 0, "Events": [[]]]"
            }
        }
    }
}
