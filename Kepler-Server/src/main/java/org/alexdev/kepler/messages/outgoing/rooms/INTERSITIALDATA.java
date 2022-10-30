
package org.alexdev.kepler.messages.outgoing.rooms;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;
public class INTERSITIALDATA extends MessageComposer {
    private final String image;
    private final String url;

    public INTERSITIALDATA(String image, String url) {
        this.image = image;
        this.url = url;
    }

    @Override
    public void compose(NettyResponse response) {
        if(this.image != null && this.url != null) {
            response.writeString(this.image+"\t"+this.url+"\r");
        } else {
            response.writeInt(0);
        }
    }

    @Override
    public short getHeader() {
        return 258;
    }
}