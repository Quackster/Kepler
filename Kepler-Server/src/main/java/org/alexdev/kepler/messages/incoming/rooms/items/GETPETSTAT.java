package org.alexdev.kepler.messages.incoming.rooms.items;

import org.alexdev.kepler.dao.mysql.PetDao;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.pets.Pet;
import org.alexdev.kepler.game.pets.PetDetails;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.messages.outgoing.rooms.items.DICE_VALUE;
import org.alexdev.kepler.messages.outgoing.rooms.items.HANDLE_PETSTAT;
import org.alexdev.kepler.messages.outgoing.rooms.items.STUFFDATAUPDATE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.apache.commons.lang3.StringUtils;

import static java.lang.Integer.parseInt;


public class GETPETSTAT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        int entityId = parseInt(reader.readString().split(Character.toString((char)4))[0]);
        System.out.println("EntityID: : " + entityId);
        Entity entity = player.getRoomUser().getRoom().getEntityManager().getByInstanceId(entityId);
        System.out.println("PETID: " + entity.getDetails().getId());

        PetDetails petDetails = PetDao.getPetDetailsById(entity.getDetails().getId());
        if (petDetails != null) {
            Pet pet = new Pet(petDetails);
            player.send(new HANDLE_PETSTAT(entityId, pet));
        }
    }
}