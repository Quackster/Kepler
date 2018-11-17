package org.alexdev.kepler.game.games.snowstorm.object;

import org.alexdev.kepler.game.games.enums.GameObjectType;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.snowstorm.SnowStormObject;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class SnowStormPlayerObject extends SnowStormObject {
    private final GamePlayer gamePlayer;

    public SnowStormPlayerObject(GamePlayer gamePlayer) {
        super(GameObjectType.SNOWWAR_PLAYER_OBJECT);
        this.gamePlayer = gamePlayer;
        this.getGameObjectsSyncValues().add(this.getGameObjectType().getObjectId()); // type id
        this.getGameObjectsSyncValues().add(gamePlayer.getPlayer().getDetails().getId()); // int id
        this.getGameObjectsSyncValues().add(gamePlayer.getPlayer().getRoomUser().getInstanceId()); // room index

        // ["type: 5",
        // "int_id: 4",
        // "x: 4",
        // "y: 0",
        // "body_direction: 5",
        // "hit_points: 0",
        // "snowball_count: 0",
        // "is_bot: 0",
        // "activity_timer: 0",
        // "activity_state: 4",
        // "next_tile_x: 4",
        // "next_tile_y: 4",
        // "move_target_x: 4",
        // "move_target_y: 0",
        // "score: 1",
        // "player_id: 0",
        // "team_id: 1",
        // "room_index: 1"]
        //"
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(this.getGameObjectType().getObjectId());
        response.writeInt(gamePlayer.getPlayer().getRoomUser().getInstanceId());

        for (int syncValue : this.getGameObjectsSyncValues()) {
            response.writeInt(syncValue);
        }
    }
}
