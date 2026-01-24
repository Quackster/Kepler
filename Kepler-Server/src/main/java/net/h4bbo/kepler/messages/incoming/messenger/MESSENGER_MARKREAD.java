package net.h4bbo.kepler.messages.incoming.messenger;

import net.h4bbo.kepler.dao.mysql.MessengerDao;
import net.h4bbo.kepler.game.messenger.Messenger;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.sql.SQLException;

public class MESSENGER_MARKREAD implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws SQLException {
        int messageId = reader.readInt();

        MessengerDao.markMessageRead(messageId);
        player.getMessenger().getOfflineMessages().remove(messageId);
    }
}
