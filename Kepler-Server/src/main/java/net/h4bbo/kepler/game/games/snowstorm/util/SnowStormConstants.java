package org.alexdev.kepler.game.games.snowstorm.util;

/**
 * Constants used throughout the SnowStorm game.
 */
public final class SnowStormConstants {
    private SnowStormConstants() {}

    // Movement
    public static final int SUBTURN_MOVEMENT = 640;
    public static final int VELOCITY_DIVISOR = 255;
    public static final int BASE_VELOCITY_MULTIPLIER = 2000;

    // Collision
    public static final int COLLISION_DISTANCE = 2000;
    public static final int BALL_HEIGHT_COLLISION_THRESHOLD = 5000;

    // Health and snowballs
    public static final int INITIAL_HEALTH = 4;
    public static final int MAX_SNOWBALLS = 5;

    // Activity timers (in game frames)
    public static final int STUNNED_TIMER = 125;
    public static final int INVINCIBILITY_TIMER = 60;
    public static final int CREATING_TIMER = 20;

    // Scoring
    public static final int HIT_SCORE = 1;
    public static final int STUN_SCORE = 5;

    // Game timing
    public static final long THROW_COOLDOWN_MS = 300;

    // Snowball machine
    public static final int MACHINE_SNOWBALL_GENERATOR_TIME = 100; // Ticks between snowball generation
    public static final int MACHINE_MAX_SNOWBALL_CAPACITY = 5;
}
