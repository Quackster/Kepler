package org.alexdev.kepler.game.games.snowstorm;

import org.alexdev.kepler.game.games.GameObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TurnContainer {
    private AtomicInteger currentTurn;
    private int checkSum;

    public TurnContainer() {
        this.currentTurn = new AtomicInteger(-1);
    }

    /**
     * Set the object list used to create a checksum for, and create a seed based on the current
     * turn of the checksum.
     *
     * @param objectList the object list that's sent to the client
     */
    public void calculateChecksum(List<GameObject> objectList) {
        int tCheckSum;

        tCheckSum = iterateSeed(this.currentTurn.get());
        tCheckSum = calculateChecksum(tCheckSum, objectList);

        this.checkSum = tCheckSum;
    }

    /**
     * Calculate object checksum based on the seed given
     * @param tSeed the checksum created by above
     * @param objectList
     * @return the integer to add on to the seed
     */
    private int calculateChecksum(int tSeed, List<GameObject> objectList) {
        int tCheckSum = tSeed;

        for (var object : objectList) {
            SnowStormObject gameObject = (SnowStormObject) object;
            tCheckSum = tCheckSum + addChecksum(gameObject.getGameObjectsSyncValues());
        }

        return tCheckSum;
    }

    /**
     * Combine the sync values used from the objects into a single integer
     *
     * @param pGameObjectSyncValues the sync values from the object
     * @return the new integer
     */
    private int addChecksum(List<Integer> pGameObjectSyncValues) {
        int tCheckSum = 0;
        int tCounter = 1;
        int i = 0;

        for (int value : pGameObjectSyncValues) {
            tCheckSum = tCheckSum + (value * tCounter);
            tCounter = tCounter + 1;
        }

        return tCheckSum;
    }

    /**
     * Seed generation, taken from client source.
     *
     * @param a_iSeed the current turn
     * @return the seed created
     */
    private int iterateSeed(int a_iSeed) {
        var t_iSeed2 = 0;

        if (a_iSeed == 0) {
            a_iSeed = -1;
        }

        t_iSeed2 = a_iSeed << 13;
        a_iSeed = a_iSeed ^ t_iSeed2;
        t_iSeed2 = a_iSeed >> 17;
        a_iSeed = a_iSeed ^ t_iSeed2;
        t_iSeed2 = a_iSeed << 5;
        a_iSeed = a_iSeed ^ t_iSeed2;

        return a_iSeed;
    }

    /**
     * Increments the current turn
     */
    public void iterateTurn() {
        this.currentTurn.incrementAndGet();
    }

    /**
     * Get the current turn in the turn container
     *
     * @return the turn
     */
    public int getCurrentTurn() {
        return currentTurn.get();
    }

    /**
     * Get the checksum created
     * @return the checksum
     */
    public int getCheckSum() {
        return checkSum;
    }
}
