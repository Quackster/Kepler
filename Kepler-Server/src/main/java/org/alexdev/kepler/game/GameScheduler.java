package org.alexdev.kepler.game;

import org.alexdev.kepler.dao.mysql.CurrencyDao;
import org.alexdev.kepler.dao.mysql.SettingsDao;
import org.alexdev.kepler.game.catalogue.RareManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class GameScheduler implements Runnable {
    private AtomicLong tickRate = new AtomicLong();

    private ScheduledExecutorService schedulerService;
    private ScheduledFuture<?> gameScheduler;
    private BlockingQueue<Player> creditsHandoutQueue;

    private static GameScheduler instance;

    private GameScheduler() {
        this.schedulerService = createNewScheduler();
        this.gameScheduler = this.schedulerService.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
        this.creditsHandoutQueue = new LinkedBlockingQueue<>();
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        this.tickRate.incrementAndGet();

        try {
            PlayerManager.getInstance().checkPlayerPeak();

            for (Player player : PlayerManager.getInstance().getPlayers()) {
                if (player.getRoomUser().getRoom() != null) {

                    // If their sleep timer is now lower than the current time, make them sleep.
                    if (DateUtil.getCurrentTimeSeconds() > player.getRoomUser().getTimerManager().getSleepTimer()) {
                        if (!player.getRoomUser().containsStatus(StatusType.SLEEP)) {
                            player.getRoomUser().removeDrinks();
                            player.getRoomUser().setStatus(StatusType.SLEEP, "");
                            player.getRoomUser().setNeedsUpdate(true);
                        }
                    }

                    // If their afk timer is up, send them out.
                    if (DateUtil.getCurrentTimeSeconds() > player.getRoomUser().getTimerManager().getAfkTimer()) {
                        player.getRoomUser().kick(true);
                    }

                    // If they're not sleeping (aka, active) and their next handout expired, give them their credits!
                    if (DateUtil.getCurrentTimeSeconds() > player.getDetails().getNextHandout()) {
                        if (!player.getRoomUser().containsStatus(StatusType.SLEEP)) {
                            this.creditsHandoutQueue.put(player);
                        }

                        player.getDetails().resetNextHandout();
                    }
                }
            }

            if (this.tickRate.get() % 30 == 0) { // Save every 30 seconds
                List<Player> playersToHandout = new ArrayList<>();
                this.creditsHandoutQueue.drainTo(playersToHandout);

                if (playersToHandout.size() > 0) {
                    Map<PlayerDetails, Integer> playerDetailsToSave = new LinkedHashMap<>();
                    Integer amount = GameConfiguration.getInstance().getInteger("credits.scheduler.amount");

                    for (Player p : playersToHandout) {
                        var details = p.getDetails();
                        playerDetailsToSave.put(details, amount);
                    }

                    CurrencyDao.increaseCredits(playerDetailsToSave);

                    for (Player p : playersToHandout) {
                        p.send(new CREDIT_BALANCE(p.getDetails()));
                    }
                }
            }

            RareManager.getInstance().performRareManagerJob(this.tickRate);
        } catch (Exception ex) {
            Log.getErrorLogger().error("GameScheduler crashed: ", ex);
        }
    }



    /**
     * Gets the scheduler service.
     *
     * @return the scheduler service
     */
    public ScheduledExecutorService getSchedulerService() {
        return schedulerService;
    }

    /**
     * Get the game scheduler loop
     *
     * @return the game scheduler loop
     */
    public ScheduledFuture<?> getGameScheduler() {
        return gameScheduler;
    }

    /**
     * Creates the new schedulerService.
     *
     * @return the scheduled executor service
     */
    public static ScheduledExecutorService createNewScheduler() {
        return Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Gets the instance
     *
     * @return the instance
     */
    public static GameScheduler getInstance() {
        if (instance == null) {
            instance = new GameScheduler();
        }

        return instance;
    }
}
