package org.alexdev.kepler.messages.outgoing.infobus;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;
import java.util.Map;

public class VOTE_RESULTS extends MessageComposer {
    private final String question;
    private final List<String> answers;
    private final Map<Integer, Integer> answerResults;
    private final int totalAnswers;

    public VOTE_RESULTS(String question, List<String> answers, Map<Integer, Integer> answerResults, int totalAnswers) {
        this.question = question;
        this.answers = answers;
        this.answerResults = answerResults;
        this.totalAnswers = totalAnswers;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString(this.question);
        response.writeInt(this.answers.size());

        int i = 0;
        for (String answer : this.answers) {
            response.writeInt(i);
            response.writeString(answer);
            response.writeInt(this.answerResults.getOrDefault(i, 0));
            i++;
        }

        response.writeInt(this.totalAnswers);
    }

    @Override
    public short getHeader() {
        return 80;
    }
}
