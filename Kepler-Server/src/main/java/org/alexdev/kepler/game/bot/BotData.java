package org.alexdev.kepler.game.bot;

import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class BotData {
    private String name;
    private String mission;
    private Position startPosition;
    private String figure;

    private List<Position> walkspace;
    private List<BotSpeech> speeches;
    private List<BotSpeech> responses;
    private List<BotSpeech> unrecognisedSpeech;
    private List<String> drinks;

    public BotData(String name, String mission, int x, int y, int headRotation, int bodyRotation, String figure, String walkspaceData,
                   String speech, String responses, String unrecognisedResponses, String drinks) {
        this.name = name;
        this.mission = mission;
        this.startPosition = new Position(x, y, 0, headRotation, bodyRotation);
        this.figure = figure;
        this.walkspace = new ArrayList<>();

        for (String positionDatas : walkspaceData.split(" ")) {
            String[] positionData = positionDatas.split(",");
            this.walkspace.add(new Position(Integer.parseInt(positionData[0]), Integer.parseInt(positionData[1])));
        }

        if (this.walkspace.stream().noneMatch(position -> position.getX() == this.startPosition.getX() && position.getY() == this.startPosition.getY())) {
            this.walkspace.add(new Position(this.startPosition.getX(), this.startPosition.getY()));
        }

        this.speeches = this.parseSpeech(speech);
        this.responses = this.parseSpeech(responses);
        this.unrecognisedSpeech = this.parseSpeech(unrecognisedResponses);
        this.drinks = Arrays.asList(drinks.split(","));
    }

    private List<BotSpeech> parseSpeech(String responses) {
        var botSpeech = new ArrayList<BotSpeech>();

        for (String sentence : responses.split(Pattern.quote("|"))) {
            String text = StringUtil.filterInput(sentence, true);

            if (text.isEmpty())
                continue;

            botSpeech.add(new BotSpeech(sentence));
        }

        return botSpeech;
    }

    public String getName() {
        return name;
    }

    public String getMission() {
        return mission;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public String getFigure() {
        return figure;
    }

    public List<Position> getWalkspace() {
        return walkspace;
    }

    public List<BotSpeech> getSpeeches() {
        return speeches;
    }

    public List<BotSpeech> getResponses() {
        return responses;
    }

    public List<BotSpeech> getUnrecognisedSpeech() {
        return unrecognisedSpeech;
    }

    public List<String> getDrinks() {
        return drinks;
    }
}
