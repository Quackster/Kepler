package org.alexdev.kepler.game.games.gamehalls.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public enum GamePokerCombination {
    FIVE_OF_A_KIND("a Five of a kind"),
    ROYAL_FLUSH("a Royal flush"),
    STRAIGHT_FLUSH("a Straight flush"),
    FOUR_OF_A_KIND("a Four of a kind"),
    FULL_HOUSE("a Full House"),
    FLUSH("a Flush"),
    STRAIGHT("a Straight"),
    THREE_OF_A_KIND("a Three of a kind"),
    TWO_PAIR("two pairs"),
    ONE_PAIR("one pair"),
    HIGH_CARD("an high card");

    final static List<String> Faces = Arrays.asList("2","3","4","5","6","7","8","9","10", "11", "12", "13", "1");
    final static List<String> Suits = Arrays.asList("h", "d", "c", "s");
    final static String Joker = "joker";

    private final String name;

    GamePokerCombination(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int getCombinationRank() {
        return Math.abs(this.ordinal() - 10);
    }

    public static List<String> getDeck() {
        List<String> deck = new ArrayList<>(Arrays.asList(Joker, Joker));

        for (String suit : Suits) {
            for (String face : Faces) {
                deck.add(suit + face);
            }
        }

        return deck;
    }

    public static GamePokerCombination getCombination(List<String> hand) {
        List<String> cards = hand.stream()
            .filter(card -> !card.equals(Joker))
            .collect(Collectors.toList());
        int jokers = hand.size() - cards.size();

        List<Integer> suits = cards.stream()
            .map(card -> Suits.indexOf(card.substring(0, 1)))
            .collect(Collectors.toList());
        List<Integer> faces = cards.stream()
            .map(card -> Faces.indexOf(card.substring(1)))
            .collect(Collectors.toList());


        if (cards.stream().anyMatch(card -> Collections.frequency(cards, card) > 1) ||
            faces.stream().anyMatch(face -> face == -1) ||
            suits.stream().anyMatch(suit -> suit == -1)) {
            return null;
        }

        boolean flush = suits.stream().allMatch(suit -> suit.equals(suits.get(0)));
        List<Integer> groups = Faces.stream()
            .map(face -> Collections.frequency(faces, Faces.indexOf(face)))
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());
        List<Integer> shifted = faces.stream()
            .map(face -> (face + 1) % 13)
            .collect(Collectors.toList());
        int distance = Math.min(Collections.max(faces) - Collections.min(faces),
            Collections.max(shifted) - Collections.min(shifted));
        boolean straight = groups.get(0) == 1 && distance < 5;
        boolean royal = straight && flush && !faces.stream().anyMatch(face -> face < 8);
        groups.set(0, groups.get(0) + jokers);

        if (groups.get(0) == 5) {
            return GamePokerCombination.FIVE_OF_A_KIND;
        } else if (royal) {
            return GamePokerCombination.ROYAL_FLUSH;
        } else if (straight && flush) {
            return GamePokerCombination.STRAIGHT_FLUSH;
        } else if (groups.get(0) == 4) {
            return GamePokerCombination.FOUR_OF_A_KIND;
        } else if (groups.get(0) == 3 && groups.get(1) == 2) {
            return GamePokerCombination.FULL_HOUSE;
        } else if (flush) {
            return GamePokerCombination.FLUSH;
        } else if (straight) {
            return GamePokerCombination.STRAIGHT;
        } else if (groups.get(0) == 3) {
            return GamePokerCombination.THREE_OF_A_KIND;
        } else if (groups.get(0) == 2 && groups.get(1) == 2) {
            return GamePokerCombination.TWO_PAIR;
        } else if (groups.get(0) == 2) {
            return GamePokerCombination.ONE_PAIR;
        } else {
            return GamePokerCombination.HIGH_CARD;
        }
    }
}