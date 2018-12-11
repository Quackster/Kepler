package org.alexdev.kepler.game.room.tasks;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.messages.outgoing.rooms.user.USER_OBJECTS;
import org.alexdev.kepler.messages.outgoing.rooms.user.USER_STATUSES;

import java.util.List;

public class WaveTask implements Runnable {
    private final Entity entity;

    public WaveTask(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void run() {
        if (this.entity.getRoomUser().getRoom() == null) {
            return;
        }

        this.entity.getRoomUser().removeStatus(StatusType.WAVE);

        if (!this.entity.getRoomUser().isWalking()) {
            this.entity.getRoomUser().getRoom().send(new USER_STATUSES(List.of(this.entity)));
        }
    }
}
