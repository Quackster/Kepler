package net.h4bbo.kepler.game.room.tasks;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.room.enums.StatusType;
import net.h4bbo.kepler.messages.outgoing.rooms.user.USER_STATUSES;

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
