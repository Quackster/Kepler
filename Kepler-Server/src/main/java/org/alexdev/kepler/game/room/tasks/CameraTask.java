package org.alexdev.kepler.game.room.tasks;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.messages.outgoing.rooms.user.USER_STATUSES;

import java.util.List;

public class CameraTask implements Runnable {
    private final Entity entity;

    public CameraTask(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void run() {
        if (this.entity.getRoomUser().getRoom() == null) {
            return;
        }

        String item = entity.getRoomUser().getStatus(StatusType.USE_ITEM).getValue();

        this.entity.getRoomUser().removeStatus(StatusType.USE_ITEM);
        this.entity.getRoomUser().setStatus(StatusType.CARRY_ITEM, item);

        if (!this.entity.getRoomUser().isWalking()) {
            this.entity.getRoomUser().getRoom().send(new USER_STATUSES(List.of(this.entity)));
        }
    }
}
