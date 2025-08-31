package org.alexdev.kepler.game.wordfilter;

import org.alexdev.kepler.dao.mysql.BanDao;
import org.alexdev.kepler.dao.mysql.WordfilterDao;
import org.alexdev.kepler.game.ban.BanManager;
import org.alexdev.kepler.game.ban.BanType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WordfilterManager {
    private static WordfilterManager instance;
    private List<WordfilterWord> bannedWords;

    public WordfilterManager() {
        this.bannedWords = WordfilterDao.getBadWords();
        this.bannedWords.sort(Comparator.comparingInt((WordfilterWord word) -> word.getWord().length()).reversed());
    }

    /**
     * Filter sentence, mandatory filtered words
     *
     * @param sentence the sentence to filter
     * @return the filtered sentence
     */
    public static boolean hasBannableSentence(Player player, String sentence) {
        int minsSinceJoined = (int) Math.floor(TimeUnit.SECONDS.toMinutes((long) (DateUtil.getCurrentTimeSeconds() - Math.floor(player.getDetails().getJoinDate()))));

        if (minsSinceJoined > 30) {
            return false;
        }

        if (GameConfiguration.getInstance().getBoolean("wordfitler.enabled")) {
            for (WordfilterWord filterWord : WordfilterManager.getInstance().getBannedWords()) {
                if (!filterWord.isBannable()) {
                    continue;
                }

                if (sentence.toLowerCase().contains(filterWord.getWord().toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Filter sentence, mandatory filtered words
     *
     * @param sentence the sentence to filter
     * @return the filtered sentence
     */
    public static String filterMandatorySentence(String sentence) {
        if (GameConfiguration.getInstance().getBoolean("wordfitler.enabled")) {
            for (WordfilterWord filterWord : WordfilterManager.getInstance().getBannedWords()) {
                if (filterWord.isFilterable()) {
                    continue;
                }

                var word = filterWord.getWord();

                if (sentence.toLowerCase().contains(word.toLowerCase())) {
                    sentence = sentence.toLowerCase().replace(word.toLowerCase(), GameConfiguration.getInstance().getString("wordfilter.word.replacement"));
                }
            }
        }

        return sentence;
    }

    /**
     * Filter sentence, if wordfilter is enabled.
     *
     * @param sentence the sentence to filter
     * @return the filtered sentence
     */
    public static String filterSentence(String sentence) {
        if (GameConfiguration.getInstance().getBoolean("wordfitler.enabled")) {
            for (WordfilterWord filterWord : WordfilterManager.getInstance().getBannedWords()) {
                var word = filterWord.getWord();

                if (sentence.toLowerCase().contains(word.toLowerCase())) {
                    sentence = sentence.toLowerCase().replace(word.toLowerCase(), GameConfiguration.getInstance().getString("wordfilter.word.replacement"));
                }
            }
        }

        return sentence;
    }

    /**
     * Perform wordfiler ban
     * @param player the player to ban
     */
    public static void performBan(Player player) {
        long in20Years = DateUtil.getCurrentTimeSeconds() + (TimeUnit.DAYS.toSeconds(365) * 20);
        BanDao.addBan(BanType.USER_ID, String.valueOf(player.getDetails().getId()), in20Years, "Banned for breaking the Habbo Way", -1);

        BanManager.getInstance().disconnectBanAccounts(new HashMap<>() {{
            put(BanType.USER_ID, String.valueOf(player.getDetails().getId()));
        }});
    }

    /**
     * Get a list of banned words.
     *
     * @return the list of banned words
     */
    public List<WordfilterWord> getBannedWords() {
        return bannedWords;
    }

    /**
     * Get the {@link WordfilterManager} instance
     *
     * @return the item manager instance
     */
    public static WordfilterManager getInstance() {
        if (instance == null) {
            instance = new WordfilterManager();
        }

        return instance;
    }

    /**
     * Reloads the singleton for the {@link WordfilterManager}.
     */
    public static void reset() {
        instance = null;
        getInstance();
    }
}
