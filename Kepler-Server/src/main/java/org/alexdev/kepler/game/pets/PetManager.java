package org.alexdev.kepler.game.pets;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.StringUtil;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class PetManager {
    private static PetManager instance;

    private String[] dogSpeech = new String[] { "Woooff... wooofff...", "Woooooooooooooooffff... wooff", "Nomnomnomnom..", "Grrrr....", "Grrrr.. grrrrr.." };
    private String[] catSpeech = new String[] { "Prrrrrrr... prrrrrr", "Prrrrrrrrrrrrrrrr...... prrrr..", "Meowwwww... meowwww..", "Meowwww!" };
    private String[] crocSpeech = new String[] { "Gnawwwwwwww... gnaw..", "Gnawwwwwwwwwwwwwwwwwwwww.... gnawwwwwwww.....", "Gnaw! Gnaw!" };

    /**
     * Handle speech for pets.
     *
     * @param player the player to call it
     * @param speech the speech to do
     */
    public void handleSpeech(Player player, String speech) {

    }

    /**
     * Get the pet stats when given the last time and stat type.
     *
     * @param lastTime the last time for an action
     * @param stat the current pet stat
     * @return the stat type
     */
    public int getPetStats(long lastTime, PetStat stat) {
        int a = (int) TimeUnit.SECONDS.toHours(DateUtil.getCurrentTimeSeconds() - lastTime);

        if (a < 2) {
            return stat.getAttributeType();
        }

        for (int x = 1; x <= stat.getAttributeType(); x++) {
            if (a > (2 * x))
                return x;
        }

        return stat.getAttributeType();

    }

    /**
     * Get if the pet name is valid.
     *
     * @param ownerName
     * @param name the name of the pet
     */
    public boolean isValidName(String ownerName, String name) {
        String[] words = StringUtil.getWords(name);

        /*for (String word : words) {
            if (WordfilterManager.getInstance().getBannedWords().contains(word)) {
                return false;
            }
        }*/

        if (name.length() > 15) {
            return false;
        }

        if (name.length() < 1) {
            return false;
        }

        if (ownerName.toLowerCase().equals(name.toLowerCase())) {
            return false;
        }

        return true;
    }

    /**
     * Gets a random speech element by pet type.
     *
     * @param petType the pet type given
     * @return the speech selected
     */
    public String getRandomSpeech(String petType) {
        String speech = null;

        switch (petType) {
            case "0": {
                speech = this.dogSpeech[ThreadLocalRandom.current().nextInt(0, this.dogSpeech.length)];
                break;
            }
            case "1": {
                speech = this.catSpeech[ThreadLocalRandom.current().nextInt(0, this.catSpeech.length)];
                break;
            }
            case "2": {
                speech = this.crocSpeech[ThreadLocalRandom.current().nextInt(0, this.crocSpeech.length)];
                break;
            }
        }

        return speech;
    }

    /**
     * Get the {@link PetManager} instance
     *
     * @return the item manager instance
     */
    public static PetManager getInstance() {
        if (instance == null) {
            instance = new PetManager();
        }

        return instance;
    }
}
