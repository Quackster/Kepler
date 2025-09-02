package net.h4bbo.kepler.game.item.interactors.types;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.pets.Pet;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.entities.RoomEntity;
import net.h4bbo.kepler.game.triggers.GenericTrigger;

public class PetToyInteractor extends GenericTrigger {
    @Override
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, boolean isRotation) {
        if (entity.getType() != EntityType.PET) {
            return;
        }

        var pet = (Pet) entity;
        var front = item.getPosition().getSquareInFront();

        pet.getRoomUser().look(front, true);
        pet.getRoomUser().getTask().startPlay();
    }

    public void onItemPickup(Player player, Room room, Item item) {
        cancelPetsPlaying(room, item.getPosition());
    }

    public void onItemMoved(Player player, Room room, Item item, boolean isRotation, Position oldPosition, Item itemBelow, Item itemAbove) {
        cancelPetsPlaying(room, oldPosition);
    }

    private void cancelPetsPlaying(Room room, Position position) {
        room.getMapping().getTile(position).getEntities().forEach(x -> {
            if (x.getType() == EntityType.PET) {
                ((Pet)x).getRoomUser().getTask().playingComplete(false);
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
