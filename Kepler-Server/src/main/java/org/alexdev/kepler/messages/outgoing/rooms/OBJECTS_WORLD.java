package org.alexdev.kepler.messages.outgoing.rooms;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;
import java.util.UUID;

public class OBJECTS_WORLD extends MessageComposer {
    private List<Item> items;
    private String[] data;

    public OBJECTS_WORLD(List<Item> items) {
        this.items = items;
    }

    public OBJECTS_WORLD(String old) {
        this.data = old.split("\r");
    }

    @Override
    public void compose(NettyResponse response) {
        if (items != null) {
            response.writeInt(this.items.size());

            for (Item item : this.items) {
                item.serialise(response);
            }
        }
        else {
            response.writeInt(this.data.length);

            for (String entry : data) {
                response.writeDelimeter(UUID.randomUUID().toString().split("-")[0], ' ');
                response.writeString(entry.split(" ")[1]);
                response.writeDelimeter(Integer.parseInt(entry.split(" ")[2]), ' ');
                response.writeDelimeter(Integer.parseInt(entry.split(" ")[3]), ' ');
                response.writeDelimeter(Integer.parseInt(entry.split(" ")[4]), ' ');
                response.write(Integer.parseInt(entry.split(" ")[5]));
                response.write(Character.toString((char) 13));
            }

            /*response.writeInt(this.data.length);

            for (String entry : data) {
                response.writeBool(false);
                response.writeString(UUID.randomUUID().toString().split("-")[0]);
                response.writeString(entry.split(" ")[1]);

                response.writeInt(Integer.parseInt(entry.split(" ")[2]));
                response.writeInt(Integer.parseInt(entry.split(" ")[3]));
                response.writeInt(Integer.parseInt(entry.split(" ")[4]));
                response.writeInt(Integer.parseInt(entry.split(" ")[5]));
            }*/
        }
        /*            response.writeInt(hasDimensions ? 1 : 0);
            response.writeString(this.customData);
            response.writeString(definition.getSprite());
            response.writeInt(this.position.getX());
            response.writeInt(this.position.getY());
            response.writeInt((int) this.position.getZ());

            if (!hasDimensions) {
                response.writeInt(this.position.getRotation());*/
    }

    @Override
    public short getHeader() {
        return 30; // "@^"
    }
}
