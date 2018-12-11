package org.alexdev.roseau.game.item.interactors.furniture;

import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.interactors.Interaction;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.model.Position;

public class ChairInteractor extends Interaction {

    public ChairInteractor(Item item) {
        super(item);
    }

    @Override
    public void onTrigger(Player player) {	}

    @Override
    public void onStoppedWalking(Player player) {
        player.getRoomUser().getPosition().setRotation(item.getPosition().getRotation());
        player.getRoomUser().removeStatus("dance");
        player.getRoomUser().removeStatus("lay");
        player.getRoomUser().setStatus("sit", " " + String.valueOf(player.getRoomUser().getPosition().getZ() + definition.getHeight()), true, -1);
    }

    public boolean hasValidEntry(Entity entity, Position neighbour) {

        Position front = this.item.getPosition().getSquareInFront();
        Position left = this.item.getPosition().getSquareLeft();
        Position right = this.item.getPosition().getSquareRight();

        long leftDistance = left.getDistance(entity.getRoomUser().getPosition());
        long rightDistance = right.getDistance(entity.getRoomUser().getPosition());

        if (entity.getRoomUser().getRoom().getMapping().isValidTile(entity, front.getX(), front.getY())) {
            if (!neighbour.isMatch(front)) {
                return false;
            }
        }

        if (leftDistance <= rightDistance) {

            if (entity.getRoomUser().getRoom().getMapping().isValidTile(entity, left.getX(), left.getY())) {
                if (!neighbour.isMatch(left)) {
                    return false;
                }
            }

        } else {
            if (entity.getRoomUser().getRoom().getMapping().isValidTile(entity, right.getX(), right.getY())) {
                if (!neighbour.isMatch(right)) {
                    return false;
                }
            }
        }


        return true;
    }

}
