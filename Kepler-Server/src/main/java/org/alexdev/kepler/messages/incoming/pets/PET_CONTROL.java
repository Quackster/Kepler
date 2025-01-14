package org.alexdev.kepler.messages.incoming.pets;

import org.alexdev.kepler.game.pets.Pet;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.pets.HANDLE_PET_CONTROL;
import org.alexdev.kepler.messages.outgoing.pets.PETSTAT;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class PET_CONTROL implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();
        String data = reader.readString();

        int petId = Integer.parseInt(data);

        Pet pet = (Pet) room.getEntityManager().getByInstanceId(petId);

        if (pet == null) {
            return;
        }

        player.send(new HANDLE_PET_CONTROL(pet));
    }
}
