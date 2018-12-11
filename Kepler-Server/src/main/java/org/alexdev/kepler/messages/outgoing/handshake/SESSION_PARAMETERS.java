package org.alexdev.kepler.messages.outgoing.handshake;

import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.HashMap;
import java.util.Map;

public class SESSION_PARAMETERS extends MessageComposer {
    public enum SessionParamType {
        // conf_coppa is enabled when value higher than 0,
        // conf_strong_coppa_required is enabled when value is higher than 1
        REGISTER_COPPA(0),

        // conf_voucher. Determines if vouchers are enabled in the client (in-game)
        VOUCHER_ENABLED(1),

        // conf_parent_email_request. I think this is to switch parent email on/off
        REGISTER_REQUIRE_PARENT_EMAIL(2),

        // conf_parent_email_request_reregistration. ???
        REGISTER_SEND_PARENT_EMAIL(3),

        // conf_allow_direct_mail. ???
        ALLOW_DIRECT_MAIL(4),

        // Configures date formatting. Value is date string.
        DATE_FORMAT(5),

        // conf_partner_integration. Value is either 1 or 0 (enabled or disabled)
        PARTNER_INTEGRATION_ENABLED(6),

        // allow_profile_editing. Enables the client (in-game) profile editor
        ALLOW_PROFILE_EDITING(7),

        // tracking_header. Value is unknown
        TRACKING_HEADER(8),

        // tutorial_enabled. Value is either 1 or 0 (enabled or disabled)
        TUTORIAL_ENABLED(9);

        private final int paramID;

        SessionParamType(int paramID) {
            this.paramID = paramID;
        }

        public int getParamID() {
            return this.paramID;
        }
    }

    private PlayerDetails details;

    public SESSION_PARAMETERS(PlayerDetails details) {
        this.details = details;
    }

    @Override
    public void compose(NettyResponse response) {
        Map<SessionParamType, String> parameters = new HashMap<>();

        parameters.put(SessionParamType.VOUCHER_ENABLED, GameConfiguration.getInstance().getBoolean("vouchers.enabled") ? "1" : "0"); // conf_voucher. Determines if vouchers are enabled in the client (in-game)
        parameters.put(SessionParamType.REGISTER_REQUIRE_PARENT_EMAIL, "0"); // conf_parent_email_request. I think this is to switch parent email on/off
        parameters.put(SessionParamType.REGISTER_SEND_PARENT_EMAIL, "0"); // conf_parent_email_request_reregistration. ???
        parameters.put(SessionParamType.ALLOW_DIRECT_MAIL, "0"); // conf_allow_direct_mail. ???
        parameters.put(SessionParamType.DATE_FORMAT, ""); // Configures date formatting. Value is date string.
        parameters.put(SessionParamType.PARTNER_INTEGRATION_ENABLED, "0");  // conf_partner_integration. Value is either 1 or 0 (enabled or disabled)
        parameters.put(SessionParamType.ALLOW_PROFILE_EDITING, GameConfiguration.getInstance().getBoolean("profile.editing") ? "1" : "0"); // allow_profile_editing. Enables the client (in-game) profile editor
        parameters.put(SessionParamType.TRACKING_HEADER, ""); // tracking_header. Value is unknown
        parameters.put(SessionParamType.TUTORIAL_ENABLED, this.details.isTutorialFinished() ? "0" : "1"); // tutorial_enabled. Value is either 1 or 0 (enabled or disabled)

        response.writeInt(parameters.size());

        for (Map.Entry<SessionParamType, String> entry : parameters.entrySet()) {
            SessionParamType key = entry.getKey();
            String value = entry.getValue();

            response.writeInt(key.getParamID());

            if (value.length() > 0 && Character.isDigit(value.charAt(0))) {
                response.writeInt(Integer.parseInt(value));
            } else {
                response.writeString(value);
            }
        }
    }

    @Override
    public short getHeader() {
        return 257;
    }
}
