package org.alexdev.kepler.messages.incoming.register;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.player.register.RegisterDataType;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class REGISTER implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        /*   im_read_b64_int(message);
    char *name = im_read_str(message);

    im_read_b64_int(message);
    char *figure = im_read_str(message);

    im_read_b64_int(message);
    char *gender = im_read_str(message);

    im_read_b64_int(message);
    im_read_b64_int(message);

    // don't give a shit about emails
    im_read_b64_int(message);
    free(im_read_str(message));

    // couldn't give a shit about your birthday either
    im_read_b64_int(message);
    free(im_read_str(message));

    im_read(message, 11);
    char *password = im_read_str(message);*/

        /*
        pRegMsgStruct.setAt("parentagree", [#id:1, "type":#boolean])
        pRegMsgStruct.setAt("name", [#id:2, "type":#string])
        pRegMsgStruct.setAt("password", [#id:3, "type":#string])
        pRegMsgStruct.setAt("figure", [#id:4, "type":#string])
        pRegMsgStruct.setAt("sex", [#id:5, "type":#string])
        pRegMsgStruct.setAt("customData", [#id:6, "type":#string])
        pRegMsgStruct.setAt("email", [#id:7, "type":#string])
        pRegMsgStruct.setAt("birthday", [#id:8, "type":#string])
        pRegMsgStruct.setAt("directMail", [#id:9, "type":#boolean])
        pRegMsgStruct.setAt("has_read_agreement", [#id:10, "type":#boolean])
        pRegMsgStruct.setAt("isp_id", [#id:11, "type":#string])
        pRegMsgStruct.setAt("partnersite", [#id:12, "type":#string])
        pRegMsgStruct.setAt("oldpassword", [#id:13, "type":#string])
         */

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
        //System.out.println(name + " / " + figure + " / " + gender + " / " + email + " / " + birthday + " / " + password);
    }
}
