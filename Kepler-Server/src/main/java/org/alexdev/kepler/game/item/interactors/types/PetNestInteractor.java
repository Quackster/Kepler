package org.alexdev.kepler.game.item.interactors.types;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.dao.mysql.PetDao;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.pets.Pet;
import org.alexdev.kepler.game.pets.PetDetails;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.triggers.GenericTrigger;

public class PetNestInteractor extends GenericTrigger {
    @Override
    public void onItemPlaced(Player player, Room room, Item item) {
        PetDetails petDetails = PetDao.getPetDetails(item.getId());

        if (petDetails != null) {
            Pet pet = this.addPet(room, petDetails, item.getPosition());

            PetDao.saveCoordinates(petDetails.getId(),
                    pet.getRoomUser().getPosition().getX(),
                    pet.getRoomUser().getPosition().getY(),
                    pet.getRoomUser().getPosition().getRotation());
        }
    }

    @Override
    public void onItemPickup(Player player, Room room, Item item) {
        PetDetails petDetails = PetDao.getPetDetails(item.getId());

        if (petDetails == null) {
            return;
        }

        int petId = petDetails.getId();
        Pet pet = (Pet)room.getEntityManager().getById(petId, EntityType.PET);

        if (pet == null) {
            return;
        }

        room.getEntityManager().leaveRoom(pet, false);
    }

    /**
     * Add a pet by given pet id.
     *
     * @param room the room to add the pet to
     * @param petDetails the details of the pet
     * @param position the position of the pet
     *
     * @return the pet instance created
     */
    public Pet addPet(Room room, PetDetails petDetails, Position position) {
        Pet pet = new Pet(petDetails);
        position.setZ(room.getMapping().getTile(position.getX(), position.getY()).getWalkingHeight());

        room.getEntityManager().enterRoom(pet, position);
        room.getMapping().getTile(position).addEntity(pet);

        pet.getRoomUser().createTask(room);

        /*GameScheduler.getInstance().getService().scheduleAtFixedRate(()-> {
            pet.getRoomUser().walkTo(room.getModel().getRandomBound(0), room.getModel().getRandomBound(0));
        }, 0, 5, TimeUnit.SECONDS);*/

        return pet;
    }
}
