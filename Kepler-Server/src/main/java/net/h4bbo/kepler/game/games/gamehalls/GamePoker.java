package net.h4bbo.kepler.game.games.gamehalls;

import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.games.triggers.GameTrigger;

import java.util.List;

public class GamePoker extends GamehallGame {
    public GamePoker(List<int[]> kvp) {
        super(kvp);
    }

    @Override
    public void gameStart() { }

    @Override
    public void gameStop() { }

    @Override
    public void joinGame(Player p) { }

    @Override
    public void leaveGame(Player player) { }

    @Override
    public void handleCommand(Player player, Room room, Item item, String command, String[] args) {
        GameTrigger trigger = (GameTrigger) item.getDefinition().getInteractionType().getTrigger();

        if (command.equals("CLOSE")) {
            trigger.onEntityLeave(player, player.getRoomUser(), item);
            return;
        }
    }

    @Override
    public int getMaximumPeopleRequired() {
        return 4;
    }

    @Override
    public int getMinimumPeopleRequired() {
        return 2;
    }

    @Override
    public String getGameFuseType() {
        return "Poker";
    }
}
