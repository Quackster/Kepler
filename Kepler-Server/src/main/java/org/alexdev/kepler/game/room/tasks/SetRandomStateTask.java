package org.alexdev.kepler.game.room.tasks;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.messages.outgoing.rooms.items.DICE_VALUE;
import org.alexdev.kepler.messages.outgoing.rooms.items.STUFFDATAUPDATE;

import java.util.concurrent.ThreadLocalRandom;

public class SetRandomStateTask implements Runnable {
    private final Item item;

    public SetRandomStateTask(Item item) {
        this.item = item;
    }

    @Override
    public void run() {
        if (!this.item.getRequiresUpdate()) {
            return;
        }

        int maxNumber = 0;

        if (this.item.getDefinition().getSprite().equals("val_randomizer")) {
            maxNumber = 4;
        }

        int randomNumber = ThreadLocalRandom.current().nextInt(1, maxNumber + 1);
        this.item.setCustomData(Integer.toString(randomNumber));

        this.item.getRoom().send(new STUFFDATAUPDATE(this.item));
        this.item.updateStatus();
        this.item.setRequiresUpdate(false);
        this.item.save();
    }
}
