package net.h4bbo.kepler.game.games.wobblesquabble;

public enum WobbleSquabbleMove {
    BALANCE_LEFT(1, "A"),
    BALANCE_RIGHT(2, "D"),
    HIT_LEFT(3, "W"),
    HIT_RIGHT(4, "E"),
    WALK_FORWARD(5, "X"),
    WALK_BACKWARD(6, "S"),
    REBALANCE(7, "0"),
    NONE(0, "-");

    private int id;
    private String letter;

    WobbleSquabbleMove(int id, String letter) {
        this.id = id;
        this.letter = letter;
    }

    /**
     * Get the wobble squabble move enum.
     *
     * @param id the id of the move
     * @return the enum
     */
    public static WobbleSquabbleMove getMove(String id) {
        for (WobbleSquabbleMove move : values()) {
            if (move.getLetter().equals(id)) {
                return move;
            }
        }

        return null;
    }

    /**
     * Get the id of the move.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Get the letter of the move.
     *
     * @return the letter
     */
    public String getLetter() {
        return letter;
    }
}
