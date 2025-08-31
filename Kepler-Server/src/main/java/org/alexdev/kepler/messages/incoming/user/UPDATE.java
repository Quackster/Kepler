package org.alexdev.kepler.messages.incoming.user;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class UPDATE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (!player.isLoggedIn()) {
            return;
        }

        var registerValues = PlayerManager.getInstance().getRegisterValues();

        while (reader.remainingBytes().length > 0) {
            var valueId = reader.readBase64();

            if (!registerValues.containsKey(valueId)) {
                return;
            }

            var value = registerValues.get(valueId);

            switch (value.getDataType()) {
                case STRING:
                {
                    value.setValue(reader.readString());
                    break;
                }
                case BOOLEAN:
                {
                    value.setFlag(reader.readBytes(1)[0] == 'A');
                    break;
                }
            }
        }

        Object directMail = PlayerManager.getInstance().getRegisterValue(registerValues, "directMail");
        if (directMail != null) {
            player.getDetails().setReceiveNews((boolean) directMail);
            PlayerDao.saveReceiveMail(player.getDetails());
        }

        Object motto = PlayerManager.getInstance().getRegisterValue(registerValues, "customData");
        if (motto != null) {
            player.getDetails().setMotto((String) motto);
        }

        Object figure = PlayerManager.getInstance().getRegisterValue(registerValues, "figure");
        if (figure != null) {
            player.getDetails().setFigure((String) figure);
        }

        Object sex = PlayerManager.getInstance().getRegisterValue(registerValues, "sex");
        if (sex != null) {
            player.getDetails().setSex((String) sex);
        }

        PlayerDao.saveDetails(
                player.getDetails().getId(),
                player.getDetails().getFigure(),
                player.getDetails().getPoolFigure(),
                player.getDetails().getSex());

        //PlayerDao.saveDetails(player.getDetails());

        PlayerDao.saveMotto(player.getDetails().getId(), player.getDetails().getMotto());

        new GET_INFO().handle(player, null);

        if (player.getRoomUser().getRoom() != null) {
            player.getRoomUser().refreshAppearance();
        }
    }
}
