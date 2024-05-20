package org.alexdev.kepler.game.pets;

import org.alexdev.kepler.game.fuserights.Fuse;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.StringUtil;

import java.util.List;
import java.util.Locale;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class PetManager {
    private static PetManager instance;

    private String[] dogSpeech = new String[] { "Vuuf... vuuuufff...", "Wuuuuuuuuuuuuuuuuuffff... vov", "Nomnomnomnom..", "Grrrr....", "Grrrr.. grrrrr.." };
    private String[] catSpeech = new String[] { "Prrrrrrr... prrrrrr", "Prrrrrrrrrrrrrrrr...... prrrr..", "Meowwwww... meowwww..", "Meowwww!" };
    private String[] crocSpeech = new String[] { "Gnawwwwwwww... gnaw..", "Gnawwwwwwwwwwwwwwwwwwwww.... gnawwwwwwww.....", "Gnaw! Gnaw!" };

    /**
     * Handle speech for pets.
     *
     * @param player the player to call it
     * @param speech the speech to do
     */
    public void handleSpeech(Player player, Room room, String speech) {
        String[] data = speech.split(" ");

        if (data.length < 2)
            return;

        if (!room.hasRights(player.getDetails().getId()) && !player.hasFuse(Fuse.ANY_ROOM_CONTROLLER)) {
            return;
        }

        for (Pet pet : room.getEntityManager().getEntitiesByClass(Pet.class)) {
            if (pet.getDetails().getName().toLowerCase().equals(data[0].toLowerCase())) {
                if (pet.getRoomUser().getTask().canMove()) {
                    petCommand(player, room, pet, speech.replace(data[0] + " ", ""));
                }
            }
        }
    }

    private void petCommand(Player player, Room room, Pet pet, String command) {
        var item = pet.getRoomUser().getCurrentItem();
        String petCommand = command.toLowerCase();

        List<String> eatCommands = List.of("spis", "eat");
        List<String> drinkCommands = List.of("drik", "drink");
        List<String> speakCommands = List.of("giv lyd", "lyd", "tal", "speak");
        List<String> begCommands = List.of("tig", "beg");
        List<String> goCommands = List.of("gå", "gå væk", "go", "go away");
        List<String> comeCommands = List.of("kom", "kom her", "på plads", "come over", "come here", "come", "heel");
        List<String> deadCommands = List.of("død", "spil død", "play dead", "dead");
        List<String> sitCommands = List.of("sit");
        List<String> layCommands = List.of("lig ned", "lig", "dæk", "lie down", "lay");
        List<String> jumpCommands = List.of("hop", "jump");

        if(eatCommands.contains(petCommand))
            pet.getRoomUser().tryEating();
        else if(drinkCommands.contains(petCommand))
            pet.getRoomUser().tryDrinking();
        else if(speakCommands.contains(petCommand)) {
            pet.getRoomUser().getTask().talk();
            pet.getRoomUser().getTask().setInteractionTimer(5);
        }
        else if(begCommands.contains(petCommand)) {
            // Beg for reward
        }
        else if(goCommands.contains(petCommand)) {
            pet.getRoomUser().getTask().walk();
            pet.getRoomUser().getTask().setInteractionTimer(10);
        }
        else if(comeCommands.contains(petCommand)) {
            pet.getRoomUser().walkTo(player.getRoomUser().getPosition().getSquareInFront().getX(), player.getRoomUser().getPosition().getSquareInFront().getY());
            pet.getRoomUser().getTask().setInteractionTimer(10);
        }
        else if(deadCommands.contains(petCommand)) {
            int length = ThreadLocalRandom.current().nextInt(4, 11);
            pet.getRoomUser().getTask().playDead(length);
        }
        else if(sitCommands.contains(petCommand)) {
            int length = ThreadLocalRandom.current().nextInt(10, 30);
            pet.getRoomUser().getTask().sit(length);
        }
        else if(layCommands.contains(petCommand)) {
            if (pet.getRoomUser().isWalking()) {
                pet.getRoomUser().stopWalking();
            }

            int length = ThreadLocalRandom.current().nextInt(10, 30);
            pet.getRoomUser().getTask().lay(length);
        }
        else if(jumpCommands.contains(petCommand)) {
            if (pet.getRoomUser().isWalking()) {
                pet.getRoomUser().stopWalking();
            }

            int length = ThreadLocalRandom.current().nextInt(2, 4);
            pet.getRoomUser().getTask().jump(length);
        }

    }

    /*
    private void petCommand(Player player, Room room, Pet pet, String command) {
        var item = pet.getRoomUser().getCurrentItem();
        boolean petCommanded = false;

        switch (command.toLowerCase()) {
            case "speak": {
                // Bark, meow, etc
                break;
            }
            case "good": {
                // Boosts pet's ego and makes them happy
                break;
            }
            case "beg": {
                // Beg for reward
                break;
            }
            case "go":
            case "go away": {
                // Pet moves away from player
                break;
            }
            case "bad": {
                // Tells pet off
                break;
            }
            case "come over":
            case "come":
            case "heel": {
                // Follow player
                break;
            }
            case "play dead":
            case "dead": {
                int length = ThreadLocalRandom.current().nextInt(4, 11);

                pet.getRoomUser().getStatuses().clear();
                pet.getRoomUser().getPosition().setRotation(pet.getRoomUser().getPosition().getBodyRotation());
                pet.getRoomUser().setStatus(StatusType.DEAD, StatusType.DEAD.getStatusCode(), length, null, -1, -1);
                pet.getRoomUser().setNeedsUpdate(true);

                pet}.setAction(PetAction.DEAD);
                pet.setActionDuration(length);
                petCommanded = true;
                break;
            }
            case "sit": {
                int length = ThreadLocalRandom.current().nextInt(10, 30);

                pet.getRoomUser().getStatuses().clear();
                pet.getRoomUser().getPosition().setRotation(pet.getRoomUser().getPosition().getBodyRotation());
                pet.getRoomUser().setStatus(StatusType.SIT, StringUtil.format(pet.getRoomUser().getPosition().getZ()), length, null, -1, -1);
                pet.getRoomUser().setNeedsUpdate(true);

                pet.setAction(PetAction.SIT);
                pet.setActionDuration(length);
                petCommanded = true;
                break;
            }
            case "lie down":
            case "lay": {
                pet.getRoomUser().getStatuses().clear();
                pet.getRoomUser().getPosition().setRotation(pet.getRoomUser().getPosition().getBodyRotation());
                pet.getRoomUser().setStatus(StatusType.LAY, StringUtil.format(pet.getRoomUser().getPosition().getZ()) + " null");
                pet.getRoomUser().setNeedsUpdate(true);

                pet.setAction(PetAction.LAY);
                pet.setActionDuration(ThreadLocalRandom.current().nextInt(10, 30));

                petCommanded = true;
                break;
            }
            case "jump": {
                if (!pet.isActionAllowed()) {
                    return;
                }

                int length = ThreadLocalRandom.current().nextInt(2, 4);

                pet.getRoomUser().getStatuses().clear();
                pet.getRoomUser().getPosition().setRotation(pet.getRoomUser().getPosition().getBodyRotation());
                pet.getRoomUser().setStatus(StatusType.JUMP, StatusType.JUMP.getStatusCode().toLowerCase(), length, null, -1, -1);
                pet.getRoomUser().setNeedsUpdate(true);

                pet.setAction(PetAction.JUMP);
                pet.setActionDuration(length);
                petCommanded = true;
                break;
            }
            case "sleep": {
                if (pet.isDoingAction()) {
                    return;
                }

                Item nest = room.getItemManager().getById(pet.getDetails().getItemId());
                pet.getRoomUser().walkTo(nest.getPosition().getX(), nest.getPosition().getY());

                if (pet.getRoomUser().isWalking()) {
                    pet.setAction(PetAction.SLEEP);
                } else {
                    if (item != null) {
                        if (item.getId() == pet.getDetails().getItemId()) {
                            item.getDefinition().getInteractionType().getTrigger().onEntityStop(pet, pet.getRoomUser(), item, false);
                            pet.setAction(PetAction.SLEEP);
                        }
                    }
                }

                if (pet.getAction() == PetAction.SLEEP) {
                    pet.getRoomUser().getStatuses().clear();
                    pet.getRoomUser().setNeedsUpdate(true);
                }

                break;
            }
            case "awake": {
                if (pet.getAction() != PetAction.SLEEP) {
                    return;
                }

                pet.awake();
                break;
            }
        }

        if (petCommanded) {
            if (pet.getRoomUser().isWalking()) {
                pet.getRoomUser().stopWalking();
            }
        }
    }
     */

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

        if (name.contains(" "))  {
            return false;
        }

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

    public PetType getType(Pet pet) {
        switch (pet.getDetails().getType()) {
            case "0": {
                return PetType.DOG;
            }
            case "1": {
                return PetType.CAT;
            }
            case "2": {
                return PetType.CROC;
            }
        }

        return null;
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
