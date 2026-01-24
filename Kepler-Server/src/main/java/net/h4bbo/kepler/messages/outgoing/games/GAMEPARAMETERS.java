package net.h4bbo.kepler.messages.outgoing.games;

import net.h4bbo.kepler.game.games.GameParameter;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class GAMEPARAMETERS extends MessageComposer {
    private final GameParameter[] parameters;

    public GAMEPARAMETERS(GameParameter[] gameParameters) {
        this.parameters = gameParameters;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.parameters.length);

        for (GameParameter parameter : this.parameters) {
            response.writeString(parameter.getName());
            response.writeBool(!parameter.hasMinMax());
            response.writeInt(parameter.isEditable() ? 2 : 0);

            if (parameter.hasMinMax()) {
                response.writeInt(Integer.parseInt(parameter.getDefaultValue()));

                if (parameter.getMin() != -1) {
                    response.writeBool(true);
                    response.writeInt(parameter.getMin());
                }

                if (parameter.getMax() != -1) {
                    response.writeBool(true);
                    response.writeInt(parameter.getMax());
                }
            } else {
                response.writeString(parameter.getDefaultValue());
                response.writeInt(0);
            }
        }
    }

    @Override
    public short getHeader() {
        return 235;
    }
}
