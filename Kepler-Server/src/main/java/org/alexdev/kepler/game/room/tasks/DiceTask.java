package org.alexdev.kepler.game.room.tasks;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.messages.outgoing.rooms.items.DICE_VALUE;

import java.util.concurrent.ThreadLocalRandom;

public class DiceTask implements Runnable {
    private final Item dice;

    public DiceTask(Item dice) {
        this.dice = dice;
    }

    @Override
    public void run() {
        if (!this.dice.getRequiresUpdate()) {
            return;
        }

        int randomNumber = ThreadLocalRandom.current().nextInt(1, 7); // between 1 and 6

        this.dice.getRoom().send(new DICE_VALUE(this.dice.getId(), false, randomNumber));

        this.dice.setCustomData(Integer.toString(randomNumber));
        this.dice.updateStatus();
        this.dice.setRequiresUpdate(false);

        ItemDao.updateItem(this.dice);
    }
}
