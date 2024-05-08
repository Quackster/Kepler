package org.alexdev.kepler.game.item.interactors.types;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.pets.Pet;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.triggers.GenericTrigger;

public class PetFoodInteractor extends GenericTrigger {
    @Override
    public void onItemPlaced(Player player, Room room, Item item) {
        if (item.getCustomData().isBlank()) {
            item.setCustomData("0");
            item.updateStatus();
            item.save();
        }
    }

    @Override
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, boolean isRotation) {
        if (entity.getType() != EntityType.PET) {
            return;
        }

        var pet = (Pet) entity;
        var front = item.getPosition().getSquareInFront();

        pet.getRoomUser().look(front, true);
        pet.getRoomUser().getTask().startEating();
    }

    public void onItemPickup(Player player, Room room, Item item) {
        cancelPetsEating(room, item.getPosition());
    }

    public void onItemMoved(Player player, Room room, Item item, boolean isRotation, Position oldPosition, Item itemBelow, Item itemAbove) {
        cancelPetsEating(room, oldPosition);
    }

    private void cancelPetsEating(Room room, Position position) {
        room.getMapping().getTile(position).getEntities().forEach(x -> {
            if (x.getType() == EntityType.PET) {
                ((Pet)x).getRoomUser().getTask().eatingComplete(false);
            }
        });
    }

    /*
    @Override
    public void onEntityLeave(Entity entity, RoomEntity roomEntity, Item item) {
        if (entity.getType() != EntityType.PET) {
            return;
        }

        var pet = (Pet) entity;
        pet.getRoomUser().getTask().eatingComplete();
    }

     */
}
