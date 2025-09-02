package net.h4bbo.kepler.messages.incoming.rooms.user;

import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.item.base.ItemBehaviour;
import net.h4bbo.kepler.game.item.interactors.InteractionType;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.pathfinder.Rotation;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.enums.StatusType;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class LOOKTO implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Item item = player.getRoomUser().getCurrentItem();

        // Don't allow LOOKTO to be handled on wobble squabble tiles.
        if (item != null) {
            if (item.getDefinition().getInteractionType() == InteractionType.WS_JOIN_QUEUE ||
                    item.getDefinition().getInteractionType() == InteractionType.WS_QUEUE_TILE ||
                    item.getDefinition().getInteractionType() == InteractionType.WS_TILE_START) {
                return; // Don't process :sit command on furniture that the user is already on.
            }
        }

        //Room room = player.getRoomUser().getRoom();

        String[] data = reader.contents().split(" ");

        Position lookDirection = new Position(
                Integer.parseInt(data[0]),
                Integer.parseInt(data[1]));

        if (player.getRoomUser().containsStatus(StatusType.LAY)) {
            return;
        }

        if (player.getRoomUser().getPosition().equals(lookDirection)) {
            return;
        }

        if (player.getRoomUser().getPosition().equals(player.getRoomUser().getRoom().getModel().getDoorLocation())) {
            return;
        }

        if (player.getRoomUser().getRoom().isPublicRoom() && player.getRoomUser().getRoom().getModel().getDoorLocation().equals(player.getRoomUser().getPosition())) {
            return;
        }

        if (player.getRoomUser().getCurrentItem() != null) {
            if (player.getRoomUser().getCurrentItem().hasBehaviour(ItemBehaviour.TELEPORTER)) {
                return;
            }
        }

        int rotation = Rotation.calculateHumanDirection(
                player.getRoomUser().getPosition().getX(),
                player.getRoomUser().getPosition().getY(),
                lookDirection.getX(),
                lookDirection.getY());

        // When sitting calculate even rotation
        if (player.getRoomUser().containsStatus(StatusType.SIT)) {
            Position current = player.getRoomUser().getPosition();

            // If they're sitting on the ground also rotate body.
            if (player.getRoomUser().isSittingOnGround()) {
                rotation = rotation / 2 * 2;
                player.getRoomUser().getPosition().setBodyRotation(rotation);
            }

            // And now rotate their head for all sitting people.
            player.getRoomUser().getPosition().setHeadRotation(Rotation.getHeadRotation(current.getRotation(), current, lookDirection));

        } else {
            player.getRoomUser().getPosition().setRotation(rotation);
        }

        player.getRoomUser().setNeedsUpdate(true);

        player.getRoomUser().getTimerManager().beginLookTimer();
        player.getRoomUser().getTimerManager().resetRoomTimer();
    }
}
