package net.h4bbo.kepler.game.room.tasks;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.outgoing.rooms.items.BROADCAST_TELEPORTER;

public class TeleporterTask implements Runnable {
    private final Item item;
    private final Entity entity;
    private final Room room;

    public TeleporterTask(Item linkedTeleporter, Entity entity, Room room) {
        this.item = linkedTeleporter;
        this.entity = entity;
        this.room = room;
    }

    @Override
    public void run() {
        this.entity.getRoomUser().warp(this.item.getPosition().copy(), true, false);

        if (this.entity.getType() == EntityType.PLAYER) {
            Player player = (Player) this.entity;
            player.getRoomUser().setAuthenticateTelporterId(-1);
        }

        this.room.send(new BROADCAST_TELEPORTER(this.item, this.entity.getDetails().getName(), false));
    }
}
