package org.alexdev.kepler.messages.incoming.rooms.pool;

import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.messages.outgoing.rooms.items.SHOWPROGRAM;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;

import java.util.concurrent.TimeUnit;

public class SPLASH_POSITION implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        if (!player.getRoomUser().isDiving()) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (!room.getModel().getName().equals("pool_b")) {
            return;
        }

        Item currentItem = player.getRoomUser().getCurrentItem();

        if (!currentItem.getDefinition().getSprite().equals("poolLift")) {
            return;
        }

        Position destination = new Position(23, 19, 0);
        String contents = destination.getX() + "," + destination.getY();

        player.getRoomUser().setStatus(StatusType.SWIM, "");
        player.getRoomUser().warp(destination, true, false);

        room.send(new SHOWPROGRAM(new String[] { "BIGSPLASH", "POSITION", contents,}));

        player.getRoomUser().setDiving(false);
        player.getRoomUser().walkTo(20, 19);
        player.getRoomUser().setEnableWalkingOnStop(true);

        currentItem.showProgram("open");

        GameScheduler.getInstance().getService().schedule(() -> {
            int total = 0;
            int sum = 0;
            double finalScore = 0;

            for (Player p : room.getEntityManager().getPlayers()) {
                if (p.getDetails().getId() == player.getDetails().getId()) {
                    continue;
                }

                if (p.getRoomUser().getLidoVote() > 0) {
                    sum += p.getRoomUser().getLidoVote();
                    total++;
                }
            }

            room.send(new SHOWPROGRAM(new String[]{"cam1", "targetcamera", String.valueOf(player.getRoomUser().getInstanceId())}));

            if (total > 0) {
                finalScore = StringUtil.format((double) sum / total);
            }

            room.send(new SHOWPROGRAM(new String[]{"cam1", "showtext", (player.getDetails().getName() + "'s\n score: " + finalScore)}));

            for (Player p : room.getEntityManager().getPlayers()) {
                p.getRoomUser().setLidoVote(0);
            }
        }, 1, TimeUnit.SECONDS);
    }
}