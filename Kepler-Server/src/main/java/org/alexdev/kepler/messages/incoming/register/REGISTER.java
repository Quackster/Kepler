package org.alexdev.kepler.messages.incoming.register;

import com.goterl.lazycode.lazysodium.interfaces.PwHash;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.NettyPlayerNetwork;
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


        reader.readBase64();
        String username = reader.readString();

        reader.readBase64();
        String figure = reader.readString();

        reader.readBase64();
        String gender = reader.readString();

        reader.readBase64();
        reader.readBase64();

        reader.readBase64();
        String email = reader.readString();

        reader.readBase64();
        String birthday = reader.readString();

        reader.readBytes(11);
        String password = reader.readString();

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

        PlayerDao.register(username, createPassword(password), figure, gender, NettyPlayerNetwork.getIpAddress(player.getNetwork().getChannel()));
        //System.out.println(name + " / " + figure + " / " + gender + " / " + email + " / " + birthday + " / " + password);
    }

    public static String createPassword(String password) throws Exception {
        byte[] pw = password.getBytes();
        byte[] outputHash = new byte[PwHash.STR_BYTES];
        PwHash.Native pwHash = (PwHash.Native) PlayerDao.LIB_SODIUM;
        boolean success = pwHash.cryptoPwHashStr(
                outputHash,
                pw,
                pw.length,
                PwHash.OPSLIMIT_INTERACTIVE,
                PwHash.MEMLIMIT_INTERACTIVE
        );

        if (!success) {
            throw new Exception("Password creation was a failure!");
        }

        return new String(outputHash).replace((char)0 + "", "");
    }


}
