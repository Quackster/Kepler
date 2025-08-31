package org.alexdev.kepler.messages.incoming.rooms.teleporter;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.item.interactors.types.TeleportInteractor;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GETDOORFLAT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        int itemId = Integer.parseInt(reader.contents());
        Item item = room.getItemManager().getById(itemId);

        if (item == null || !item.hasBehaviour(ItemBehaviour.TELEPORTER)) {
            return;
        }

        var interaction = new TeleportInteractor();
        interaction.onInteract(player, room, item, 0);

        /*Item linkedTeleporter = ItemDao.getItem(item.getTeleporterId());

        if (linkedTeleporter == null) {
            return;
        }

        if (!player.getRoomUser().getPosition().equals(item.getPosition())
                && !player.getRoomUser().getGoal().equals(item.getPosition())) {
            return;
        }

        player.getRoomUser().getGoal().setX(-1);
        player.getRoomUser().getGoal().setY(-1);

        // Kick out user from teleporter if link is broken
        if (RoomManager.getInstance().getRoomById(item.getRoomId()) == null ||
            RoomManager.getInstance().getRoomById(linkedTeleporter.getRoomId()) == null) {
            return;
        }

        player.getRoomUser().setWalkingAllowed(false);
        player.getRoomUser().setAuthenticateTelporterId(itemId);

        if (linkedTeleporter.getRoomId() == room.getId()) {
            room.send(new BROADCAST_TELEPORTER(item, player.getDetails().getName(), true));

            // Initial warp to the next teleporter
            GameScheduler.getInstance().getService().schedule(() -> {
                if (player.getRoomUser().getAuthenticateTelporterId() == -1) {
                    return;
                }

                player.getRoomUser().warp(linkedTeleporter.getPosition().copy(), false, false);
                room.send(new BROADCAST_TELEPORTER(linkedTeleporter, player.getDetails().getName(), false));
            }, 1000, TimeUnit.MILLISECONDS);

            // Walk out of the teleporter
            GameScheduler.getInstance().getService().schedule(() -> {
                if (player.getRoomUser().getAuthenticateTelporterId() == -1) {
                    return;
                }

                linkedTeleporter.setCustomData("TRUE");
                linkedTeleporter.updateStatus();

                player.getRoomUser().walkTo(linkedTeleporter.getPosition().getSquareInFront().getX(), linkedTeleporter.getPosition().getSquareInFront().getY());
            }, 2000, TimeUnit.MILLISECONDS);

            // Finally let user walk
            GameScheduler.getInstance().getService().schedule(() -> {
                if (player.getRoomUser().getAuthenticateTelporterId() == -1) {
                    return;
                }

                player.getRoomUser().setWalkingAllowed(true);
                player.getRoomUser().setAuthenticateTelporterId(-1);
            }, 2500, TimeUnit.MILLISECONDS);

        } else {
            player.send(new TELEPORTER_INIT(linkedTeleporter.getId(), linkedTeleporter.getRoomId()));
        }*/
    }
}