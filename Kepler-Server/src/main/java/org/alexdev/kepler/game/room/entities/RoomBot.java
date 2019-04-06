package org.alexdev.kepler.game.room.entities;

import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.bot.Bot;
import org.alexdev.kepler.game.entity.Entity;

import java.util.concurrent.TimeUnit;

public class RoomBot extends RoomEntity {
    private final Bot bot;

    public RoomBot(Entity entity) {
        super(entity);
        this.bot = (Bot) entity;
    }

    @Override
    public void stopWalking() {
        super.stopWalking();

        if (this.bot.getBotData() != null) {
            if (this.bot.getRoomUser().getPosition().getRotation() != this.bot.getBotData().getStartPosition().getRotation()) {
                GameScheduler.getInstance().getService().schedule(() -> {
                    this.getPosition().setRotation(this.bot.getBotData().getStartPosition().getRotation());
                    this.setNeedsUpdate(true);
                }, 500, TimeUnit.MILLISECONDS);
            }
        }
    }
}
