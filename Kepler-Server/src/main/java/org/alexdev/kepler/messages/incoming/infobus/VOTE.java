package org.alexdev.kepler.messages.incoming.infobus;

import org.alexdev.kepler.dao.mysql.InfobusDao;
import org.alexdev.kepler.game.infobus.InfobusManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class VOTE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null || !player.getRoomUser().getRoom().getModel().getName().equals("park_b")) {
            return;
        }

        if (InfobusManager.getInstance().getCurrentPoll() == null) {
            return;
        }

        int choice = reader.readInt();
        var currentPoll = InfobusManager.getInstance().getCurrentPoll();

        if (choice <= 0 || choice > currentPoll.getPollData().getAnswers().size()) {
            return;
        }

        if (InfobusDao.hasAnswer(currentPoll.getId(), player.getDetails().getId())) {
            return;
        }

        InfobusDao.addAnswer(currentPoll.getId(), choice - 1, player.getDetails().getId());

        if (InfobusManager.getInstance().canUpdateResults()) {
            InfobusManager.getInstance().showPollResults(InfobusManager.getInstance().getCurrentPoll().getId());
        }
    }
}
