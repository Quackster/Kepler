package org.alexdev.kepler.game.games.snowstorm.tasks;

import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.player.GameTeam;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.SnowStormTurn;
import org.alexdev.kepler.game.games.snowstorm.events.SnowStormAvatarMoveEvent;
import org.alexdev.kepler.game.games.snowstorm.events.SnowStormMachineAddSnowballEvent;
import org.alexdev.kepler.game.games.snowstorm.events.SnowStormMachineMoveSnowballsEvent;
import org.alexdev.kepler.game.games.snowstorm.mapping.SnowStormPathfinder;
import org.alexdev.kepler.game.games.snowstorm.objects.SnowStormMachineObject;
import org.alexdev.kepler.game.games.snowstorm.util.SnowStormFuture;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.pathfinder.Rotation;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.games.SNOWSTORM_GAMESTATUS;
import org.alexdev.kepler.util.schedule.FutureRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class SnowStormGameTask implements Runnable {
    private final Room room;
    private final SnowStormGame game;
    private List<SnowStormTurn> snowStormTurnList;
    private List<SnowStormFuture> futureEvents;
    private int maxGameTurns = 5;

    public SnowStormGameTask(Room room, SnowStormGame game) {
        this.room = room;
        this.game = game;
        this.resetTurns();
        this.futureEvents = new CopyOnWriteArrayList<>();
    }

    private void resetTurns() {
        this.snowStormTurnList = new CopyOnWriteArrayList<>();

        for (int i = 0; i < maxGameTurns; i++) {
            this.snowStormTurnList.add(new SnowStormTurn());
        }
    }

    @Override
    public void run() {
        try {
            if (this.game.getActivePlayers().isEmpty()) {
                return; // Don't send any packets or do any logic checks during when the game is finished
            }

            for (GameTeam gameTeam : this.game.getTeams().values()) {
                for (GamePlayer gamePlayer : gameTeam.getPlayers()) {
                    Player player = gamePlayer.getPlayer();

                    if (player != null
                            && player.getRoomUser().getRoom() != null
                            && player.getRoomUser().getRoom() == this.room) {
                        player.getRoomUser().handleSpamTicks();
                        this.processEntity(gamePlayer, this.game);
                    }
                }
            }

        } catch (Exception ex) {
            Log.getErrorLogger().error("SnowstormWalkTask crashed: ", ex);
        }

        try {
            for (var future : futureEvents) {
                if (future.getFramesFuture() == 0) {
                    sendQueue(0, future.getSubTurn(), future.getEvent());
                }
            }

            //if (this.snowStormTurnList.stream().anyMatch(turn -> turn.getSubTurns().size() > 0)) {
            for (Player player : this.game.getRoom().getEntityManager().getPlayers()) {
                player.send(new SNOWSTORM_GAMESTATUS(this.snowStormTurnList));
            }

            this.resetTurns();
            //}

            this.futureEvents.removeIf(future -> future.getFramesFuture() == 0);

            for (var future : futureEvents) {
                if (future.getFramesFuture() > 0) {
                    future.decrementFrame();
                }
            }

        } catch (Exception ex) {
            Log.getErrorLogger().error("SnowstormTask crashed: ", ex);

        }
    }


    public void sendQueue(int framesFuture, int subTurn, GameObject snowStormEvent) {
        if (framesFuture == 0) {
            try {
                this.snowStormTurnList.get(subTurn - 1).getSubTurns().add(snowStormEvent);
            } catch (Exception ex) {

            }

            return;
        }

        this.futureEvents.add(new SnowStormFuture(framesFuture, subTurn, snowStormEvent));
    }

    /**
     * Process entity.
     */
    private void processEntity(GamePlayer gamePlayer, SnowStormGame game) {
        if (gamePlayer.getSnowStormAttributes().isWalking()) {
            if (gamePlayer.getSnowStormAttributes().getCurrentPosition().equals(gamePlayer.getSnowStormAttributes().getWalkGoal())) {
                this.sendQueue(0, 1, new SnowStormAvatarMoveEvent(gamePlayer.getObjectId(),
                        gamePlayer.getSnowStormAttributes().getGoalWorldCoordinates()[0],
                        gamePlayer.getSnowStormAttributes().getGoalWorldCoordinates()[1]));
                gamePlayer.getSnowStormAttributes().setRotation(Rotation.calculateWalkDirection(gamePlayer.getSnowStormAttributes().getCurrentPosition(), gamePlayer.getSnowStormAttributes().getWalkGoal()));
                gamePlayer.getSnowStormAttributes().setNextGoal(null);
                gamePlayer.getSnowStormAttributes().setWalking(false);
                this.trySnowballMachine(gamePlayer, game);
                return;
            }

            var nextPosition = SnowStormPathfinder.getNextDirection(game, gamePlayer);

            if (nextPosition != null) {
                gamePlayer.getSnowStormAttributes().setRotation(Rotation.calculateWalkDirection(gamePlayer.getSnowStormAttributes().getCurrentPosition(), nextPosition));
                gamePlayer.getSnowStormAttributes().setCurrentPosition(nextPosition.copy());
                gamePlayer.getSnowStormAttributes().setNextGoal(nextPosition.copy());
                this.sendQueue(0, 1, new SnowStormAvatarMoveEvent(gamePlayer.getObjectId(),
                        gamePlayer.getSnowStormAttributes().getGoalWorldCoordinates()[0],
                        gamePlayer.getSnowStormAttributes().getGoalWorldCoordinates()[1]));
            } else {
                gamePlayer.getSnowStormAttributes().setNextGoal(null);
                gamePlayer.getSnowStormAttributes().setWalking(false);
            }
        }
    }

    private void trySnowballMachine(GamePlayer gamePlayer, SnowStormGame game) {
        //final var game = this;
        var snowballItemPosition = new Position(gamePlayer.getSnowStormAttributes().getCurrentPosition().getX(), gamePlayer.getSnowStormAttributes().getCurrentPosition().getY() - 1);
        var snowballMachineTile = game.getMap().getTile(snowballItemPosition);

        if (snowballMachineTile == null || snowballMachineTile.getHighestItem() == null || !snowballMachineTile.getHighestItem().isSnowballMachine()) {
            return;
        }

        SnowStormMachineObject obj = null;//this.game.getObjects().stream().filter((SnowStormMachineObject obj) -> obj.getPosition().)

        for (var object : game.getObjects()) {
            if (!(object instanceof SnowStormMachineObject)) {
                continue;
            }

            SnowStormMachineObject machineObject = (SnowStormMachineObject) object;

            if (machineObject.getPosition().equals(snowballItemPosition)) {
                obj = machineObject;
            }
        }

        if (obj == null) {
            return;
        }

        final var snowMachine = obj;
        //final var snowballsToAdd = new AtomicInteger(0);

        var snowMachineAnimation = new FutureRunnable() {
            public void run() {
                try {
                    if (!snowMachine.isPlayerCollectingSnowballs(game)) {
                        this.cancelFuture();
                        return;
                    }

                    if (snowMachine.getSnowballs().get() < 5) {
                        snowMachine.getSnowballs().incrementAndGet();
                        game.getUpdateTask().sendQueue(0, 1, new SnowStormMachineAddSnowballEvent(snowMachine.getId()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        /*var snowMachineRefill = new FutureRunnable() {
            public void run() {
                try {
                    if (!snowMachine.isPlayerCollectingSnowballs(game)) {
                        this.cancelFuture();
                        return;
                    }

                    if (snowMachine.getSnowballs().get() < 5 && snowballsToAdd.get() > 0) {
                        snowMachine.getSnowballs().incrementAndGet();
                        snowballsToAdd.set(0);
                        ;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };*/

        var snowMachineRestock = new FutureRunnable() {
            public void run() {
                try {
                    if (!snowMachine.isPlayerCollectingSnowballs(game)) {
                        this.cancelFuture();
                        return;
                    }

                    if (snowMachine.getSnowballs().get() > 0) {
                        if (gamePlayer.getSnowStormAttributes().getSnowballs().get() < 5) {
                            gamePlayer.getSnowStormAttributes().getSnowballs().incrementAndGet();
                            game.getUpdateTask().sendQueue(0, 1, new SnowStormMachineMoveSnowballsEvent(gamePlayer.getObjectId(), snowMachine.getId()));
                            snowMachine.getSnowballs().decrementAndGet();
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        snowMachineAnimation.setFuture(GameScheduler.getInstance().getService().scheduleWithFixedDelay(snowMachineAnimation, 0, 3, TimeUnit.SECONDS));
        snowMachineRestock.setFuture(GameScheduler.getInstance().getService().scheduleAtFixedRate(snowMachineRestock, 0, 1, TimeUnit.SECONDS));
    }

    public List<SnowStormTurn> getExecutingTurns() {
        return new ArrayList<>(this.snowStormTurnList);
    }

}
