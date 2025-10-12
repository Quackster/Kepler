package org.alexdev.kepler.messages.incoming.infostand;

import org.alexdev.kepler.dao.mysql.InfoStandDao;
import org.alexdev.kepler.game.infostand.InfoStandProp;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.infostand.INFO_STAND_UPDATE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import static org.alexdev.kepler.game.infostand.InfoStand.DEFAULT_ACTION;
import static org.alexdev.kepler.game.infostand.InfoStand.DEFAULT_DIRECTION;
import static org.alexdev.kepler.game.infostand.InfoStand.DEFAULT_EXPRESSION;
import static org.alexdev.kepler.game.infostand.InfoStand.DEFAULT_FURNI;
import static org.alexdev.kepler.game.infostand.InfoStand.DEFAULT_PLATE;

public class UPDATE_INFO_STAND implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        final String expression = reader.readString();
        final String action = reader.readString().replace("/", "");
        final int direction = reader.readInt();
        final int furni = reader.readInt();
        final int plate = reader.readInt();

        if (!expression.equals(DEFAULT_EXPRESSION) && !player.getInfoStand().ownsProp(InfoStandProp.EXPRESSION, expression)) {
            return;
        }

        if (!action.equals(DEFAULT_ACTION) && !action.equals("sit") && !player.getInfoStand().ownsProp(InfoStandProp.ACTION, action)) {
            return;
        }

        if (direction != DEFAULT_DIRECTION && !player.getInfoStand().ownsProp(InfoStandProp.DIRECTION, String.valueOf(direction))) {
            return;
        }

        if (furni != DEFAULT_FURNI && !player.getInfoStand().ownsProp(InfoStandProp.FURNI, String.valueOf(furni))) {
            return;
        }

        if (plate != DEFAULT_PLATE && !player.getInfoStand().ownsProp(InfoStandProp.PLATE, String.valueOf(plate))) {
            return;
        }

        InfoStandDao.setUserActive(player.getInfoStand(), plate, furni, expression, action, direction);

        final Room room = player.getRoomUser().getRoom();
        if (room == null) {
            return;
        }

        room.send(new INFO_STAND_UPDATE(player.getRoomUser().getInstanceId(), player.getInfoStand().getActive()));
    }
}
