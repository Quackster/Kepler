package org.alexdev.kepler.game.ads;

import org.alexdev.kepler.dao.mysql.AdvertisementsDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class AdManager {
    private static AdManager instance;
    private final Map<Integer, List<Advertisement>> ads;

    public AdManager() {
        this.ads = AdvertisementsDao.getAds();
    }

    /**
     * Get the {@link AdManager} instance
     *
     * @return the item manager instance
     */
    public static AdManager getInstance() {
        if (instance == null) {
            instance = new AdManager();
        }

        return instance;
    }

    /**
     * Resets the item manager singleton.
     */
    public void reset() {
        instance = null;
        AdManager.getInstance();
    }

    /**
     * Get the collection of ads to use.
     *
     * @return the list of ads
     */
    public Advertisement getRandomAd(int roomId) {
        if (this.ads.containsKey(roomId)) {
            List<Advertisement> advertisements = this.ads.get(roomId).stream().filter(ad -> ad.isEnabled() && !ad.isLoadingAd()).collect(Collectors.toList());
            return advertisements.get(ThreadLocalRandom.current().nextInt(advertisements.size()));
        }

        return null;
    }

    /**
     * Get the collection of ads to use.
     *
     * @return the list of ads
     */
    public Advertisement getRandomLoadingAd() {
        List<Advertisement> advertisements = this.ads.containsKey(-1) ? this.ads.get(-1).stream().filter(ad -> ad.isEnabled() && ad.isLoadingAd()).collect(Collectors.toList()) : List.of();

        if (advertisements.size() > 0) {
            return advertisements.get(ThreadLocalRandom.current().nextInt(advertisements.size()));
        }

        return null;
    }

    /**
     * Get the collection of ads to use.
     *
     * @return the list of ads
     */
    public Advertisement getAd(int id) {
        for (var kvp : this.ads.values()) {
            for (Advertisement advertisement : kvp) {
                if (advertisement.getId() == id) {
                    return advertisement;
                }
            }
        }

        return null;
    }

    public List<Advertisement> getAds() {
        List<Advertisement> advertisementList = new ArrayList<>();

        for (var roomAds : ads.values()) {
            for (var ad : roomAds) {
                advertisementList.add(ad);
            }
        }

        return advertisementList;
    }
}