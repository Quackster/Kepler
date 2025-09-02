package net.h4bbo.kepler.messages.incoming.jukebox;

import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.song.jukebox.JukeboxManager;
import net.h4bbo.kepler.messages.outgoing.jukebox.JUKEBOX_DISCS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class GET_JUKEBOX_DISCS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (room.getItemManager().getSoundMachine() == null) {
            return;
        }

        if (!room.hasRights(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            //return;
        }

        room.send(new JUKEBOX_DISCS(JukeboxManager.getInstance().getDisks(room.getItemManager().getSoundMachine().getId())));
    }
}
