package org.alexdev.kepler.game.games.gamehalls.utils.poker;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {
    private static final int NO_OF_CARDS = Card.NO_OF_RANKS * Card.NO_OF_SUITS;

    private Card[] cards;
    private int nextCardIndex = 0;

    private Random random = new SecureRandom();


    public Deck() {
        this.cards = new Card[NO_OF_CARDS];

        int index = 0;
        for (int suit = Card.NO_OF_SUITS - 1; suit >= 0; suit--) {
            for (int rank = Card.NO_OF_RANKS - 1; rank >= 0 ; rank--) {
                this.cards[index++] = new Card(rank, suit);
            }
        }
    }

    /**
     * Shuffles the cards in the deck randomly using the Fisher-Yates shuffle algorithm.
     */
    public void shuffle() {
        for (int oldIndex = 0; oldIndex < NO_OF_CARDS; oldIndex++) {
            int newIndex = random.nextInt(NO_OF_CARDS);
            Card tempCard = cards[oldIndex];
            this.cards[oldIndex] = cards[newIndex];
            this.cards[newIndex] = tempCard;
        }

        this.nextCardIndex = 0;
    }

    /**
     * Resets the deck by setting the index of the next card to deal to the beginning of the deck.
     */
    public void reset() {
        this.nextCardIndex = 0;
    }

    /**
     * Returns the next card in the deck and increments the index of the next card to deal.
     *
     * @return the next card in the deck
     */
    public Card deal() {
        return this.cards[this.nextCardIndex++];
    }

    /**
     * Returns a list of the specified number of cards from the deck, starting from the current next card.
     * Increments the index of the next card to deal by the number of cards dealt.
     *
     * @param noOfCards the number of cards to deal
     * @return a list of the specified number of cards from the deck
     */
    public List<Card> deal(int noOfCards) {
        List<Card> dealtCards = new ArrayList<Card>();
        for (int i = 0; i < noOfCards; i++) {
            dealtCards.add(this.cards[this.nextCardIndex++]);
        }
        return dealtCards;
    }
}
