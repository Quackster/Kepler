package org.alexdev.kepler.messages.outgoing.rooms.user;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityState;
import org.alexdev.kepler.game.infostand.InfoStand;
import org.alexdev.kepler.game.infostand.InfoStandActive;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;
import org.alexdev.kepler.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class USER_OBJECTS extends MessageComposer {
    private List<EntityState> states;

    public USER_OBJECTS(ConcurrentLinkedQueue<Entity> entities) {
        createEntityStates(new ArrayList<>(entities));
    }

    public USER_OBJECTS(List<Entity> users) {
        createEntityStates(users);
    }

    public USER_OBJECTS(Entity entity) {
        createEntityStates(List.of(entity));
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
            response.writeInt(states.getEntityId());
            response.writeString(states.getDetails().getName());
            response.writeString(states.getDetails().getFigure());
            response.writeString(states.getDetails().getSex());
            response.writeString("");
            response.writeInt(states.getPosition().getX());
            response.writeInt(states.getPosition().getY());
            response.writeString(Double.toString(StringUtil.format(states.getPosition().getZ())));

            if ((states.getRoom().getModel().getName().startsWith("pool_") || states.getRoom().getModel().getName().equals("md_a")) &&
                    !states.getDetails().getPoolFigure().isEmpty()) {
                response.writeString(states.getDetails().getPoolFigure());
            } else {
                response.writeString("");
            }

            response.writeString(states.getDetails().getCurrentBadge());

            switch (states.getEntityType()) {
                case PLAYER: {
                    response.writeInt(1);

                    final InfoStandActive infoStand = states.getActiveInfoStand();
                    if (infoStand != null) {
                        response.writeString(infoStand.getExpression());
                        response.writeString(infoStand.getAction());
                        response.writeInt(infoStand.getDirection());
                        response.writeInt(infoStand.getFurni());
                        response.writeInt(infoStand.getPlate());
                    } else {
                        response.writeString(InfoStand.DEFAULT_EXPRESSION);
                        response.writeString(InfoStand.DEFAULT_ACTION);
                        response.writeInt(InfoStand.DEFAULT_DIRECTION);
                        response.writeInt(InfoStand.DEFAULT_FURNI);
                        response.writeInt(InfoStand.DEFAULT_PLATE);
                    }
                    break;
                }
                case PET: {
                    response.writeInt(2);
                    break;
                }
                case BOT: {
                    response.writeInt(3);
                    response.writeInt(0); // TODO: interactionID
                }
            }
        }
    }

    @Override
    public short getHeader() {
        return 28; // "@\"
    }
}
