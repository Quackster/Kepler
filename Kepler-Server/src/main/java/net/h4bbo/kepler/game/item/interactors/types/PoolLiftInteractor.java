package net.h4bbo.kepler.game.item.interactors.types;

import net.h4bbo.kepler.dao.mysql.CurrencyDao;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.entities.RoomEntity;
import net.h4bbo.kepler.game.triggers.GenericTrigger;
import net.h4bbo.kepler.messages.outgoing.rooms.pool.JUMPINGPLACE_OK;
import net.h4bbo.kepler.messages.outgoing.user.currencies.TICKET_BALANCE;

public class PoolLiftInteractor extends GenericTrigger {

    @Override
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, boolean isRotation) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player)entity;
        item.showProgram("close");

        player.getRoomUser().setWalkingAllowed(false);
        player.getRoomUser().getTimerManager().resetRoomTimer(120); // Only allow 120 seconds when diving, to stop the queue from piling up if someone goes AFK.
        player.getRoomUser().setDiving(true);

        CurrencyDao.decreaseTickets(player.getDetails(), 1);

        player.send(new TICKET_BALANCE(player.getDetails().getTickets()));
        player.send(new JUMPINGPLACE_OK());
    }

    @Override
    public void onEntityLeave(Entity entity, RoomEntity roomEntity, Item item) {
        item.showProgram("open");
    }
}
