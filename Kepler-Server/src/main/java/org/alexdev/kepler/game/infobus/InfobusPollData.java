package org.alexdev.kepler.game.infobus;

import java.util.ArrayList;
import java.util.List;

public class InfobusPollData {
    private String question;
    private List<String> answers;

    public InfobusPollData(String question) {
        this.question = question;
        this.answers = new ArrayList<>();
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getAnswers() {
        return answers;
    }
}
