package org.alexdev.kepler.game;

import org.alexdev.kepler.dao.mysql.CurrencyDao;
import org.alexdev.kepler.dao.mysql.PurseDao;
import org.alexdev.kepler.game.catalogue.RareManager;
import org.alexdev.kepler.game.commandqueue.CommandQueueManager;
import org.alexdev.kepler.game.events.EventsManager;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.moderation.ChatManager;
import org.alexdev.kepler.game.moderation.cfh.CallForHelpManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class GameScheduler implements Runnable {
    private AtomicLong tickRate = new AtomicLong();

    private ScheduledExecutorService schedulerService;
    private ScheduledFuture<?> gameScheduler;

    private BlockingQueue<Player> creditsHandoutQueue;
    private BlockingQueue<Item> itemSavingQueue;
    private BlockingQueue<Integer> itemDeletionQueue;

    private static GameScheduler instance;

    private GameScheduler() {
        this.schedulerService = createNewScheduler();
        this.gameScheduler = this.schedulerService.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
        this.creditsHandoutQueue = new LinkedBlockingQueue<>();
        this.itemSavingQueue = new LinkedBlockingDeque<>();
        this.itemDeletionQueue = new LinkedBlockingDeque<>();
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            PlayerManager.getInstance().checkPlayerPeak();

            for (Player player : PlayerManager.getInstance().getPlayers()) {
                if (player.getRoomUser().getRoom() != null) {

                    // If their sleep timer is now lower than the current time, make them sleep.
                    if (DateUtil.getCurrentTimeSeconds() > player.getRoomUser().getTimerManager().getSleepTimer()) {
                        if (!player.getRoomUser().containsStatus(StatusType.AVATAR_SLEEP)) {
                            player.getRoomUser().removeDrinks();
                            player.getRoomUser().setStatus(StatusType.AVATAR_SLEEP, "");
                            player.getRoomUser().setNeedsUpdate(true);
                        }
                    }

                    // If their afk timer is up, send them out.
                    if (DateUtil.getCurrentTimeSeconds() > player.getRoomUser().getTimerManager().getAfkTimer()) {
                        player.getRoomUser().kick(true);
                    }

                    // If they're not sleeping (aka, active) and their next handout expired, give them their credits!
                    if (GameConfiguration.getInstance().getBoolean("credits.scheduler.enabled")) {
                        if (DateUtil.getCurrentTimeSeconds() > player.getDetails().getNextHandout()) {
                            if (!player.getRoomUser().containsStatus(StatusType.AVATAR_SLEEP)) {
                                this.creditsHandoutQueue.put(player);
                            }

                            player.getDetails().resetNextHandout();
                        }
                    }
                }
            }

            if (GameConfiguration.getInstance().getBoolean("credits.scheduler.enabled") &&
                    this.tickRate.get() % 30 == 0) { // Save every 30 seconds

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
                        PurseDao.logCreditSpend("gift", p.getDetails(), amount);
                        p.send(new CREDIT_BALANCE(p.getDetails()));
                    }
                }
            }

            // Purge expired rows
            if (this.tickRate.get() % TimeUnit.DAYS.toSeconds(1) == 0) {
                EventsManager.getInstance().removeExpiredEvents();
            }

            // Item saving queue ticker every 10 seconds
            if (this.tickRate.get() % 10 == 0) {
                if (this.itemSavingQueue != null) {
                    this.performItemSaving();
                }
            }

            // Execute Command Queue every 3 second
            if (this.tickRate.get() % 3 == 0) {
                CommandQueueManager.getInstance().executeCommands();
            }

            // Item deletion queue ticker every 5 second
            if (this.tickRate.get() % 5 == 0) {
                if (this.itemSavingQueue != null) {
                    this.performItemDeletion();
                }
            }

            // Delete expired CFH's every 60 seconds
            if (this.tickRate.get() % 60 == 0) {
                CallForHelpManager.getInstance().purgeExpiredCfh();
            }

            // Save chat messages every 60 seconds
            if (this.tickRate.get() % 60 == 0) {
                ChatManager.getInstance().performChatSaving();
            }

            RareManager.getInstance().performRareManagerJob(this.tickRate);
        } catch (Exception ex) {
            Log.getErrorLogger().error("GameScheduler crashed: ", ex);
        }

        this.tickRate.incrementAndGet();
    }

    /**
     * Queue item to be saved.
     *
     * @param item the item to save
     */
    public void queueSaveItem(Item item) {
        this.itemSavingQueue.removeIf(i -> i.getId() == item.getId());
        this.itemSavingQueue.add(item);
    }

    /**
     * Queue item to be deleted.
     *
     * @param itemId the item to delete
     */
    public void queueDeleteItem(int itemId) {
        this.itemDeletionQueue.removeIf(i -> i.equals(itemId));
        this.itemDeletionQueue.add(itemId);
    }

    /**
     * Method to perform item saving.
     */
    public void performItemSaving() {
        ItemManager.getInstance().performItemSaving(this.itemSavingQueue);
    }

    /**
     * Method to perform item deletion.
     */
    public void performItemDeletion() {
        ItemManager.getInstance().performItemDeletion(this.itemDeletionQueue);
    }


    /**
     * Gets the scheduler service.
     *
     * @return the scheduler service
     */
    public ScheduledExecutorService getService() {
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
