package net.h4bbo.kepler.messages.incoming.songs;

import net.h4bbo.kepler.dao.mysql.CurrencyDao;
import net.h4bbo.kepler.dao.mysql.ItemDao;
import net.h4bbo.kepler.dao.mysql.JukeboxDao;
import net.h4bbo.kepler.dao.mysql.SongMachineDao;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.item.ItemManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.song.Song;
import net.h4bbo.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.util.Calendar;

public class BURN_SONG implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        // if (player.getVersion()  <= 14) {
        //     return;
        // }

        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (room.getItemManager().getSoundMachine() == null) {
            return;
        }

        if (!room.hasRights(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        if (player.getDetails().getCredits() <= 0) {
            return;
        }

        int songId = reader.readInt();

        Song song = SongMachineDao.getSong(songId);

        if (song == null) {
            return;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        Item item = new Item();
        item.setOwnerId(player.getDetails().getId());
        item.setDefinitionId(ItemManager.getInstance().getDefinitionBySprite("song_disk").getId());
        item.setCustomData(player.getDetails().getName() + (char)10 +
                cal.get(Calendar.DAY_OF_MONTH) + (char)10 +
                cal.get(Calendar.MONTH) + (char)10 +
                cal.get(Calendar.YEAR) + (char)10 +
                song.getLength() + (char)10 +
                song.getTitle());

        ItemDao.newItem(item);

        player.getInventory().addItem(item);
        player.getInventory().getView("new");

        JukeboxDao.addDisk(item.getId(), 0, songId);
        JukeboxDao.setBurned(songId, true);

        CurrencyDao.decreaseCredits(player.getDetails(), 1);
        player.send(new CREDIT_BALANCE(player.getDetails().getCredits()));
    }
}
