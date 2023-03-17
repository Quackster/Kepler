package org.alexdev.kepler.game.games.gamehalls;

import org.alexdev.kepler.game.games.gamehalls.utils.poker.PokerPlayer;
import org.alexdev.kepler.game.games.gamehalls.utils.poker.TexasHoldem;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.games.triggers.GameTrigger;
import org.alexdev.kepler.messages.outgoing.rooms.games.ITEMMSG;

import java.util.Arrays;
import java.util.List;

public class GamePoker extends GamehallGame {
    public TexasHoldem pokerGame;

    public GamePoker(List<int[]> kvp) {
        super(kvp);
    }

    @Override
    public void gameStart() {

    }

    @Override
    public void gameStop() {
        this.pokerGame = null;
    }

    @Override
    public void handleCommand(Player player, Room room, Item item, String command, String[] args) {
        GameTrigger trigger = (GameTrigger) item.getDefinition().getInteractionType().getTrigger();

        if (command.equals("CLOSE")) {
            this.pokerGame = null;
            trigger.onEntityLeave(player, player.getRoomUser(), item);
            return;
        }

        if (command.equals("OPEN")) {
            if (this.pokerGame != null) {
                boolean playerAdded = false;

                if (this.pokerGame.getPlayers().stream().noneMatch(PokerPlayer::getChangeDone)) { // Game hasn't started, so add them
                    this.pokerGame.addPlayer(player);
                }

            } else {
                this.pokerGame = new TexasHoldem(this, this.getPlayers());
            }

            this.pokerGame.sendOpponents();
            this.pokerGame.dealCommunityCards();

            return;
        }

        if (command.equals("STARTOVER")) {
            if (this.pokerGame == null) {
                return;
            }

            this.pokerGame.getPlayers().forEach(x -> this.pokerGame.sendCards(x));
            this.pokerGame.sendOpponents();
        }

        if (command.equals("CHANGE")) {
           int[] cardsChanged = Arrays.stream(args)
                .filter(s -> !s.isEmpty())
                .mapToInt(Integer::parseInt)
                .toArray();

           var pokerPlayer = this.pokerGame.getPlayer(player);

           for (int position : cardsChanged) {
               pokerPlayer.changeCard(position, this.pokerGame.getDeck().deal());
           }

           pokerPlayer.setCardsChanged(cardsChanged);
           pokerPlayer.setChangeDone(true);

            this.pokerGame.sendChanged(pokerPlayer);
            this.pokerGame.sendCards(pokerPlayer);
            return;
        }
    }

    @Override
    public int getMaximumPeopleRequired() {
        return 4;
    }

    @Override
    public int getMinimumPeopleRequired() {
        return 1;
    }

    @Override
    public String getGameFuseType() {
        return "Poker";
    }
}
