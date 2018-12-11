package org.alexdev.kepler.game.games.snowstorm.object;

import org.alexdev.kepler.game.games.enums.GameObjectType;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.SnowStormObject;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class SnowStormAvatarObject extends SnowStormObject {
    private final GamePlayer gamePlayer;

    public SnowStormAvatarObject(GamePlayer gamePlayer) {
        super(GameObjectType.SNOWWAR_AVATAR_OBJECT);
        this.gamePlayer = gamePlayer;
        this.getGameObjectsSyncValues().add(GameObjectType.SNOWWAR_AVATAR_OBJECT.getObjectId()); // type id
        this.getGameObjectsSyncValues().add(gamePlayer.getPlayer().getDetails().getId()); // int id
        this.getGameObjectsSyncValues().add(gamePlayer.getPlayer().getRoomUser().getPosition().getX()); // x
        this.getGameObjectsSyncValues().add(gamePlayer.getPlayer().getRoomUser().getPosition().getY()); // y
        this.getGameObjectsSyncValues().add(gamePlayer.getPlayer().getRoomUser().getPosition().getRotation()); // body direction
        this.getGameObjectsSyncValues().add(0); // hit points
        this.getGameObjectsSyncValues().add(5); // snowball count
        this.getGameObjectsSyncValues().add(0); // is bot 
        this.getGameObjectsSyncValues().add(0); // activity timer
        this.getGameObjectsSyncValues().add(0); // activity state
        this.getGameObjectsSyncValues().add(gamePlayer.getPlayer().getRoomUser().getPosition().getX()); // next tile x
        this.getGameObjectsSyncValues().add(gamePlayer.getPlayer().getRoomUser().getPosition().getY()); // next tile y
        this.getGameObjectsSyncValues().add(gamePlayer.getPlayer().getRoomUser().getPosition().getX()); // move target x
        this.getGameObjectsSyncValues().add(gamePlayer.getPlayer().getRoomUser().getPosition().getY()); // move target y
        this.getGameObjectsSyncValues().add(0); // score
        this.getGameObjectsSyncValues().add(gamePlayer.getPlayer().getDetails().getId()); // player id
        this.getGameObjectsSyncValues().add(gamePlayer.getTeamId()); // team id
        this.getGameObjectsSyncValues().add(gamePlayer.getPlayer().getRoomUser().getInstanceId()); // room index

        /*

        [
        "type: 5",
        "int_id: 1",
        "x: 99360",
        "y: 39360",
        "body_direction: 1",
        "hit_points: 0",
        "snowball_count: 5",
        "is_bot: 0",
        "activity_timer: 0",
        "activity_state: 0",
        "next_tile_x: 20",
        "next_tile_y: 8",
        "move_target_x: 0",
        "move_target_y: 0",
        "score: 0",
        "player_id: 1",
        "team_id: 0",
        "room_index: 1"]

         */
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        for (var syncVar : this.getGameObjectsSyncValues()) {
            response.writeInt(syncVar);
        }

        response.writeString(gamePlayer.getPlayer().getDetails().getName());
        response.writeString(gamePlayer.getPlayer().getDetails().getMotto());
        response.writeString(gamePlayer.getPlayer().getDetails().getFigure());
        response.writeString(gamePlayer.getPlayer().getDetails().getSex());// Actually room user id/instance id
    }
}
