package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.items.PLACE_FLOORITEM;
import org.alexdev.kepler.messages.outgoing.rooms.items.SLIDEOBJECTBUNDLE;

import java.util.concurrent.ThreadLocalRandom;

public class UfosCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.ADMINISTRATOR_ACCESS);
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (!room.isOwner(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        int ufoAmount = ThreadLocalRandom.current().nextInt(0, 45) + 50;
        String textToSpeech = "Help, unknown flying objects! The aliens! There's a swarm of " + ufoAmount + " ufos coming this way! UFOS! Help! The hotel is attacked! Zap zap zap... Houston, we have a problem! Aliens! soi soi soi soi soi. The aliens are coming! We didn't listen! The end of the world! Aargh! Help, Aliens everywhere! I see ufos! I dream about cheese! I mean, beep beep beep! Meep meep meep! Code Red! Code Red! Area 51! Marihuana! Cape Canaveral! Aaron is a fag! Ufos! " + ufoAmount + " of them! I see them everywhere! Oh and I see dead people! UFOS! UFOs from Mars! Or from the Moon! Fuck knows! Whatever! Oh my god! They look like fucking weirdos! Space monsters! They look even worse than Rick Astley! UFOs! It's the end of the world! Ufos! Ufos! Ufos!";

        TalkCommand.createVoiceSpeakMessage(room, textToSpeech);

        int incremental = 0;

        for (int i = 0; i < ufoAmount; i++)  {
            //final int tempId = i;

           // GameScheduler.getInstance().getSchedulerService().scheduleAtFixedRate(() -> {
                int ufoId = Integer.MAX_VALUE - (i + 1);

                Item pItem = new Item();
                pItem.setId(ufoId);
                pItem.setPosition(new Position(
                        ThreadLocalRandom.current().nextInt(0, 45),
                        ThreadLocalRandom.current().nextInt(0, 45),
                        ThreadLocalRandom.current().nextInt(-3, 10)));


                pItem.getDefinition().setSprite("nest");
                pItem.getDefinition().setLength(1);
                pItem.getDefinition().setWidth(1);
                room.send(new PLACE_FLOORITEM(pItem));

                int destX = ThreadLocalRandom.current().nextInt(-(20 + (pItem.getPosition().getX() * 2)), 20 + (pItem.getPosition().getY() * 2));
                int destY = ThreadLocalRandom.current().nextInt(-(20 + (pItem.getPosition().getY() * 2)), 20 + (pItem.getPosition().getX() * 2));// + ThreadLocalRandom.current().nextInt(-10, -20);
                float destZ = ThreadLocalRandom.current().nextInt(-9, 10);

                room.send(new SLIDEOBJECTBUNDLE(pItem.getPosition(), destX, destY, destZ, pItem.getId()));
            //}, 0, incremental += 10, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public String getDescription() {
        return "UFO's :o";
    }
}
