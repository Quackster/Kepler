package net.h4bbo.kepler.game.room.tasks;

import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.outgoing.rooms.items.SHOWPROGRAM;

import java.util.concurrent.ThreadLocalRandom;

public class SpaceCafeTask implements Runnable {
    public static final int FLIPBOARD_TIME = 60;
    public static final int LIGHT_TIME = 5;

    private static final int[] FLIPBOARD_ORDER  = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, };
    private final Room room;

    private int stageIndex;
    private long timeToNextStage;
    private long timeToNextLight;

    private int firstColour;
    private int secondColour;
    private int thirdColour;

    public SpaceCafeTask(Room room) {
        this.room = room;
        this.stageIndex = 2;
        this.timeToNextStage = convertToInterval(FLIPBOARD_TIME);
        this.timeToNextLight = convertToInterval(LIGHT_TIME);
    }

    @Override
    public void run() {
        if (timeToNextStage > 0) {
            timeToNextStage--;
        }

        if (this.timeToNextStage == 0) {
            processBoardFlip();
        }

        if (this.timeToNextLight > 0) {
            this.timeToNextLight--;
        }

        if (this.timeToNextLight == 0) {
            this.processLightColor();
        }
    }

    private void processLightColor() {
        int[] numbers = new int[] {4, 1, 2};

        int tempFirstColour = findNewColour(this.firstColour, numbers);
        int tempSecondColour = findNewColour(this.secondColour, numbers);
        int tempThirdColour = findNewColour(this.thirdColour, numbers);

        if (tempFirstColour != this.firstColour) {
            this.firstColour = tempFirstColour;
            this.room.send(new SHOWPROGRAM(new String[]{"lmpa", "ufol", String.valueOf(this.firstColour)}));
            this.room.send(new SHOWPROGRAM(new String[]{"liga", "litecol", String.valueOf(this.firstColour)}));
        }

        if (tempSecondColour != this.secondColour) {
            this.secondColour = tempSecondColour;
            this.room.send(new SHOWPROGRAM(new String[]{"lmpb", "ufol", String.valueOf(this.secondColour)}));
            this.room.send(new SHOWPROGRAM(new String[]{"ligb", "litecol", String.valueOf(this.secondColour)}));
        }

        if (tempThirdColour != this.thirdColour) {
            this.thirdColour = tempThirdColour;
            this.room.send(new SHOWPROGRAM(new String[]{"lmpc", "ufol", String.valueOf(this.thirdColour)}));
            this.room.send(new SHOWPROGRAM(new String[]{"ligc", "litecol", String.valueOf(this.thirdColour)}));
        }

        this.timeToNextLight = convertToInterval(LIGHT_TIME);
    }

    private void processBoardFlip() {
        this.stageIndex++;

        if (this.stageIndex >= FLIPBOARD_ORDER.length) {
            this.stageIndex = 0;
        }

        int previousBoard = this.stageIndex;
        int nextBoard = (previousBoard + 1) >= FLIPBOARD_ORDER.length ? 0 : previousBoard + 1;

        room.send(new SHOWPROGRAM(new String[]{"flipflop" + FLIPBOARD_ORDER[previousBoard], "visible", String.valueOf(0)}));
        room.send(new SHOWPROGRAM(new String[]{"flipflop" + FLIPBOARD_ORDER[nextBoard], "visible", String.valueOf(1)}));

        boolean isStillBoard = (previousBoard == 2 && nextBoard == 3) || (previousBoard == 5 && nextBoard == 6) || (previousBoard == 8 && nextBoard == 0);

        if (isStillBoard) {
            this.timeToNextStage = convertToInterval(FLIPBOARD_TIME);
        } else {
            this.timeToNextStage = 1;
        }
    }

    public int findNewColour(int currentColour, int[] selection) {
        return  selection[ThreadLocalRandom.current().nextInt(0, selection.length)];
        /*int newColour = currentColour;

        while (newColour == currentColour) {
            newColour = selection[ThreadLocalRandom.current().nextInt(0, selection.length)];
        }

        return newColour;*/
    }

    public static int convertToInterval(int seconds) {
        return seconds * 2;
    }
}
