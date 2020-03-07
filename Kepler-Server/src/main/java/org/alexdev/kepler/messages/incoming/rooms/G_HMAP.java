package org.alexdev.kepler.messages.incoming.rooms;

import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.battleball.BattleBallGame;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.games.FULLGAMESTATUS;
import org.alexdev.kepler.messages.outgoing.games.SNOWSTORM_FULLGAMESTATUS;
import org.alexdev.kepler.messages.outgoing.rooms.HEIGHTMAP;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

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

                List<GameObject> objects = game.getObjects();
                //List<GameObject> events = new ArrayList<>();

                /*for (GameTeam gameTeam : game.getTeams().values()) {
                    for (GamePlayer p : gameTeam.getActivePlayers()) {
                        objects.add(new SnowStormAvatarObject(p));
                        //events.add(new SnowStormObjectEvent(new SnowStormAvatarObject(p)));
                    }
                }*/

                player.send(new SNOWSTORM_FULLGAMESTATUS(game, gamePlayer, objects, List.of()));
                player.send(new MessageComposer() {
                    @Override
                    public void compose(NettyResponse response) {
                        response.writeInt(10);
                        response.write("c3124 sw_tree4" + (char)2 + "12 14 0 6\r" +
                                "d3125 sw_tree2" + (char)2 + "12 33 0 6\r" +
                                "d3126 sw_tree1" + (char)2 + "29 22 0 6\r" +
                                "d3127 sw_tree2" + (char)2 + "29 26 0 6\r" +
                                "d3128 sw_tree4" + (char)2 + "32 23 0 6\r" +
                                "d3129 sw_tree3" + (char)2 + "25 40 0 6\r" +
                                "d3130 sw_tree4" + (char)2 + "26 43 0 6\r" +
                                "d3131 block_basic" + (char)2 + "42 19 0 6\r" +
                                "d3132 block_basic" + (char)2 + "42 20 0 6\r" +
                                "d3133 block_basic" + (char)2 + "42 21 0 6");
                    }

                    @Override
                    public short getHeader() {
                        return 30;
                    }
                });
                /*player.send(new MessageComposer() {
                    @Override
                    public void compose(NettyResponse response) {
                        response.writeInt(10);
                        response.write("c3124 sw_tree4" + (char)2 + "12 14 0 6 1\r" +
                                "d3125 sw_tree2" + (char)2 + "12 33 0 6 1\r" +
                                "d3126 sw_tree1" + (char)2 + "29 22 0 6 1\r" +
                                "d3127 sw_tree2" + (char)2 + "29 26 0 6 1\r" +
                                "d3128 sw_tree4" + (char)2 + "32 23 0 6 1\r" +
                                "d3129 sw_tree3" + (char)2 + "25 40 0 6 1\r" +
                                "d3130 sw_tree4" + (char)2 + "26 43 0 6 1\r" +
                                "d3131 block_basic" + (char)2 + "42 19 0 6 1\r" +
                                "d3132 block_basic" + (char)2 + "42 20 0 6 1\r" +
                                "d3133 block_basic" + (char)2 + "42 21 0 6 1");
                    }

                    @Override
                    public short getHeader() {
                        return 30;
                    }
                });*/
            }
        }
    }
}
