package net.h4bbo.http.kepler.controllers.habblet;

import io.netty.handler.codec.http.FullHttpResponse;
import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.http.kepler.util.RegisterUtil;

public class NameCheckController {
    public static void namecheck(WebConnection webConnection) {
        String errorMessage = "";

        String username = webConnection.post().getString("name");
        int errorCode = RegisterUtil.getNameErrorCode(username);

        if (errorCode == 6) {
            errorMessage = "This name is unacceptable to hotel management.";
        } else if (errorCode == 5) {
            errorMessage = "Your username is invalid or contains invalid characters.";
        } else if (errorCode == 4) {
            errorMessage = "This name is not allowed.";
        } else if (errorCode == 3) {
            errorMessage = "The name you have chosen is too long.";
        } else if (errorCode == 2) {
            errorMessage = "Please enter a username.";
        } else if (errorCode == 1) {
            errorMessage = "A user with this name already exists.";
        }

        FullHttpResponse httpResponse = ResponseBuilder.create("");
        httpResponse.headers().set("X-JSON", "{\"registration_name\":\"" + errorMessage + "\"}");
        webConnection.send(httpResponse);
    }

    public static boolean hasAllowedCharacters(String str, String allowedChars) {
        if (str == null) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            if (allowedChars.contains(Character.valueOf(str.toCharArray()[i]).toString())) {
                continue;
            }

            return false;
        }

        return true;
    }
}
