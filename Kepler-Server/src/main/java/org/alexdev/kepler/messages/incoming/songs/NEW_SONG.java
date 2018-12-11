package org.alexdev.kepler.messages.incoming.songs;

import org.alexdev.kepler.dao.mysql.SongMachineDao;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.songs.USER_SOUND_PACKAGES;
import org.alexdev.kepler.messages.outgoing.songs.SOUND_PACKAGES;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class NEW_SONG implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (!room.isOwner(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        if (room.getItemManager().getSoundMachine() == null) {
            return;
        }

        // We don't want a user to get kicked when making cool beats
        player.getRoomUser().getTimerManager().resetRoomTimer();

        player.send(new SOUND_PACKAGES(SongMachineDao.getTracks(room.getItemManager().getSoundMachine().getId())));
        player.send(new USER_SOUND_PACKAGES(player.getInventory().getSoundsets()));
    }
}
