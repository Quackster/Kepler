package org.alexdev.kepler.messages.outgoing.pets;

import org.alexdev.kepler.game.pets.Pet;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class HANDLE_PET_CONTROL extends MessageComposer {
    private final Pet pet;

    public HANDLE_PET_CONTROL(Pet pet) {
        this.pet = pet;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.pet.getRoomUser().getInstanceId());
    }

    @Override
    public short getHeader() {
        return 902;
    }
}
