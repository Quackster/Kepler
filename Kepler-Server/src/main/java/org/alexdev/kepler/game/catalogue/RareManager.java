package org.alexdev.kepler.game.catalogue;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.dao.mysql.RareDao;
import org.alexdev.kepler.dao.mysql.SettingsDao;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class RareManager {
    private static RareManager instance;
    private final String RARE_TICK_SETTING = "rare.cycle.tick.time";

    private LinkedList<CatalogueItem> rareList;
    private Map<CatalogueItem, Integer> rareCost;
    private Map<String, Long> daysSinceUsed;

    private CatalogueItem currentRare;
    private Long currentRareTime;
    private AtomicLong tickTime;

    public RareManager() {
        this.rareCost = new HashMap<>();
        this.tickTime = new AtomicLong(GameConfiguration.getInstance().getLong(RARE_TICK_SETTING));

        String[] hourData = GameConfiguration.getInstance().getString("rare.cycle.pages").split("\\|");

        for (String numbers : hourData) {
            int cataloguePage = Integer.parseInt(numbers.split(",")[0]);
            int hoursRequired = Integer.parseInt(numbers.split(",")[1]);

            if (hoursRequired > 0) {
                for (CatalogueItem item : CatalogueManager.getInstance().getCataloguePageItems(cataloguePage)) {
                    this.rareCost.put(item, getHandoutAmountInHours(hoursRequired));
                }
            }
        }

        try {
            this.daysSinceUsed = RareDao.getUsedRares();

            if (this.daysSinceUsed.size() > 0) {
                var currentItemData = RareDao.getCurrentRare();
                this.currentRare = CatalogueManager.getInstance().getCatalogueItem(currentItemData.getKey());
                this.currentRareTime = currentItemData.getValue(); // Get the active item
            }

            this.loadRares();

            // If there was no current rare, or the current rare time ran out, then cycle to the next rare
            if (this.currentRare == null) {
                this.selectNewRare();
            }

        } catch (Exception ex) {
            Storage.logError(ex);
        }
    }

    /**
     * Finds all rares that can't be accessed normally by the user, and then shuffles the list.
     */
    private void loadRares() {
        this.rareList = new LinkedList<>();

        for (CataloguePage cataloguePage : CatalogueManager.getInstance().getCataloguePages()) {
            // Skip pages where normal users can access
            if (!(cataloguePage.getMinRole().getRankId() > 1)) {
                continue;
            }

            // Search in rares pages only
            if (!cataloguePage.getLayout().equals("ctlg_layout2") || !cataloguePage.getImageHeadline().equals("catalog_rares_headline1")) {
                continue;
            }

            this.rareList.addAll(CatalogueManager.getInstance().getCataloguePageItems(cataloguePage.getId()));
        }

        Collections.shuffle(this.rareList);
    }

    /**
     * Selects a new rare, adds it to the database so it can only be selected once every X interval defined (default is 3 days).
     */
    public void selectNewRare() throws SQLException {
        TimeUnit reuseTimeUnit = TimeUnit.valueOf(GameConfiguration.getInstance().getString("rare.cycle.reuse.timeunit"));
        long interval = reuseTimeUnit.toSeconds(GameConfiguration.getInstance().getInteger("rare.cycle.reuse.interval"));

        List<String> toRemove = new ArrayList<>();

        // Remove expired rares
        for (var kvp : this.daysSinceUsed.entrySet()) {
            if (DateUtil.getCurrentTimeSeconds() > kvp.getValue()) {
                toRemove.add(kvp.getKey());
            }
        }

        for (var sprite : toRemove) {
            this.daysSinceUsed.remove(sprite);
        }

        RareDao.removeRares(toRemove);

        // If the rare list has ran out, reload it.
        if (this.rareList.isEmpty()) {
            this.loadRares();
        }

        CatalogueItem rare = this.rareList.pollFirst(); // Select the rare from the rare list

        if (rare != null) {
            // If the rare is in the expired list, search for another rare
            if (this.daysSinceUsed.containsKey(rare.getDefinition().getSprite())) {
                this.currentRare = null; // Set to null in case we can't find one, so it can default back to the default catalogue item set in database

                if (this.rareList.size() > 0) {
                    this.selectNewRare();
                }

                return;
            }

            this.currentRare = rare;

            // Handle override by using "rare.cycle.reuse.CATALOGUE_SALE_CODE.timeunit" and "rare.cycle.reuse.CATALOGUE_SALE_CODE.interval"
            String overrideUnit = GameConfiguration.getInstance().getString("rare.cycle.reuse." + rare.getSaleCode() + ".timeunit", null);

            if (overrideUnit != null) {
                reuseTimeUnit = TimeUnit.valueOf(overrideUnit);
                interval = reuseTimeUnit.toSeconds(GameConfiguration.getInstance().getInteger("rare.cycle.reuse." + rare.getSaleCode() + ".interval"));
            }

            // Add rare to expiry table so it can't be used for a certain X number of days
            this.daysSinceUsed.put(rare.getDefinition().getSprite(), DateUtil.getCurrentTimeSeconds() + interval);

            RareDao.removeRares(List.of(rare.getDefinition().getSprite()));
            RareDao.addRare(rare.getDefinition().getSprite(), DateUtil.getCurrentTimeSeconds() + interval);

            this.tickTime.set(0);
            this.saveTick();
        }
    }

    /**
     * Get the credit amount handout but in hours.
     *
     * @param hours the hours to select for
     * @return the amount of rareCost
     */
    public int getHandoutAmountInHours(int hours) {
        TimeUnit unit = TimeUnit.valueOf(GameConfiguration.getInstance().getString("credits.scheduler.timeunit"));
        long interval = unit.toMinutes(GameConfiguration.getInstance().getInteger("credits.scheduler.interval"));

        long minutesInHour = 60;
        long minutes = minutesInHour / interval;

        return (int) ((hours * minutes) * GameConfiguration.getInstance().getInteger("credits.scheduler.amount"));
    }

    /**
     * Tick manager for checking expiry of the rare on sale.
     *
     * @param tickTime the global tick counter instance
     * @throws SQLException for when selectNewRare() fails
     */
    public void performRareManagerJob(AtomicLong tickTime) throws SQLException {
        // Rare cycle management
        TimeUnit rareManagerUnit = TimeUnit.valueOf(GameConfiguration.getInstance().getString("rare.cycle.refresh.timeunit"));
        long interval = rareManagerUnit.toSeconds(GameConfiguration.getInstance().getInteger("rare.cycle.refresh.interval"));

        RareManager.getInstance().getTick().incrementAndGet();

        // Save tick time every 60 seconds...
        if (tickTime.get() % 60 == 0) {
            RareManager.getInstance().saveTick();
        }

        // Select new rare
        if (RareManager.getInstance().getTick().get() >= interval) {
            RareManager.getInstance().selectNewRare();
        }
    }

    /**
     * Remove the colour tag from the sprite name. eg pillow*1 to pillow, used for
     * comparing the same items which are just different colours.
     *
     * @param sprite the sprite to remove the colour tag from
     * @return the new sprite
     */
    private String stripColor(String sprite) {
        return sprite.contains("*") ? sprite.split("\\*")[0] : sprite;
    }

    /**
     * Get the current random rare
     * @return the random rare
     */
    public CatalogueItem getCurrentRare() {
        return currentRare;
    }

    /**
     * Get the rares costs.
     *
     * @return the map of rares costs
     */
    public Map<CatalogueItem, Integer> getRareCost() {
        return rareCost;
    }

    /**
     * Get the {@link RareManager} instance
     *
     * @return the rare manager instance
     */
    public static RareManager getInstance() {
        if (instance == null) {
            instance = new RareManager();
        }

        return instance;
    }

    /**
     * Get the current tick.
     * @return the tick time
     */
    public AtomicLong getTick() {
        return tickTime;
    }

    /**
     * Save the tick time to database
     */
    public void saveTick() {
        GameConfiguration.getInstance().getConfig().put(RARE_TICK_SETTING, String.valueOf(RareManager.getInstance().getTick().get()));
        SettingsDao.updateSetting(RARE_TICK_SETTING, GameConfiguration.getInstance().getString(RARE_TICK_SETTING));
    }
}
