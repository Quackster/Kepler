package org.alexdev.kepler.game.room.models.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.games.GameManager;
import org.alexdev.kepler.game.games.enums.GameType;
import org.alexdev.kepler.game.games.battleball.BattleBallGame;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.triggers.GameLobbyTrigger;
import org.alexdev.kepler.messages.outgoing.games.LOUNGEINFO;
import org.alexdev.kepler.messages.outgoing.games.GAMEPLAYERINFO;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BattleballLobbyTrigger extends GameLobbyTrigger {
    @Override
    public void onRoomEntry(Entity entity, Room room, Object... customArgs) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        // Don't show panel and lounge info if create game is disabled
        if (!GameConfiguration.getInstance().getBoolean(this.getGameType().name().toLowerCase() + ".create.game.enabled")) {
            return;
        }

        Player player = (Player) entity;

        player.send(new LOUNGEINFO());
        player.send(new GAMEPLAYERINFO(this.getGameType(), room.getEntityManager().getPlayers()));

        this.showPoints(player, room);
    }

    @Override
    public void onRoomLeave(Entity entity, Room room, Object... customArgs) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        if (player.getRoomUser().getObservingGameId() != -1) {
            player.getRoomUser().stopObservingGame();
        }
    }

    @Override
    public void createGame(Player gameCreator, Map<String, Object> gameParameters) {
        int mapId = (int) gameParameters.get("fieldType");

        if (mapId < 1 || mapId > 5) {
            return;
        }

        int teams = (int) gameParameters.get("numTeams");

        if (teams < 2 || teams > 4) {
            return;
        }

        String name = (String) gameParameters.get("name");

        if (name.isEmpty()) {
            return;
        }

        List<Integer> allowedPowerUps = new ArrayList<>();

        String powerUps = (String) gameParameters.get("allowedPowerups");

        for (String powerUp : powerUps.split(",")) {
            if (StringUtils.isNumeric(powerUp)) {
                allowedPowerUps.add(Integer.parseInt(powerUp));
            }
        }

        BattleBallGame game = new BattleBallGame(GameManager.getInstance().createId(), mapId, this.getGameType(), name, teams, gameCreator, allowedPowerUps);

        GamePlayer gamePlayer = new GamePlayer(gameCreator);
        gamePlayer.setGameId(game.getId());
        gamePlayer.setTeamId(0);

        gameCreator.getRoomUser().setGamePlayer(gamePlayer);
        game.movePlayer(gamePlayer, -1, 0);

        GameManager.getInstance().getGames().add(game);
    }

    @Override
    public GameType getGameType() {
        return GameType.BATTLEBALL;
    }
}
