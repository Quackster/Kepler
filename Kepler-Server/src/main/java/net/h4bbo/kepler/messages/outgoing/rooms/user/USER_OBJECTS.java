package net.h4bbo.kepler.messages.outgoing.rooms.user;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityState;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;
import net.h4bbo.kepler.util.FigureUtil;
import net.h4bbo.kepler.util.StringUtil;

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
            var entityState = new EntityState(
                    user.getDetails().getId(),
                    user.getRoomUser().getInstanceId(),
                    user.getDetails(),
                    user.getType(),
                    user.getRoomUser().getRoom(),
                    user.getRoomUser().getPosition().copy(),
                    user.getRoomUser().getStatuses());

            if (user instanceof Player player) {
                entityState.getBadges().addAll(player.getBadgeManager().getEquippedBadges());
            }

            this.states.add(entityState);
        }
    }

    @Override
    public void compose(NettyResponse response) {
        for (EntityState states : states) {
            response.write("\r");

            if (states.getEntityType() == EntityType.PET) {
                response.writeKeyValue("i", states.getInstanceId());
                response.writeKeyValue("n", states.getInstanceId() + Character.toString((char) 4) + states.getDetails().getName());
                response.writeKeyValue("f", states.getDetails().getFigure());
                response.writeKeyValue("l", states.getPosition().getX() + " " + states.getPosition().getY() + " " + (int) states.getPosition().getZ());
                response.writeKeyValue("c", "");
            } else {
                response.writeKeyValue("i", states.getInstanceId());
                response.writeKeyValue("a", states.getEntityId());
                response.writeKeyValue("n", states.getDetails().getName());
                response.writeKeyValue("f", states.getDetails().getFigure());
                response.writeKeyValue("s", states.getDetails().getSex());
                response.writeKeyValue("l", states.getPosition().getX() + " " + states.getPosition().getY() + " " + Double.toString(StringUtil.format(states.getPosition().getZ())));

                if (states.getDetails().getMotto().length() > 0) {
                    response.writeKeyValue("c", states.getDetails().getMotto());
                }

                /*
                if (states.getDetails().getShowBadge()) {
                    response.writeKeyValue("b", states.getDetails().getCurrentBadge());
                }
                */

                /*
                String szNotify = "";

                for (var badge : states.getBadges()) {
                    szNotify += badge.getSlotId();
                    szNotify += ":";
                    szNotify += badge.getBadgeCode();
                    szNotify += ",";
                }

                if (szNotify.length() > 0)
                    response.writeKeyValue("b", szNotify); // s += "b:" + szNotify + Convert.ToChar(13);

                if (states.getGroupMember() != null) {
                    response.writeKeyValue("g", states.getGroupMember().getGroupId());
                    response.writeKeyValue("t", states.getGroupMember().getMemberRank().getClientRank());
                }*/

                if (states.getRoom().getModel().getName().startsWith("pool_") ||
                        states.getRoom().getModel().getName().equals("md_a")) {

                    if (states.getDetails().getPoolFigure() != null && states.getDetails().getPoolFigure().length() > 0) {
                        response.writeKeyValue("p", states.getDetails().getPoolFigure());
                    }
                }

                if (states.getEntityType() == EntityType.BOT) {
                    response.writeDelimeter("[bot]", (char) 13);
                }
            }
        }
    }

    @Override
    public short getHeader() {
        return 28; // "@\"
    }
}