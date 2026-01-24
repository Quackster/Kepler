package net.h4bbo.kepler.game.games.snowstorm.tasks;

import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormGame;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowStormAvatarObject;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowStormGameObject;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowStormMachineObject;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowStormSnowballObject;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormMath;

import java.util.List;

/**
 * Calculates game state checksums for client synchronization validation.
 */
public class SnowStormChecksumCalculator {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";

    private final SnowStormGame game;
    private final List<SnowStormSnowballObject> snowBalls;
    private boolean debugEnabled = false;

    public SnowStormChecksumCalculator(SnowStormGame game, List<SnowStormSnowballObject> snowBalls) {
        this.game = game;
        this.snowBalls = snowBalls;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public int calculate(int currentTurn) {
        int checksum = SnowStormMath.iterateSeed(currentTurn);

        if (debugEnabled) {
            System.out.println(ANSI_GREEN + "############# TURN: " + currentTurn + " ( " + checksum + " ) #############" + ANSI_RESET);
        }

        checksum += calculateMachineChecksums();
        checksum += calculatePlayerChecksums();
        checksum += calculateSnowballChecksums();

        if (debugEnabled) {
            System.out.println("CHECKSUM REPORT: " + checksum);
            System.out.println(ANSI_GREEN + "####################################" + ANSI_RESET);
        }

        return checksum;
    }

    private int calculateMachineChecksums() {
        int totalChecksum = 0;

        for (var object : game.getObjects()) {
            if (!(object instanceof SnowStormMachineObject)) {
                continue;
            }

            SnowStormMachineObject machine = (SnowStormMachineObject) object;
            List<Integer> values = machine.getChecksumValues();
            int machineChecksum = machine.getChecksumContribution();
            totalChecksum += machineChecksum;

            if (debugEnabled) {
                String[] labels = {"type: ", "int_id: ", "x: ", "y: ", "snowball_count: "};
                System.out.println("[ MACHINE " + machine.getId() + " ] ->" + formatDebugOutput(labels, values));
            }
        }

        return totalChecksum;
    }

    private int calculatePlayerChecksums() {
        int totalChecksum = 0;

        for (GamePlayer player : game.getActivePlayers()) {
            SnowStormAvatarObject avatar = (SnowStormAvatarObject) player.getGameObject();
            List<Integer> values = avatar.getChecksumValues();
            int playerChecksum = avatar.getChecksumContribution();
            totalChecksum += playerChecksum;

            if (debugEnabled) {
                String[] labels = {"type: ", "int_id: ", "x: ", "y: ", "body_direction: ", "hit_points: ",
                        "snowball_count: ", "is_bot: ", "activity_timer: ", "activity_state: ",
                        "next_tile_x: ", "next_tile_y: ", "move_target_x: ", "move_target_y: ",
                        "score: ", "player_id: ", "team_id: ", "room_index: "};
                System.out.println("[" + player.getPlayer().getDetails().getName() + "] ->" + formatDebugOutput(labels, values));
            }
        }

        return totalChecksum;
    }

    private int calculateSnowballChecksums() {
        int totalChecksum = 0;

        for (SnowStormSnowballObject ball : snowBalls) {
            List<Integer> values = ball.getChecksumValues();
            int ballChecksum = ball.getChecksumContribution();
            totalChecksum += ballChecksum;

            if (debugEnabled) {
                String[] labels = {"type: ", "int_id: ", "x: ", "y: ", "z: ", "movement_direction: ",
                        "trajectory: ", "time_to_live: ", "int_thrower_id: ", "parabola_offset: "};
                System.out.println("[BALL " + ball.getObjectId() + " ] ->" + formatDebugOutput(labels, values));
            }
        }

        return totalChecksum;
    }

    private String formatDebugOutput(String[] labels, List<Integer> values) {
        StringBuilder output = new StringBuilder();

        int count = Math.min(values.size(), labels.length);
        for (int i = 0; i < count; i++) {
            String label = labels[i].trim(); // remove your trailing space
            output.append('[')
                    .append(label)
                    .append(values.get(i))      // label already includes ":" so this prints "type:123"
                    .append("], ");
        }

        // remove trailing ", "
        if (output.length() >= 2) {
            output.setLength(output.length() - 2);
        }

        return output.toString();
    }

}
