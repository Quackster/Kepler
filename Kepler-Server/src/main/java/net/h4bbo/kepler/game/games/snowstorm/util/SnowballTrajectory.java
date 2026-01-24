package net.h4bbo.kepler.game.games.snowstorm.util;

/**
 * Trajectory types for snowball throws.
 */
public enum SnowballTrajectory {
    QUICK_THROW(0, 3000),
    SHORT_TRAJECTORY(1, 3000),
    LONG_TRAJECTORY(2, 3000);

    private final int trajectoryId;
    private final int velocity;

    SnowballTrajectory(int trajectoryId, int velocity) {
        this.trajectoryId = trajectoryId;
        this.velocity = velocity;
    }

    public int getTrajectoryId() {
        return trajectoryId;
    }

    public int getVelocity() {
        return velocity;
    }

    public static SnowballTrajectory fromId(int id) {
        for (SnowballTrajectory trajectory : values()) {
            if (trajectory.trajectoryId == id) {
                return trajectory;
            }
        }
        return QUICK_THROW;
    }
}
