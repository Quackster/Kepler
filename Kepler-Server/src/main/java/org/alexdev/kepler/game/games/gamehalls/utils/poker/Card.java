package org.alexdev.kepler.game.games.gamehalls.utils.poker;

public class Card {
    /** The number of ranks in a deck. */
    public static final int NO_OF_RANKS = 13;

    /** The number of suits in a deck. */
    public static final int NO_OF_SUITS = 4;

    // The ranks.
    public static final int ACE      = 12;
    public static final int KING     = 11;
    public static final int QUEEN    = 10;
    public static final int JACK     = 9;
    public static final int TEN      = 8;
    public static final int NINE     = 7;
    public static final int EIGHT    = 6;
    public static final int SEVEN    = 5;
    public static final int SIX      = 4;
    public static final int FIVE     = 3;
    public static final int FOUR     = 2;
    public static final int THREE    = 1;
    public static final int DEUCE    = 0;

    // The suits.
    public static final int SPADES   = 3;
    public static final int HEARTS   = 2;
    public static final int CLUBS    = 1;
    public static final int DIAMONDS = 0;

    /** The rank symbols. */
    public static final String[] RANK_SYMBOLS = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"
    };

    /** The suit symbols. */
    public static final char[] SUIT_SYMBOLS = { 'd', 'c', 'h', 's' };

    private int rank;
    private int suit;

    public Card(int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int getSuit() {
        return suit;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public String toString() {
        if (RANK_SYMBOLS[rank].equals("11")) {
            return "joker";
        }

        return SUIT_SYMBOLS[suit] + RANK_SYMBOLS[rank];
    }

    /**
     * Compares this card with another card based on their rank and suit.
     *
     * @param other the other card to compare with
     * @return a negative integer, zero, or a positive integer as this card is less than,
     * equal to, or greater than the other card
     */
    public int compareTo(Card other) {
        int rankCompare = this.rank - other.rank;
        if (rankCompare != 0) {
            return rankCompare;
        } else {
            return this.suit - other.suit;
        }
    }
}