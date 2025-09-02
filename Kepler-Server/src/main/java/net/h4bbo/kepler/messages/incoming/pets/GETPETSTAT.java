package net.h4bbo.kepler.messages.incoming.pets;

import net.h4bbo.kepler.game.pets.Pet;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.outgoing.pets.PETSTAT;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class GETPETSTAT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();
        String[] petData = reader.readString().split(Character.toString((char)4));

        int petId = Integer.parseInt(petData[0]);
        String petName = petData[1];

        Pet pet = (Pet) room.getEntityManager().getByInstanceId(petId);

        if (pet == null) {
            return;
        }

        player.send(new PETSTAT(pet));
    }
}
