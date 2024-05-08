package org.alexdev.kepler.game.room.tasks;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.pets.*;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PetTask extends TickTask {
    private final Pet pet;
    private final Room room;

    private int eatingTimer;
    private int drinkTimer;
    private int playTimer;
    private int interactionTimer;

    private static final PetAction[] possibleActions = {
            PetAction.WALK, PetAction.LAY, PetAction.SIT, PetAction.PLAY
    };


    public PetTask(Pet pet, Room room) {
        this.pet = pet;
        this.room = room;
    }

    public void tick() {
        this.tickTimers();

        if (!this.isTickRunnable())
            return;

        if (!this.canMove())
            return;

        if (this.pet.getHunger() <= 2) {
            if (this.tryEat()) {
                return;
            }
        }

        if (this.pet.getThirst() <= 1) {
            if (this.tryDrink()) {
                return;
            }
        }

        PetAction petAction = possibleActions[ThreadLocalRandom.current().nextInt(0, possibleActions.length - 1)];

        switch (petAction) {
            case WALK:
                this.walk();
                break;

            case SIT:
                this.sit();
                break;

            case PLAY:
                this.tryPlay();
                break;
                    /*
                case TALK:
                    this.sayRandomSpeech();
                    break;
                     */
            case LAY:
                this.lay();
                break;
        }

        if (ThreadLocalRandom.current().nextInt(0, 25) == 0) {
            this.talk();
        }


        this.setTimeUntilNextTick(ThreadLocalRandom.current().nextInt(10, 26));
    }

    public void walk() {
        Position availableTile = this.room.getMapping().getRandomWalkableBound(pet, false);

        if (availableTile != null) {
            pet.getRoomUser().walkTo(availableTile.getX(), availableTile.getY());
        }
    }

    public void sit() {
        this.sit(0);
    }

    public void sit(int time) {
        if (this.pet.getRoomUser().isWalking()) {
            this.pet.getRoomUser().stopWalking();
        }

        int rotation = this.pet.getRoomUser().getPosition().getRotation() / 2 * 2;

        this.pet.getRoomUser().getPosition().setRotation(rotation);

        if (time > 0) {
            this.pet.getRoomUser().setStatus(StatusType.SIT, StringUtil.format(pet.getRoomUser().getPosition().getZ()), time, null, -1, -1);
            this.interactionTimer = time;
        } else {
            this.pet.getRoomUser().setStatus(StatusType.SIT, StringUtil.format(pet.getRoomUser().getPosition().getZ()));
        }

        this.pet.getRoomUser().removeStatus(StatusType.LAY);
        this.pet.getRoomUser().setNeedsUpdate(true);
    }

    public void lay() {
        this.lay(0);
    }

    public void lay(int time) {
        if (this.pet.getRoomUser().isWalking()) {
            this.pet.getRoomUser().stopWalking();
        }

        int rotation = this.pet.getRoomUser().getPosition().getRotation() / 2 * 2;

        this.pet.getRoomUser().getPosition().setRotation(rotation);

        if (time > 0) {
            this.pet.getRoomUser().setStatus(StatusType.LAY, StringUtil.format(pet.getRoomUser().getPosition().getZ()), time, null, -1, -1);
            this.interactionTimer = time;
        } else {
            this.pet.getRoomUser().setStatus(StatusType.LAY, StringUtil.format(pet.getRoomUser().getPosition().getZ()));
        }

        this.pet.getRoomUser().removeStatus(StatusType.SIT);
        this.pet.getRoomUser().setNeedsUpdate(true);
    }

    private void tickTimers() {
        if (this.eatingTimer != 0) {
            this.eatingTimer--;

            if (this.eatingTimer == 1) {
                this.eatingComplete(true);
            }
        }

        if (this.drinkTimer != 0) {
            this.drinkTimer--;

            if (this.drinkTimer == 1) {
                this.drinkingComplete(true);
            }
        }


        if (this.playTimer != 0) {
            this.playTimer--;

            if (this.playTimer == 1) {
                this.playingComplete(true);
            }
        }

        if (this.interactionTimer != 0) {
            this.interactionTimer--;
        }
    }

    private boolean tryEat() {
        this.talk();
        final PetType petType = PetManager.getInstance().getType(this.pet);

        List<Item> foodInRoom =
                this.room.getItemManager().getFloorItems().stream()
                        .filter(item -> (item.hasBehaviour(ItemBehaviour.PET_FOOD) ||
                                        (
                                                (petType == PetType.CAT && item.hasBehaviour(ItemBehaviour.PET_CAT_FOOD)) ||
                                                (petType == PetType.DOG && item.hasBehaviour(ItemBehaviour.PET_DOG_FOOD))
                                        )) &&
                                        !item.getCustomData().equalsIgnoreCase("4") &&
                                        item.getTile().getOtherEntities(this.pet).isEmpty()).toList();



        if (foodInRoom.isEmpty()) {
            return false;
        }

        Item item = foodInRoom.get(0);

        if (item == null)
            return false;

        return this.pet.getRoomUser().walkTo(item.getPosition().getX(), item.getPosition().getY());
    }

    public void startEating() {
        this.pet.getRoomUser().setStatus(StatusType.EAT, "1");
        this.pet.getRoomUser().setNeedsUpdate(true);

        this.eatingTimer = 30;
    }


    public void eatingComplete(boolean reapBenefits) {
        this.pet.getRoomUser().removeStatus(StatusType.EAT);
        this.pet.getRoomUser().setStatus(StatusType.SMILE, "1", 30, null, -1, -1);
        this.pet.getRoomUser().setNeedsUpdate(true);

        if (reapBenefits) {
            this.pet.getDetails().setLastEat(DateUtil.getCurrentTimeSeconds());
            this.pet.saveDetails();

            var currentItem = this.pet.getRoomUser().getCurrentItem();

            if (currentItem != null &&
                currentItem.hasBehaviour(ItemBehaviour.PET_FOOD)) {
                
                if (StringUtils.isNumeric(currentItem.getCustomData())) {
                    int state = Integer.parseInt(currentItem.getCustomData()) + 1;

                    if (state <= 4) {
                        currentItem.setCustomData(String.valueOf(state));
                        currentItem.updateStatus();
                        currentItem.save();
                    }
                }
            }

            /*
            var currentItem = this.pet.getRoomUser().getCurrentItem();


            if (currentItem != null &&
                currentItem.hasBehaviour(ItemBehaviour.PET_FOOD) &&
                currentItem.getCustomData().equalsIgnoreCase("5")) {
                this.room.getMapping().removeItem(currentItem);
                currentItem.delete();
            }*/
        }

        this.talk();
    }

    private boolean tryDrink() {
        this.talk();
        // var petType = PetManager.getInstance().getType(this.pet);

        List<Item> drinkInRoom =
                this.room.getItemManager().getFloorItems().stream()
                        .filter(item -> item.hasBehaviour(ItemBehaviour.PET_WATER_BOWL) &&
                                !item.getCustomData().equalsIgnoreCase("0") &&
                                item.getTile().getOtherEntities(this.pet).isEmpty()).toList();

        if (drinkInRoom.isEmpty()) {
            return false;
        }

        Item item = drinkInRoom.get(0);

        if (item == null)
            return false;

        return this.pet.getRoomUser().walkTo(item.getPosition().getX(), item.getPosition().getY());
    }

    public void startDrinking() {
        this.pet.getRoomUser().setStatus(StatusType.EAT, "1");
        this.pet.getRoomUser().setNeedsUpdate(true);

        this.drinkTimer = 30;
    }


    public void drinkingComplete(boolean reapBenefits) {
        this.pet.getRoomUser().removeStatus(StatusType.EAT);
        this.pet.getRoomUser().setStatus(StatusType.SMILE, "1", 30, null, -1, -1);
        this.pet.getRoomUser().setNeedsUpdate(true);

        if (reapBenefits) {
            this.pet.getDetails().setLastDrink(DateUtil.getCurrentTimeSeconds());
            this.pet.saveDetails();

            var currentItem = this.pet.getRoomUser().getCurrentItem();

            if (currentItem != null &&
                currentItem.hasBehaviour(ItemBehaviour.PET_WATER_BOWL)) {

                if (StringUtils.isNumeric(currentItem.getCustomData())) {
                    int state = Integer.parseInt(currentItem.getCustomData()) - 1;

                    if (state >= 0) {
                        currentItem.setCustomData(String.valueOf(state));
                        currentItem.updateStatus();
                        currentItem.save();
                    }
                }
            }
        }

        this.talk();
    }

    private void tryPlay() {
        this.talk();
        final PetType petType = PetManager.getInstance().getType(this.pet);

        List<Item> foodInRoom =
                this.room.getItemManager().getFloorItems().stream()
                        .filter(item -> item.hasBehaviour(ItemBehaviour.PET_TOY) &&
                                        item.getTile().getOtherEntities(this.pet).isEmpty()).toList();



        if (foodInRoom.isEmpty()) {
            return;
        }

        Item item = foodInRoom.get(0);

        if (item == null)
            return;

        this.pet.getRoomUser().walkTo(item.getPosition().getX(), item.getPosition().getY());
    }

    public void startPlay() {
        this.pet.getRoomUser().setStatus(StatusType.PLAY, "1");
        this.pet.getRoomUser().setNeedsUpdate(true);

        this.talk();

        this.playTimer = 15;
    }


    public void playingComplete(boolean reapBenefits) {
        this.pet.getRoomUser().removeStatus(StatusType.PLAY);
        this.pet.getRoomUser().setStatus(StatusType.SMILE, "1", 30, null, -1, -1);
        this.pet.getRoomUser().setNeedsUpdate(true);

        if (reapBenefits) {
            this.pet.getDetails().setLastPlayToy(DateUtil.getCurrentTimeSeconds());
            this.pet.saveDetails();
        }

        this.talk();
    }

    public void talk() {
        this.pet.getRoomUser().talk(
                PetManager.getInstance().getRandomSpeech(pet.getDetails().getType()),
                CHAT_MESSAGE.ChatMessageType.CHAT
        );
    }

    public boolean canMove() {
        return this.eatingTimer == 0 && this.drinkTimer == 0 && this.playTimer == 0 && this.interactionTimer == 0;
    }

    public void jump(int length) {
        if (this.pet.getRoomUser().isWalking()) {
            this.pet.getRoomUser().stopWalking();
        }

        this.pet.getRoomUser().getStatuses().clear();
        this.pet.getRoomUser().getPosition().setRotation(pet.getRoomUser().getPosition().getBodyRotation());

        if (length > 0) {
            this.pet.getRoomUser().setStatus(StatusType.JUMP, StatusType.JUMP.getStatusCode().toLowerCase(), length, null, -1, -1);
            this.interactionTimer = length;
        } else {
            this.pet.getRoomUser().setStatus(StatusType.JUMP, StatusType.JUMP.getStatusCode().toLowerCase());
        }

        this.pet.getRoomUser().setNeedsUpdate(true);
    }

    public void playDead(int length) {
        if (this.pet.getRoomUser().isWalking()) {
            this.pet.getRoomUser().stopWalking();
        }

        this.pet.getRoomUser().getStatuses().clear();
        this.pet.getRoomUser().getPosition().setRotation(pet.getRoomUser().getPosition().getBodyRotation());

        if (length > 0) {
            this.pet.getRoomUser().setStatus(StatusType.DEAD, StatusType.DEAD.getStatusCode().toLowerCase(), length, null, -1, -1);
            this.interactionTimer = length;
        } else {
            this.pet.getRoomUser().setStatus(StatusType.DEAD, StatusType.DEAD.getStatusCode().toLowerCase());
        }

        this.pet.getRoomUser().setNeedsUpdate(true);

    }

    public void setInteractionTimer(int i) {
        this.interactionTimer = i;
    }
}
