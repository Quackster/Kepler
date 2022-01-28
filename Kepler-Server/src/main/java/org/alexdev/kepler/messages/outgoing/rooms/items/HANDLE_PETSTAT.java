package org.alexdev.kepler.messages.outgoing.rooms.items;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.pets.Pet;
import org.alexdev.kepler.game.pets.PetDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class HANDLE_PETSTAT extends MessageComposer {

    private Pet petDetails;
    private int entityId;
    public HANDLE_PETSTAT(int entityId, Pet petDetails) {
        this.petDetails = petDetails;
        this.entityId = entityId;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.entityId);
        response.writeInt(petDetails.getAge());
        response.writeInt(petDetails.getHunger());
        response.writeInt(petDetails.getThirst());
        response.writeInt(petDetails.getHappiness());
        response.writeInt(petDetails.getEnergy());
        response.writeInt(petDetails.getFriendship());
        /*
        *                 Response.appendWired(pPet.ID);
                Response.appendWired(pPet.Information.Age);
                Response.appendWired((int)pPet.Information.Hunger);
                Response.appendWired((int)pPet.Information.Thirst);
                Response.appendWired((int)pPet.Information.Happiness);
                Response.appendWired((int)pPet.Information.Energy);
                Response.appendWired((int)pPet.Information.Friendship);
        *
        * */
        //response.writeInt(this.teleporterId);
        //response.writeInt(this.roomId);
    }

    @Override
    public short getHeader() {
        return 210; // "CR"
    }
}
