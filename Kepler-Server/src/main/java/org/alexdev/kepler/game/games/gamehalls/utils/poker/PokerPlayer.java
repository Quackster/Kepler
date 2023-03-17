package org.alexdev.kepler.game.games.gamehalls.utils.poker;

import org.alexdev.kepler.game.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PokerPlayer {
    private final Player player;
    private final int playerId;

    private List<Card> cards;
    private int[] cardsChanged;
    private int amountChanged;
    private boolean changeDone;

    public PokerPlayer(Player player, int playerId) {
        this.player = player;
        this.cards = new ArrayList<>();
        this.playerId = playerId;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void changeCard(int position, Card replacement) {
        var tempCards = this.cards.toArray(Card[]::new);
        tempCards[position] = replacement;

        // this.cards.set(position, replacement);

        this.cards = Arrays.stream(tempCards).toList();
        this.amountChanged++;
    }

    public int[] getCardsChanged() {
        if (cardsChanged == null) {
            return new int[0];
        }

        return cardsChanged;
    }

    public void setCardsChanged(int[] cardsChanged) {
        this.cardsChanged = cardsChanged;
    }

    public boolean getChangeDone() {
        return changeDone;
    }

    public void setChangeDone(boolean changeDone) {
        this.changeDone = changeDone;
    }

    public int getAmountChanged() {
        return amountChanged;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPlayerId() {
        return playerId;
    }
}
