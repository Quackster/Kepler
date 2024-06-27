package org.alexdev.kepler.game.games.gamehalls.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.alexdev.kepler.dao.mysql.CurrencyDao;
import org.alexdev.kepler.game.games.gamehalls.GamePoker;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.items.ITEM_DELIVERED;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;
import org.alexdev.kepler.messages.outgoing.user.currencies.TICKET_BALANCE;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.ThreadLocalRandom;

import org.alexdev.kepler.game.catalogue.CatalogueItem;
import org.alexdev.kepler.game.catalogue.CatalogueManager;

public class GamePokerOptions {

    final private GameConfiguration configuration = GameConfiguration.getInstance();
    final private GamePoker room;

    final private int entryPrice;
    final private int minPlayers;

    final private boolean redistributeEntry;
    final private boolean redistributeOnTie;

    final private int creditsBonus;
    final private boolean creditsBonusOnTie;

    final private String[] raresReward;
    final private int raresQuantity;
    final private boolean raresOnTie;

    final private int ticketsReward;
    final private boolean ticketsOnTie;

    final private boolean announceWinner;
    final private boolean announceRewards;

    // Stateful - will reset every round
    private int entryPaid = 0;
    final private List<String> rewardMessages = new ArrayList<>();


    public GamePokerOptions(GamePoker room) {
        this.room = room;
        this.entryPrice = this.getInteger("poker.entry.price", 0);
        this.minPlayers = this.getInteger("poker.reward.min.player", 2);

        this.redistributeEntry = this.getBoolean("poker.entry.price.redistribute", true);
        this.redistributeOnTie = this.getBoolean("poker.entry.price.redistribute.on.tie", true);

        this.creditsBonus = this.getInteger("poker.reward.credits.bonus", 0);
        this.creditsBonusOnTie = this.getBoolean("poker.reward.credits.bonus.on.tie", false);

        this.raresReward = this.getString("poker.reward.rares", "").split(",");
        this.raresQuantity = this.getInteger("poker.reward.rares.quantity", 0);
        this.raresOnTie = this.getBoolean("poker.reward.rares.on.tie", false);

        this.ticketsReward = this.getInteger("poker.reward.tickets", 0);
        this.ticketsOnTie = this.getBoolean("poker.reward.tickets.on.tie", false);

        this.announceWinner = this.getBoolean("poker.announce.winner", true);
        this.announceRewards = this.getBoolean("poker.announce.rewards", false);
    }

    public void onEntry(Player player) {
        if (this.entryPrice <= 0)
            return;
        CurrencyDao.decreaseCredits(player.getDetails(), this.entryPrice);
        player.send(new CREDIT_BALANCE(player.getDetails()));
        this.entryPaid += this.entryPrice;
    }

    public void clear() {
        this.entryPaid = 0;
        this.rewardMessages.clear();
    }

    public void onFinish(Map<Player, List<String>> _players) {
        List<Player> playing = _players.keySet().stream().collect(Collectors.toList());
        Pair<GamePokerCombination, List<Player>> winners = this.getWinners(_players);
        GamePokerCombination winningCombination = winners.getLeft();
        List<Player> winnerList = winners.getRight();

        if (this.announceWinner)
            this.showChat(playing, this.getWinnerMessage(winningCombination, winnerList));

        if (playing.size() < this.minPlayers || !this.canReward(winnerList))
            return;

        this.rewardCredits(winnerList);
        this.rewardRares(winnerList);
        this.rewardTickets(winnerList);

        if (this.announceRewards)
            this.rewardMessages.forEach(msg -> this.showChat(playing, msg));
    }

    private void rewardCredits(List<Player> winners) {
        int credits = 0;

        if (this.redistributeEntry && (winners.size() == 1 || this.redistributeOnTie))
            credits += this.entryPaid;

        if (this.creditsBonus > 0 && (winners.size() == 1 || this.creditsBonusOnTie))
            credits += this.creditsBonus;

        if (credits == 0)
            return;

        int creditsPerWinner = (int) Math.floor(credits / winners.size());

        for (Player winner : winners) {
            CurrencyDao.increaseCredits(winner.getDetails(), creditsPerWinner);
            winner.send(new CREDIT_BALANCE(winner.getDetails()));
        }

        if (winners.size() > 1)
            this.rewardMessages.add("Each winner received " + creditsPerWinner + " credits!");
        else
            this.rewardMessages.add(winners.get(0).getDetails().getName() + " received " + creditsPerWinner + " credits!");
    }

    private void rewardRares(List<Player> winners) {
        if (this.raresReward.length == 0 || this.raresQuantity == 0 || (winners.size() > 1 && !this.raresOnTie))
            return;

        CatalogueManager catalogue = CatalogueManager.getInstance();
        CatalogueItem[] rares = new CatalogueItem[this.raresQuantity];
        String[] rareNames = new String[this.raresQuantity];

        for (int i = 0; i < this.raresQuantity; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(0, this.raresReward.length);
            CatalogueItem rare = catalogue.getCatalogueItem(this.raresReward[randomIndex]);
            rares[i] = rare;
            rareNames[i] = rare.getName();
        }


        for (Player winner : winners) {
            for (CatalogueItem rare : rares) {
                try {
                    catalogue.purchase(winner, rare, null, null, DateUtil.getCurrentTimeSeconds());
                } catch (SQLException e) {
                    // todo: handle exception
                    e.printStackTrace();
                    return;
                }
            }
            winner.getInventory().getView("new");
            // winner.send(new ITEM_DELIVERED());
        }

        if (winners.size() > 1)
            this.rewardMessages.add("Each winner received " + String.join(", ", rareNames) + " !");
        else
            this.rewardMessages.add(winners.get(0).getDetails().getName() + " received " + String.join(", ", rareNames) + " !");
    }

    private void rewardTickets(List<Player> winners) {
        if (this.ticketsReward <= 0 || (winners.size() > 1 && !this.ticketsOnTie))
            return;

        for (Player winner : winners) {
            CurrencyDao.increaseTickets(winner.getDetails(), this.ticketsReward);
            winner.send(new TICKET_BALANCE(winner.getDetails().getTickets()));
        }

        if (winners.size() > 1)
            this.rewardMessages.add("Each winner received " + this.ticketsReward + " tickets!");
        else
            this.rewardMessages.add(winners.get(0).getDetails().getName() + " received " + this.ticketsReward + " tickets!");
    }
    /**
     * Show chat message to all players in game
     * @param chat
     */
    private void showChat(List<Player> players, String chat) {
        for (Player p : players) {
            p.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.CHAT, p.getRoomUser().getInstanceId(), chat));
        }
    }

    /**
     * Does the option apply to the current room?
     * @param option
     * @return
     */
    private boolean activeInRoom(String option) {
        String rooms = this.configuration.getString(option + ".only.in.rooms", "");
        return rooms.isBlank() || rooms.contains(Integer.toString(this.room.getRoomId()));
    }

    private String getString(String name, String def) {
        String value = this.configuration.getString(name, def);
        if (!this.activeInRoom(name))
            return def;
        return value;
    }

    private int getInteger(String name, int def) {
        int value = this.configuration.getInteger(name);
        if (!this.activeInRoom(name))
            return def;
        return value;
    }

    private boolean getBoolean(String name, boolean def) {
        boolean value = this.configuration.getBoolean(name);
        if (!this.activeInRoom(name))
            return def;
        return value;
    }

    private boolean canReward(List<Player> winners) {
        boolean isTie = winners.size() > 1;
        return (
            (this.redistributeEntry && this.entryPaid > 0 && (!isTie || this.redistributeOnTie)) ||
            (this.raresReward.length > 0 && (!isTie || this.raresOnTie)) ||
            (this.ticketsReward > 0 && (!isTie || this.ticketsOnTie)) ||
            (this.creditsBonus > 0 && (!isTie || this.creditsBonusOnTie))
        );
    }

    private String getWinnerMessage(GamePokerCombination combination, List<Player> winners) {
        if (winners.size() == 1) {
            String name = winners.get(0).getDetails().getName();
            return name + " wins this round with " + combination.getName() + ". Congratulations!";
        } else {
            String names = String.join(", ", winners.stream().map(p -> p.getDetails().getName()).collect(Collectors.toList()));
            return "It's a tie! " + names + " all have " + combination.getName() + ". Congratulations!";
        }
    }

    private Pair<GamePokerCombination, List<Player>> getWinners(Map<Player, List<String>> players) {
        List<Player> winners = new ArrayList<>();
        GamePokerCombination winningCombination = null;

        // Calculate winners
        for (Player player : players.keySet()) {
            List<String> hand = players.get(player);
            GamePokerCombination combination = GamePokerCombination.getCombination(hand);
            if (winningCombination == null) {
                winningCombination = combination;
                winners.add(player);
                continue;
            }
            if (combination.getCombinationRank() > winningCombination.getCombinationRank()) {
                winningCombination = combination;
                winners.clear();
                winners.add(player);
            } else if (combination.getCombinationRank() == winningCombination.getCombinationRank()) {
                winners.add(player);
            }
        }
        return Pair.of(winningCombination, winners);
    }
}
