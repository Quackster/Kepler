package org.alexdev.kepler.messages.outgoing.rooms.user;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityState;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;
import org.alexdev.kepler.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class USER_STATUSES extends MessageComposer {
    private List<EntityState> states;

    public USER_STATUSES(List<Entity> users) {
        createEntityStates(users);
    }

    private void createEntityStates(List<Entity> entities) {
        this.states = new ArrayList<>();

        for (Entity user : entities) {
            this.states.add(EntityState.createFromEntity(user));
        }
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.states.size());

        for (EntityState states : states) {
            response.writeInt(states.getInstanceId());
            response.writeInt(states.getPosition().getX());
            response.writeInt(states.getPosition().getY());
            response.writeString(Double.toString(StringUtil.format(states.getPosition().getZ())));
            response.writeInt(states.getPosition().getHeadRotation());
            response.writeInt(states.getPosition().getBodyRotation());

            final StringBuilder action = new StringBuilder();

            action.append("/");

            for (var status : states.getStatuses().values()) {
                action.append(status.getKey().getStatusCode());

                if (!status.getValue().isEmpty()) {
                    action.append(" ");
                    action.append(status.getValue());
                }

                action.append("/");
            }

            final String actionString = action.toString();

            response.writeString(actionString);
        }
    }

    @Override
    public short getHeader() {
        return 34; // "@b"
    }
}
