package org.alexdev.kepler.messages.incoming.register;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.register.REGISTRATION_OK;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class REGISTER implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
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

        String username = (String) PlayerManager.getInstance().getRegisterValue(registerValues, "name");
        String figure = (String) PlayerManager.getInstance().getRegisterValue(registerValues, "figure");
        String gender = (String) PlayerManager.getInstance().getRegisterValue(registerValues, "sex");
        String email = (String) PlayerManager.getInstance().getRegisterValue(registerValues, "email");
        String birthday = (String) PlayerManager.getInstance().getRegisterValue(registerValues, "birthday");
        String password = (String) PlayerManager.getInstance().getRegisterValue(registerValues, "password");

        if (username.equals(password)) {
            return;
        } else if (password.length() < 6) {
            return;
        } else if (password.length() > 10) {
            return;
        }

        if (APPROVENAME.getNameCheckCode(username) > 0) {
            return;
        }

        var hashedPassword = PlayerManager.getInstance().createPassword(password);

        if (hashedPassword == null)
            return;

        PlayerDao.register(username, hashedPassword, figure, gender, email, birthday);

        player.send(new REGISTRATION_OK(""));
    }
}
