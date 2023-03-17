package org.alexdev.kepler.game.games.gamehalls.utils.poker;

import org.alexdev.kepler.game.games.gamehalls.GamePoker;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.games.ITEMMSG;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TexasHoldem {
    private final GamePoker game;
    private Map<Player, PokerPlayer> players;
    private Deck deck;
    private AtomicInteger idCounter;
    public TexasHoldem(GamePoker game, List<Player> players) {
        this.game = game;
        this.players = new ConcurrentHashMap<>();
        this.idCounter = new AtomicInteger(0);

        this.deck = new Deck();
        this.deck.shuffle();

        for (var player : players) {
            this.addPlayer(player);
        }
    }

    /**
     * Determines the winner of the poker game based on the hands of the players.
     *
     * @return the player with the best hand
     */
    public PokerPlayer determineWinner() {
        var winner = this.players.get(0);
        int maxRank = getBestOutcome(winner.getCards()).getKey();

        for (int i = 1; i < this.players.size(); i++) {
            var player = this.players.get(i);
            int rank = getBestOutcome(player.getCards()).getKey();

            if (rank > maxRank) {
                winner = player;
                maxRank = rank;
            } else if (rank == maxRank) {
                int compareResult = compareHands(winner.getCards(), player.getCards());
                if (compareResult < 0) {
                    winner = player;
                }
            }
        }

        return winner;
    }

    /**
     * Compares two hands of cards based on their poker ranking and returns the result.
     *
     * @param hand1 the first hand of cards to compare
     * @param hand2 the second hand of cards to compare
     * @return a negative integer, zero, or a positive integer as the first hand is less than,
     * equal to, or greater than the second hand.
     */
    private int compareHands(List<Card> hand1, List<Card> hand2) {
        int rank1 = getBestOutcome(hand1).getKey();
        int rank2 = getBestOutcome(hand2).getKey();

        // Compare the ranks of the hands
        if (rank1 > rank2) {
            return 1;
        } else if (rank1 < rank2) {
            return -1;
        } else {
            // The hands have the same rank, so compare the cards in the hands
            List<Card> sortedHand1 = sortCardsByRankDescending(hand1);
            List<Card> sortedHand2 = sortCardsByRankDescending(hand2);

            for (int i = 0; i < sortedHand1.size(); i++) {
                Card card1 = sortedHand1.get(i);
                Card card2 = sortedHand2.get(i);

                int compareResult = card1.compareTo(card2);

                if (compareResult > 0) {
                    return 1;
                } else if (compareResult < 0) {
                    return -1;
                }
            }

            // The hands have the same rank and the same cards, so it's a tie
            return 0;
        }
    }

    /**
     * Sorts a list of cards by rank, in descending order.
     *
     * @param cards the list of cards to sort
     * @return a new list that contains the same cards as the input list, but sorted by rank
     * in descending order
     */
    private List<Card> sortCardsByRankDescending(List<Card> cards) {
        List<Card> sortedCards = new ArrayList<>(cards);
        sortedCards.sort((card1, card2) -> {
            int rank1 = card1.getRank();
            int rank2 = card2.getRank();
            return Integer.compare(rank2, rank1);
        });
        return sortedCards;
    }


    /**
     * Determines the best possible poker outcome for the given hand of cards.
     *
     * @param cards the list of cards in the hand
     * @return a string describing the best outcome for the hand, such as "Straight flush" or "Pair"
     */
    public Pair<Integer, String> getBestOutcome(List<Card> cards) {
        if (hasStraightFlush(cards)) {
            return Pair.of(7, "a straight flush");
        } else if (hasFourOfAKind(cards)) {
            return Pair.of(6, "four of a kind");
        } else if (hasFullHouse(cards)) {
            return Pair.of(5, "a full house");
        } else if (hasFlush(cards)) {
            return Pair.of(4, "a flush");
        } else if (hasStraight(cards)) {
            return Pair.of(3, "a straight");
        } else if (hasThreeOfAKind(cards)) {
            return Pair.of(3, "three of a kind");
        } else if (hasTwoPair(cards)) {
            return Pair.of(2, "two pair");
        } else if (hasPair(cards)) {
            return Pair.of(1, "one pair");
        } else {
            return Pair.of(0, "a high card");
        }
    }


    /**
     * Checks if the hand contains a pair of cards with the same rank.
     *
     * @param cards the list of cards in the hand
     * @return true if a pair is found, false otherwise
     */
    public boolean hasPair(List<Card> cards) {
        Set<Integer> ranks = new HashSet<>();

        for (Card card : cards) {
            int rank = card.getRank();

            if (ranks.contains(rank)) {
                return true;
            }
            ranks.add(rank);
        }

        return false;
    }

    /**
     * Checks if the hand contains two pairs of cards with different ranks.
     *
     * @param cards the list of cards in the hand
     * @return true if two pairs are found, false otherwise
     */
    public boolean hasTwoPair(List<Card> cards) {
        Set<Integer> ranks = new HashSet<>();
        int pairs = 0;

        for (Card card : cards) {
            int rank = card.getRank();

            if (ranks.contains(rank)) {
                pairs++;
            }

            ranks.add(rank);
        }

        return pairs == 2;
    }


    /**
     * Checks if the hand contains three cards with the same rank.
     *
     * @param cards the list of cards in the hand
     * @return true if three of a kind is found, false otherwise
     */
    public boolean hasThreeOfAKind(List<Card> cards) {
        int[] rankCounts = new int[Card.NO_OF_RANKS];

        for (Card card : cards) {
            int rank = card.getRank();
            rankCounts[rank]++;
        }

        for (int count : rankCounts) {
            if (count == 3) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the hand contains five cards with consecutive ranks.
     *
     * @param cards the list of cards in the hand
     * @return true if a straight is found, false otherwise
     */
    public boolean hasStraight(List<Card> cards) {
        Set<Integer> ranks = new HashSet<>();

        for (Card card : cards) {
            ranks.add(card.getRank());
        }

        if (ranks.size() != 5) {
            return false;
        }

        List<Integer> sortedRanks = new ArrayList<>(ranks);
        Collections.sort(sortedRanks);

        return sortedRanks.get(4) - sortedRanks.get(0) == 4;
    }

    /**
     * Checks if the hand contains five cards of the same suit.
     *
     * @param cards the list of cards in the hand
     * @return true if a flush is found, false otherwise
     */
    public boolean hasFlush(List<Card> cards) {
        int suit = cards.get(0).getSuit();

        for (Card card : cards) {
            if (card.getSuit() != suit) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the hand contains a pair and a three of a kind.
     *
     * @param cards the list of cards in the hand
     * @return true if a full house is found, false otherwise
     */
    public boolean hasFullHouse(List<Card> cards) {
        int[] rankCounts = new int[Card.NO_OF_RANKS];

        for (Card card : cards) {
            int rank = card.getRank();
            rankCounts[rank]++;
        }

        boolean hasThreeOfAKind = false;
        boolean hasPair = false;

        for (int count : rankCounts) {
            if (count == 3) {
                hasThreeOfAKind = true;
            } else if (count == 2) {
                hasPair = true;
            }
        }
        return hasThreeOfAKind && hasPair;
    }

    /**
     * Checks if the hand contains four cards with the same rank.
     *
     * @param cards the list of cards in the hand
     * @return true if four of a kind is found, false otherwise
     */
    public boolean hasFourOfAKind(List<Card> cards) {
        int[] rankCounts = new int[Card.NO_OF_RANKS];

        for (Card card : cards) {
            int rank = card.getRank();
            rankCounts[rank]++;
        }

        for (int count : rankCounts) {
            if (count == 4) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the hand contains five cards of the same suit with consecutive ranks.
     *
     * @param cards the list of cards in the hand
     * @return true if a straight flush is found, false otherwise
     */
    public boolean hasStraightFlush(List<Card> cards) {
        return hasFlush(cards) && hasStraight(cards);
    }

    /**
     * Deals community cards to each player in the game.
     */
    public void dealCommunityCards() {
        for (var player : this.players.values()) {
            if (!player.getCards().isEmpty()) {
                continue;
            }

            player.getCards().add(new Card(1, 1));
            player.getCards().add(new Card(2, 1));
            player.getCards().add(new Card(3, 1));
            player.getCards().add(new Card(4, 1));
            player.getCards().add(new Card(5, 1));
            //this.players.get(player).deal(this.deck.deal(5));
        }


    }

    /**
     * Sends a message to the given player containing their cards and relevant game information.
     *
     * @param pokerPlayer the player to send the message to
     */
    public void sendCards(PokerPlayer pokerPlayer) {
        var cardList = pokerPlayer.getCards();
        var cardDisplay = "";

        cardDisplay += "YOURCARDS";
        cardDisplay += " ";
        cardDisplay += pokerPlayer.getPlayerId();
        cardDisplay += "/";

        cardDisplay += pokerPlayer.getChangeDone() ? "1" : "0";
        cardDisplay += " ";
        cardDisplay += pokerPlayer.getAmountChanged();
        cardDisplay += "/";

        for (var card : cardList) {
            cardDisplay += card.toString();
            cardDisplay += "/";
        }

        pokerPlayer.getPlayer().send(new ITEMMSG(new String[]{this.game.getGameId(), cardDisplay, ""}));
    }

    /**
     * Sends a message to all players in the game containing the names and IDs of all opponents.
     */
    public void sendOpponents() {
        var packetData = new ArrayList<String>();
        packetData.add(this.game.getGameId());
        packetData.add("OPPONENTS");
        packetData.addAll(this.players.values().stream().map(x -> x.getPlayer().getDetails().getName()  + " " + x.getAmountChanged()).toList());
        packetData.add("");

        this.game.sendToEveryone(new ITEMMSG(packetData.toArray(String[]::new)));
    }

    public void sendChanged(PokerPlayer player) {
        var packetData = new ArrayList<String>();
        packetData.add(this.game.getGameId());
        packetData.add("CHANGED");
        packetData.add(player.getPlayer().getDetails().getName() + "/" + String.join(" ", Arrays.stream(player.getCardsChanged()).mapToObj(String::valueOf).toArray(String[]::new)));
        // packetData.add("");

        this.game.sendToOpponents(player.getPlayer(), new ITEMMSG(packetData.toArray(String[]::new)));
    }

    /**
     * Returns the PokerPlayer object that corresponds to the given Player object.
     *
     * @param player the Player object to look up
     * @return the corresponding PokerPlayer object, or null if the player is not found
     */
    public PokerPlayer getPlayer(Player player) {
        return this.players.get(player);
    }

    public void addPlayer(Player player) {
        this.players.put(player, new PokerPlayer(player, this.idCounter.getAndIncrement()));
    }

    public List<PokerPlayer> getPlayers() {
        return new ArrayList<>(this.players.values());
    }

    public Deck getDeck() {
        return deck;
    }
}