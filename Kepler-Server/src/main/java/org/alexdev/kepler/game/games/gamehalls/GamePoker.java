package org.alexdev.kepler.game.games.gamehalls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.alexdev.kepler.game.games.gamehalls.utils.GamePokerCombination;
import org.alexdev.kepler.game.games.gamehalls.utils.GamePokerOptions;
import org.alexdev.kepler.game.games.triggers.GameTrigger;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.games.ITEMMSG;
import org.alexdev.kepler.messages.types.MessageComposer;

public class GamePoker extends GamehallGame {
    public GamePoker(List<int[]> kvp) {
        super(kvp);
    }

    // Players that played at least one round (not on open screen)
    private List<Player> playersInGame;

    // Players that are currently in round
    private List<Player> playersPlaying;

    // Changes and cards for this round
    private List<String> deck;
    private Map<Player, String[]> playerChanges;
    private Map<Player, List<String>> playerCards;

    private GamePokerOptions options;


    @Override
    public void gameStart() {
        this.deck = new ArrayList<>();
        this.playerCards = new HashMap<>();
        this.playerChanges = new HashMap<>();
        this.playersInGame = new ArrayList<>();
        this.playersPlaying = new ArrayList<>();
        this.options = new GamePokerOptions(this);
    }

    @Override
    public void gameStop() {
        this.clearRound();
        this.playersInGame.clear();
    }

    @Override
    public void handleCommand(Player player, Room room, Item item, String command, String[] args) {
        GameTrigger trigger = (GameTrigger) item.getDefinition().getInteractionType().getTrigger();

        switch (command) {
            case "CLOSE" -> {
                trigger.onEntityLeave(player, player.getRoomUser(), item);
                this.getDisconnectedPlayers().stream().forEach(this::disconnectPlayer);
                if (this.isRoundRunning() && this.hasEveryoneChanged())
                    this.endRound();
                return;
            }
            case "OPEN" -> {
                this.getDisconnectedPlayers().stream().forEach(this::disconnectPlayer);
                this.broadcastOpponents(player);
                return;
            }
            case "STARTOVER" -> {
                if (!this.isRoundRunning()) {
                    this.clearRound();
                    this.deck.addAll(GamePokerCombination.getDeck());
                }
                this.addPlayerToGame(player);
                this.addPlayerToRound(player);
                this.giveCardsToPlayer(player);
                this.broadcastOpponents();
                return;
            }
            case "CHANGE" -> {
                this.getDisconnectedPlayers().stream().forEach(this::disconnectPlayer);
                this.changeCards(player, args);
                this.broadcastOpponents();
                if (this.isRoundRunning() && this.hasEveryoneChanged())
                    this.endRound();
                return;
            }
        }
    }

    private void addPlayerToGame(Player player) {
        if (this.playersInGame.contains(player))
            return;

        this.playersInGame.add(player);
        this.broadcastOpponents();
    }

    private void addPlayerToRound(Player player) {
        this.playersPlaying.add(player);

        // Update player with latest state
        this.playerChanges.keySet().stream()
            .map(key -> this.formatPlayerChangedUpdate(key))
            .forEach(entry -> player.send(new ITEMMSG(entry)));

        this.options.onEntry(player);
    }

    private void giveCardsToPlayer(Player player) {
        List<String> cards = IntStream.rangeClosed(0, 4)
            .mapToObj(i -> this.deck.remove(ThreadLocalRandom.current().nextInt(this.deck.size())))
            .toList();
        this.playerCards.put(player, cards);
        this.playerChanges.remove(player);

        player.send(new ITEMMSG(new String[]{this.getGameId(), "YOURCARDS 0/" + this.formatRevealCardsUpdate(player)}));
    }

    private void changeCards(Player player, String[] sIndexes) {
        if (this.playerChanges.containsKey(player))
            return;

        List<String> playerHand = this.playerCards.get(player);
        List<Integer> cardsToChange = Arrays.asList(sIndexes)
            .stream()
            .filter(predicate -> !predicate.isBlank())
            .map(Integer::parseInt)
            .filter(index -> index >= 0 && index < playerHand.size())
            .sorted().collect(Collectors.toList());

        List<String> changeHand = playerHand.stream()
            .map(card -> {
                int index = playerHand.indexOf(card);
                if (cardsToChange.contains(index))
                    return this.deck.remove(ThreadLocalRandom.current().nextInt(this.deck.size()));
                else
                    return card;
            })
            .collect(Collectors.toList());

        String[] cardsChanged = cardsToChange.stream().map(String::valueOf).toArray(String[]::new);

        this.playerCards.put(player, changeHand);
        this.playerChanges.put(player, cardsChanged);

        // Send change event & reveal new cards to player
        this.sentToPlayingOpponents(player, new ITEMMSG(
            this.formatPlayerChangedUpdate(player)
        ));

        player.send(new ITEMMSG(
            String.join(Character.toString((char) 13), new String[]{
                this.getGameId(),
                "YOURCARDS 0/" + this.formatRevealCardsUpdate(player)
            }))
        );
    }

    private String formatPlayerChangedUpdate(Player player) {
        if (!this.playerChanges.containsKey(player))
            return "";
        String[] cardsChanged = this.playerChanges.get(player);
        String change = player.getDetails().getName() + "/" + String.join(" ", cardsChanged);

        return String.join(Character.toString((char) 13), new String[]{
            this.getGameId(),
            "CHANGED",
            change
        });
    }

    private String formatRevealCardsUpdate(Player player) {
        return String.join("/", new String[] {
            String.join(",", this.playerChanges.containsKey(player) ?  "1" : "0", Integer.toString(this.playerChanges.getOrDefault(player, new String[]{}).length)),
            String.join("/", this.playerCards.get(player))
        });
    }

    private void disconnectPlayer(Player player) {
        if (!this.playersInGame.contains(player))
            return;

        this.playersInGame.remove(player);
        this.playersPlaying.remove(player);
        this.playerChanges.remove(player);
        this.playerCards.remove(player);

        ITEMMSG itemMessage = new ITEMMSG(String.join(Character.toString((char) 13),
            this.getGameId(),
            "OPPONENT_LOGOUT",
            player.getDetails().getName()
        ));

        for (Player p : this.playersInGame)
            p.send(itemMessage);
    }

    private void broadcastOpponents() {
        for (Player player: this.playersInGame) {
            this.broadcastOpponents(player);
        }
    }

    private void broadcastOpponents(Player player) {
        List<String> opponentData = new ArrayList<>();
        opponentData.add(player.getDetails().getName() + " " + Integer.toString(this.playerChanges.getOrDefault(player, new String[]{}).length));

        for (Player p: this.playersInGame) {
            if (p == player)
                continue;
            opponentData.add(p.getDetails().getName() + " " + Integer.toString(this.playerChanges.getOrDefault(p, new String[]{}).length));
        }
        player.send(new ITEMMSG(new String[]{this.getGameId(), "OPPONENTS", String.join(Character.toString((char) 13), opponentData)}));
    }

    private void revealCards() {
        // Reveal cards to everyone
        List<String> playerUpdates = new ArrayList<>();
        for (Player player : this.playersPlaying)
            playerUpdates.add(player.getDetails().getName() + "/" + this.formatRevealCardsUpdate(player));

        this.sendToPlaying(new ITEMMSG(new String[]{
            this.getGameId(),
            "REVEALCARDS",
            String.join(Character.toString((char) 13), playerUpdates)
        }));
    }

    private void endRound() {
        this.options.onFinish(playerCards);
        this.revealCards();
        this.clearRound();
    }

    private void clearRound() {
        this.options.clear();
        this.deck.clear();
        this.playerCards.clear();
        this.playerChanges.clear();
        this.playersPlaying.clear();
    }

    private List<Player> getDisconnectedPlayers() {
        List<Player> toDisconnect = new ArrayList<>();

        for (Player player: this.playersInGame) {
            if (!this.getPlayers().contains(player))
                toDisconnect.add(player);
        }

        return toDisconnect;
    }

    private boolean isRoundRunning() {
        return !this.deck.isEmpty();
    }

    private boolean hasEveryoneChanged() {
        return this.playersPlaying.stream().filter(p -> this.playerChanges.containsKey(p)).count() == this.playersPlaying.size();
    }

    /**
     * Send message to all players in game (including sender)
     * @param messageComposer
     */
    public void sendToPlaying(MessageComposer messageComposer) {
        for (Player p : this.playersPlaying) {
            p.send(messageComposer);
        }
    }

    /**
     * Send message to all opponents in game (excluding sender)
     * @param sender
     * @param messageComposer
     */
    public void sentToPlayingOpponents(Player sender, MessageComposer messageComposer) {
        for (Player p : this.playersPlaying) {
            if (p == sender)
                continue;
            p.send(messageComposer);
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
