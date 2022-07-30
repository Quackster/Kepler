package org.alexdev.kepler.messages.incoming.user.settings;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.player.register.RegisterDataType;
import org.alexdev.kepler.game.player.register.RegisterValue;
import org.alexdev.kepler.messages.incoming.register.REGISTER;
import org.alexdev.kepler.messages.outgoing.user.settings.UPDATE_ACCOUNT_RESPONSE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;

import java.util.LinkedHashMap;

public class UPDATE_ACCOUNT implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        // BU@M@Iqwerty123@H@J06.07.1992@C@F123123
        // header - id - old password - birthday - new password
        var registerValues = new LinkedHashMap<Integer, RegisterValue>();
        registerValues.put(3, new RegisterValue("password", RegisterDataType.STRING));
        registerValues.put(13, new RegisterValue("oldpassword", RegisterDataType.STRING));
        registerValues.put(7, new RegisterValue("email", RegisterDataType.STRING));
        registerValues.put(8, new RegisterValue("birthday", RegisterDataType.STRING));

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

        String birthday = (String) PlayerManager.getInstance().getRegisterValue(registerValues, "birthday");
        String oldPassword = (String) PlayerManager.getInstance().getRegisterValue(registerValues, "oldpassword");
        String newPassword = (String) PlayerManager.getInstance().getRegisterValue(registerValues, "password");
        String email = (String) PlayerManager.getInstance().getRegisterValue(registerValues, "email");

        System.out.println(player.getDetails().getBirthday());

        if (!StringUtil.isNullOrEmpty(player.getDetails().getBirthday()) && !player.getDetails().getBirthday().equals(birthday)) {
            player.send(new UPDATE_ACCOUNT_RESPONSE(UPDATE_ACCOUNT_RESPONSE.ResponseType.INCORRECT_BIRTHDAY));
            return;
        }

        if (!PlayerDao.login(player.getDetails().getName(), oldPassword)) {
            player.send(new UPDATE_ACCOUNT_RESPONSE(UPDATE_ACCOUNT_RESPONSE.ResponseType.INCORRECT_PASSWORD));
            return;
        }

        if (newPassword != null) {
            PlayerDao.savePassword(player.getDetails().getId(), PlayerManager.getInstance().createPassword(newPassword.toString()));
            player.send(new UPDATE_ACCOUNT_RESPONSE(UPDATE_ACCOUNT_RESPONSE.ResponseType.SUCCESS));
        }

        if (email != null) {
            PlayerDao.saveEmail(player.getDetails().getId(), email);
            player.send(new UPDATE_ACCOUNT_RESPONSE(UPDATE_ACCOUNT_RESPONSE.ResponseType.SUCCESS));
        }

        if (StringUtil.isNullOrEmpty(player.getDetails().getBirthday())) {
            player.getDetails().setBirthday(birthday);
            PlayerDao.saveBirthday(player.getDetails().getId(), birthday);
        }

        /*if (StringUtil.isNullOrEmpty(PlayerManager.getInstance().getRegisterValue(registerValues, "email"))) {
            var oldPassword = PlayerManager.getInstance().getRegisterValue(registerValues, "password");
            var newPassword = PlayerManager.getInstance().getRegisterValue(registerValues, "oldpassword");
            var birthday = PlayerManager.getInstance().getRegisterValue(registerValues, "birthday");

            if (newPassword != null && oldPassword != null && PlayerDao.login(player.getDetails().getName(), oldPassword.toString())) {
                if (birthday != null && birthday.toString().length() > 0) {
                    PlayerDao.savePassword(player.getDetails().getId(), PlayerManager.getInstance().createPassword(newPassword.toString()));
                    PlayerDao.saveBirthday(player.getDetails().getId(), birthday.toString());

                    player.send(new UPDATE_ACCOUNT_RESPONSE(UPDATE_ACCOUNT_RESPONSE.ResponseType.SUCCESS));
                } else {
                    player.send(new UPDATE_ACCOUNT_RESPONSE(UPDATE_ACCOUNT_RESPONSE.ResponseType.INCORRECT_BIRTHDAY));
                }
            } else {
                player.send(new UPDATE_ACCOUNT_RESPONSE(UPDATE_ACCOUNT_RESPONSE.ResponseType.INCORRECT_PASSWORD));
            }

            // change birthday and password
        }*/
    }
}