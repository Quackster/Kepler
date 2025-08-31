package org.alexdev.kepler.messages.outgoing.infobus;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class POLL_QUESTION extends MessageComposer {
    private final String question;
    private final List<String> answers;

    public POLL_QUESTION(String question, List<String> answers) {
        this.question = question;
        this.answers = answers;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString(this.question);
        response.writeInt(this.answers.size());

        int index = 0;

        for (String answer : this.answers) {
            response.writeInt(index);
            response.writeString(answer);
            index++;
        }
    }

    @Override
    public short getHeader() {
        return 79;
    }
}
