package org.alexdev.kepler.game.games.snowstorm.object;

import org.alexdev.kepler.game.games.enums.GameObjectType;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.SnowStormObject;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.ArrayList;
import java.util.List;

public class SnowStormAvatarObject extends SnowStormObject {
    private final GamePlayer gamePlayer;

    private int X;
    private int Y;

    private int moveNextX;
    private int moveNextY;

    private int moveTargetX;
    private int moveTargetY;

    private int bodyDirection;

    public SnowStormAvatarObject(GamePlayer gamePlayer) {
        super(GameObjectType.SNOWWAR_AVATAR_OBJECT);
        this.gamePlayer = gamePlayer;
    }

    @Override
    public List<Integer> getGameObjectsSyncValues() {
        List<Integer> pGameObjectsSyncValues = new ArrayList<>();
        pGameObjectsSyncValues.clear();
        pGameObjectsSyncValues.add(GameObjectType.SNOWWAR_AVATAR_OBJECT.getObjectId()); // type id
        pGameObjectsSyncValues.add(gamePlayer.getObjectId()); // int id
        pGameObjectsSyncValues.add(SnowStormGame.convertToWorldCoordinate(this.X)); // x
        pGameObjectsSyncValues.add(SnowStormGame.convertToWorldCoordinate(this.Y)); // x
        pGameObjectsSyncValues.add(this.bodyDirection); // body direction
        pGameObjectsSyncValues.add(0); // hit points
        pGameObjectsSyncValues.add(5); // snowball count
        pGameObjectsSyncValues.add(0); // is bot
        pGameObjectsSyncValues.add(0); // activity timer
        pGameObjectsSyncValues.add(0); // activity state
        pGameObjectsSyncValues.add(this.moveNextX); // move target x
        pGameObjectsSyncValues.add(this.moveNextY); // move target y
        pGameObjectsSyncValues.add(SnowStormGame.convertToWorldCoordinate(this.moveTargetX)); // move target x
        pGameObjectsSyncValues.add(SnowStormGame.convertToWorldCoordinate(this.moveTargetY)); // move target y
        pGameObjectsSyncValues.add(0); // score
        pGameObjectsSyncValues.add(gamePlayer.getPlayer().getDetails().getId()); // player id
        pGameObjectsSyncValues.add(gamePlayer.getTeamId()); // team id
        pGameObjectsSyncValues.add(gamePlayer.getObjectId()); // room index

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
        return pGameObjectsSyncValues;
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

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public int getMoveNextX() {
        return moveNextX;
    }

    public void setMoveNextX(int moveNextX) {
        this.moveNextX = moveNextX;
    }

    public int getMoveNextY() {
        return moveNextY;
    }

    public void setMoveNextY(int moveNextY) {
        this.moveNextY = moveNextY;
    }

    public int getMoveTargetX() {
        return moveTargetX;
    }

    public void setMoveTargetX(int moveTargetX) {
        this.moveTargetX = moveTargetX;
    }

    public int getMoveTargetY() {
        return moveTargetY;
    }

    public void setMoveTargetY(int moveTargetY) {
        this.moveTargetY = moveTargetY;
    }

    public int getBodyDirection() {
        return bodyDirection;
    }

    public void setBodyDirection(int bodyDirection) {
        this.bodyDirection = bodyDirection;
    }
}
