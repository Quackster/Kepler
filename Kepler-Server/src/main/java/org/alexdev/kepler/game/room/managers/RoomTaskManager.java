package org.alexdev.kepler.game.room.managers;

import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.games.Game;
import org.alexdev.kepler.game.games.GameManager;
import org.alexdev.kepler.game.games.battleball.BattleBallGame;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.SnowstormUpdateTask;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.tasks.EntityTask;
import org.alexdev.kepler.game.games.battleball.BattleBallTask;
import org.alexdev.kepler.game.room.tasks.RollerTask;
import org.alexdev.kepler.game.room.tasks.StatusTask;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RoomTaskManager {
    private Room room;
    private ScheduledExecutorService executorService;
    private Map<String, Pair<ScheduledFuture<?>, Runnable>> processTasks;

    public RoomTaskManager(Room room) {
        this.room = room;
        this.processTasks = new ConcurrentHashMap<>();
        this.executorService = GameScheduler.getInstance().getSchedulerService();
    }

    /**
     * Start all needed room tasks, called when there's at least 1 player in the room.
     */
    public void startTasks() {
        if (this.room.getModel().getName().contains("_arena_") || this.room.getModel().getName().contains("snowwar_arena")) {
            this.loadGameTasks();
            return;
        }

        this.scheduleTask("EntityTask", new EntityTask(this.room), 0, 500, TimeUnit.MILLISECONDS);
        this.scheduleTask("StatusTask", new StatusTask(this.room), 0, 1, TimeUnit.SECONDS);

        // Only allow roller tasks to be created for private rooms
        if (!this.room.isPublicRoom()) {
            int rollerMillisTask = GameConfiguration.getInstance().getInteger("roller.tick.default");
            this.scheduleTask("RollerTask", new RollerTask(this.room), 1, rollerMillisTask, TimeUnit.MILLISECONDS);
        }
    }

    private void loadGameTasks() {
        Game game = GameManager.getInstance().getGameById(this.room.getId());

        if (game instanceof BattleBallGame) {
            BattleBallGame battleballGame = (BattleBallGame) game;
            this.scheduleTask("GameTask", new BattleBallTask(this.room, battleballGame), 0, 500, TimeUnit.MILLISECONDS);
        }

        if (game instanceof SnowStormGame) {
            SnowStormGame snowStormGame = (SnowStormGame) game;
            this.scheduleTask("GameTask", new SnowstormUpdateTask(this.room, snowStormGame), 0, 500, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Stop tasks, called when there's no players in the room.
     */
    public void stopTasks() {
        for (var tasksKvp : this.processTasks.entrySet()) {
            tasksKvp.getValue().getLeft().cancel(false);
        }

        this.processTasks.clear();
    }

    /**
     * Schedule a custom task with a delay.
     *
     * @param taskName the task name identifier
     * @param runnableTask the runnable task instance
     * @param interval the interval of the task
     * @param delay the time to wait before the task starts
     * @param timeUnit the time unit of the interval
     */
    public void scheduleTask(String taskName, Runnable runnableTask, int delay, int interval, TimeUnit timeUnit) {
        this.cancelTask(taskName);

        var future = this.executorService.scheduleAtFixedRate(runnableTask, delay, interval, timeUnit);
        this.processTasks.put(taskName, Pair.of(
                future,
                runnableTask
        ));
    }

    /**
     * Cancels a custom task by task name.
     *
     * @param taskName the name of the task to cancel
     */
    public void cancelTask(String taskName) {
        if (this.processTasks.containsKey(taskName)) {
            this.processTasks.get(taskName).getLeft().cancel(false);
            this.processTasks.remove(taskName);
        }
    }

    /**
     * Gets the task by the task name
     *
     * @param taskName the task name to locate task
     * @return the task instance
     */
    public Runnable getTask(String taskName) {
        if (this.processTasks.containsKey(taskName)) {
            return this.processTasks.get(taskName).getValue();
        }

        return null;
    }

    /**
     * Get if the task is registered.
     *
     * @param taskName the task name to check for
     * @return true, if successful
     */
    public boolean hasTask(String taskName) {
        return this.processTasks.containsKey(taskName);
    }
}
