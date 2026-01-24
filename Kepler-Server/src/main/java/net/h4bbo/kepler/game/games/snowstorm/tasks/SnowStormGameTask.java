package net.h4bbo.kepler.game.games.snowstorm.tasks;

import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.*;
import net.h4bbo.kepler.game.games.snowstorm.events.SnowStormAvatarMoveEvent;
import net.h4bbo.kepler.game.games.snowstorm.events.SnowStormMachineAddSnowballEvent;
import net.h4bbo.kepler.game.games.snowstorm.events.SnowStormMachineMoveSnowballsEvent;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowStormAvatarObject;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowStormMachineObject;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowStormSnowballObject;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormAttributes;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.log.Log;
import net.h4bbo.kepler.messages.outgoing.games.GAMESTATUS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SnowStormGameTask implements Runnable {
    private static final int MAX_GAME_TURNS = 5;

    private final Room room;
    private final SnowStormGame game;
    private final SnowStormChecksumCalculator checksumCalculator;

    /**
     * Thread-safe via intrinsic lock on the list instance.
     * NOTE: Never expose this list directly; always return snapshots.
     */
    private final List<SnowStormSnowballObject> snowballs =
            Collections.synchronizedList(new ArrayList<>());


    private List<List<GameObject>> snowStormTurnList;
    private int currentTurn = 0;
    private int currentChecksum = 0;

    public SnowStormGameTask(Room room, SnowStormGame game) {
        this.room = room;
        this.game = game;
        this.checksumCalculator = new SnowStormChecksumCalculator(game, snowballs);
        this.snowStormTurnList = new CopyOnWriteArrayList<>();
        resetTurns();
    }

    @Override
    public void run() {
        try {
            if (game.getActivePlayers().isEmpty()) return;

            if (game.isGameFinished()) return;

            game.getTeams().values().stream()
                    .flatMap(team -> team.getPlayers().stream())
                    .filter(gp -> gp.getPlayer() instanceof Player p && p.getRoomUser().getRoom() == room)
                    .forEach(gp -> {
                        gp.getPlayer().getRoomUser().handleSpamTicks();
                        processEntity(gp);
                    });
        } catch (Exception ex) {
            Log.getErrorLogger().error("SnowstormWalkTask crashed: ", ex);
        }

        try {
            var status = new GAMESTATUS(snowStormTurnList, currentTurn, currentChecksum);
            game.getRoom().getEntityManager().getPlayers().forEach(p -> p.send(status));

            resetTurns();
        } catch (Exception ex) {
            Log.getErrorLogger().error("SnowstormTask crashed: ", ex);
        }
    }

    public void queueEvent(GameObject event) {
        try {
            snowStormTurnList.getFirst().add(event);
        } catch (Exception ex) {
            Log.getErrorLogger().error("Failed to queue event", ex);
        }
    }

    private void resetTurns() {
        snowStormTurnList = new CopyOnWriteArrayList<>();
        currentTurn++;

        var collisionEvents = new ArrayList<SnowStormDelayedEvent>();
        var machinesToAddSnowball = new ArrayList<SnowStormMachineObject>();

        for (int i = 0; i < MAX_GAME_TURNS; i++) {
            snowStormTurnList.add(new CopyOnWriteArrayList<>());

            List<SnowStormSnowballObject> ballsThisFrame = snowballsSnapshot();

            for (var player : game.getActivePlayers()) {
                SnowStormAvatarObject avatar = SnowStormAvatarObject.getAvatar(player);

                if (avatar != null) {
                    avatar.calculateFrameMovement();
                    collisionEvents.addAll(avatar.checkCollisions(ballsThisFrame));
                }
            }

            // Move balls + remove dead ones
            for (var ball : ballsThisFrame) {
                ball.calculateFrameMovement();

                if (!ball.isAlive()) {
                    boolean removed;
                    synchronized (snowballs) {
                        removed = snowballs.remove(ball);
                    }

                    if (removed) {
                        game.removeSnowBall(ball);
                    }
                }
            }

            // Process machine snowball generation
            for (var obj : game.getObjects()) {
                if (obj instanceof SnowStormMachineObject machine && machine.processGeneratorTick()) {
                    queueEvent(new SnowStormMachineAddSnowballEvent(machine.getId()));
                    machinesToAddSnowball.add(machine);
                }
            }
        }

        synchronized (snowballs) {
            currentChecksum = checksumCalculator.calculate(currentTurn);
        }

        // Apply state changes AFTER checksum calculation
        for (var machine : machinesToAddSnowball) {
            machine.addSnowball();
        }

        // Check for machine pickup after snowballs are added
        for (var player : game.getActivePlayers()) {
            var attr = SnowStormPlayers.get(player);

            for (var obj : game.getObjects()) {
                if (obj instanceof SnowStormMachineObject machine && machine.canPickup(attr)) {
                    queueEvent(new SnowStormMachineMoveSnowballsEvent(player.getObjectId(), machine.getId()));
                    machine.applyPickup(attr);
                    break;
                }
            }
        }

        SnowStormAvatarObject.handleForSnowballCollisions(collisionEvents);
    }

    private void processEntity(GamePlayer gamePlayer) {
        var attr = SnowStormPlayers.get(gamePlayer);
        if (!attr.isWalking()) return;

        queueEvent(new SnowStormAvatarMoveEvent(
                gamePlayer.getObjectId(),
                attr.getGoalWorldCoordinates()[0],
                attr.getGoalWorldCoordinates()[1]
        ));
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public int getCurrentChecksum() {
        return currentChecksum;
    }

    /**
     * Snapshot only (never return the live list).
     */
    public List<SnowStormSnowballObject> getSnowballs() {
        synchronized (snowballs) {
            return List.copyOf(snowballs);
        }
    }

    public List<List<GameObject>> getExecutingTurns() {
        return List.copyOf(snowStormTurnList);
    }

    public void setChecksumDebugEnabled(boolean enabled) {
        checksumCalculator.setDebugEnabled(enabled);
    }

    public void addSnowball(SnowStormSnowballObject ball) {
        if (ball == null) return;
        synchronized (snowballs) {
            snowballs.add(ball);
        }
    }
    public boolean removeSnowball(SnowStormSnowballObject ball) {
        if (ball == null) return false;
        synchronized (snowballs) {
            return snowballs.remove(ball);
        }
    }

    private List<SnowStormSnowballObject> snowballsSnapshot() {
        synchronized (snowballs) {
            return List.copyOf(snowballs);
        }
    }
}
