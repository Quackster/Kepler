package net.h4bbo.http.kepler.game.stickers;

import net.h4bbo.http.kepler.dao.HomesDao;
import net.h4bbo.http.kepler.dao.StoreDao;
import net.h4bbo.http.kepler.dao.WidgetDao;
import net.h4bbo.http.kepler.game.homes.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StickerManager {
    private static StickerManager instance;
    private List<StickerProduct> catalogueList;
    private List<StickerCategory> categoryList;

    public StickerManager() {
        this.categoryList = StoreDao.getCategories();
        this.catalogueList = StoreDao.getCatalogue();
    }

    /**
     * Create home handler.
     *
     * @param userId the user id to create the home for
     */
    public void createHome(int userId) {
        HomesDao.create(userId);

        /*

          <div class="movable sticker s_paper_clip_1" style="left: 183px; top: 371px; z-index: 171" id="sticker-47">
    </div>


    <div class="movable sticker s_needle_3" style="left: 109px; top: 19px; z-index: 134" id="sticker-48">
    </div>


    <div class="movable sticker s_sticker_spaceduck" style="left: 281px; top: 346px; z-index: 150" id="sticker-49">


         */

        WidgetDao.purchaseWidget(userId, 455, 27, 129,  1, this.getStickerByData("profilewidget", StickerType.HOME_WIDGET).getId(), "", 0, true);
        WidgetDao.purchaseWidget(userId, 440, 321, 177,  1, this.getStickerByData("roomswidget", StickerType.HOME_WIDGET).getId(), "", 0, true);
        WidgetDao.purchaseWidget(userId, 383, 491, 179,  6, this.getStickerByData("highscoreswidget", StickerType.HOME_WIDGET).getId(), "", 0, true);
        WidgetDao.purchaseWidget(userId, 183, 371, 171,  1, this.getStickerByData("paper_clip_1", StickerType.STICKER).getId(), "", 0, true);
        WidgetDao.purchaseWidget(userId, 109, 19, 134,  1, this.getStickerByData("needle_3", StickerType.STICKER).getId(), "", 0, true);
        WidgetDao.purchaseWidget(userId, 281, 346, 150,  1, this.getStickerByData("sticker_spaceduck", StickerType.STICKER).getId(), "", 0, true);
        WidgetDao.purchaseWidget(userId, 56, 229, 151,  2, this.getStickerByData("stickienote", StickerType.NOTE).getId(), "Welcome to a brand new Habbo Home page!\n" +
                "This is the place where you can express yourself with a wild and unique variety of stickers, hoot yo\n" +
                "trap off with colourful notes and showcase your Habbo rooms! To\n" +
                "start editing just click the edit button.\n", 0, true);

        WidgetDao.purchaseWidget(userId, 110, 409, 170,  5, this.getStickerByData("stickienote", StickerType.NOTE).getId(), "Where are my friends?\n" +
                "To add your buddy list to your page click edit and look in your widgets inventory. After placing it on the page you can move it all over the place and even change how it looks. Go on!", 0, true);

        WidgetDao.purchaseWidget(userId, 125, 38, 131,  4, this.getStickerByData("stickienote", StickerType.NOTE).getId(), "Remember!\n" +
                "Posting personal information about yourself or your friends, including addresses, phone numbers or email, and getting round the filter will result in your note being deleted.\n" +
                "Deleted notes will not be funded.\n", 0, true);

        WidgetDao.purchaseWidget(userId, 0, 0, 0,  1, this.getStickerByData("guestbookwidget", StickerType.HOME_WIDGET).getId(), "", 0, false);
        WidgetDao.purchaseWidget(userId, 0, 0, 0,  1, this.getStickerByData("badgeswidget", StickerType.HOME_WIDGET).getId(), "", 0, false);
        WidgetDao.purchaseWidget(userId, 0, 0, 0,  1, this.getStickerByData("friendswidget", StickerType.HOME_WIDGET).getId(), "", 0, false);
        WidgetDao.purchaseWidget(userId, 0, 0, 0,  1, this.getStickerByData("groupswidget", StickerType.HOME_WIDGET).getId(), "", 0, false);
        WidgetDao.purchaseWidget(userId, 0, 0, 0,  1, this.getStickerByData("traxplayerwidget", StickerType.HOME_WIDGET).getId(), "", 0, false);
        WidgetDao.purchaseWidget(userId, 0, 0, 0,  1, this.getStickerByData("ratingwidget", StickerType.HOME_WIDGET).getId(), "", 0, false);

    }

    public List<Widget> getDefaultWidgets(int userId) {
        List<Widget> widgets = new ArrayList<>();
        /*

          <div class="movable sticker s_paper_clip_1" style="left: 183px; top: 371px; z-index: 171" id="sticker-47">
    </div>


    <div class="movable sticker s_needle_3" style="left: 109px; top: 19px; z-index: 134" id="sticker-48">
    </div>


    <div class="movable sticker s_sticker_spaceduck" style="left: 281px; top: 346px; z-index: 150" id="sticker-49">


         */

        //(int id, int userId, int x, int y, int z, int stickerId, int skinId, int groupId, String text, int amount, boolean isPlaced, String extraData) {

        widgets.add(new Widget(1, userId, 455, 27, 129, this.getStickerByData("profilewidget", StickerType.HOME_WIDGET).getId(), 1, 1, "", 1, true, ""));
        widgets.add(new Widget(2, userId, 440, 321, 177, this.getStickerByData("roomswidget", StickerType.HOME_WIDGET).getId(), 6, 1, "", 1, true, ""));
        widgets.add(new Widget(3, userId, 383, 491, 179, this.getStickerByData("highscoreswidget", StickerType.HOME_WIDGET).getId(), 1, 1, "", 1, true, ""));
        widgets.add(new Widget(4, userId, 183, 371, 171, this.getStickerByData("paper_clip_1", StickerType.STICKER).getId(), 1, 1, "", 1, true, ""));
        widgets.add(new Widget(5, userId, 109, 19, 134, this.getStickerByData("needle_3", StickerType.STICKER).getId(), 1, 1, "", 1, true, ""));
        widgets.add(new Widget(6, userId, 281, 346, 150, this.getStickerByData("sticker_spaceduck", StickerType.STICKER).getId(), 2, 1, "", 1, true, ""));

        widgets.add(new Widget(7, userId, 56, 229, 151, this.getStickerByData("stickienote", StickerType.NOTE).getId(), 2, 1, "Welcome to a brand new Habbo Home page!\n" +
                "This is the place where you can express yourself with a wild and unique variety of stickers, hoot yo\n" +
                "trap off with colourful notes and showcase your Habbo rooms! To\n" +
                "start editing just click the edit button.\n", 1, true, ""));

        widgets.add(new Widget(8, userId, 110, 409, 170, this.getStickerByData("stickienote", StickerType.NOTE).getId(), 5, 1, "To add your buddy list to your page click edit and look in your widgets inventory. After placing it on the page you can move it all over the place and even change how it looks. Go on!",
                1, true, ""));

        widgets.add(new Widget(9, userId, 125, 38, 131, this.getStickerByData("stickienote", StickerType.NOTE).getId(), 4, 1, "Remember!\n" +
                "Posting personal information about yourself or your friends, including addresses, phone numbers or email, and getting round the filter will result in your note being deleted.\n" +
                "Deleted notes will not be funded.\n", 1, true, ""));


        return widgets;
    }

    /**
     * Get the sticker list.
     *
     * @return the sticker list
     */
    public List<StickerProduct> getCatalogueList() {
        return catalogueList;
    }

    /**
     * Get the category list by minimum rank.
     *
     * @param minRank the min rank to check
     * @return the list of categories
     */
    public List<StickerCategory> getCategories(int minRank) {
        return categoryList.stream().filter(category -> minRank >= category.getMinRank()).collect(Collectors.toList());
    }

    /**
     * Get the category by id
     *
     * @return the categrory
     */
    public StickerCategory getCategory(int id) {
        return categoryList.stream().filter(category -> category.getId() == id).findFirst().orElse(null);
    }

    /**
     * Get the sticker product by id.
     *
     * @param stickerId the sticker id
     * @return the sticker
     */
    public StickerProduct getStickerProduct(int stickerId) {
        return catalogueList.stream().filter(product -> product.getId() == stickerId).findFirst().orElse(null);
    }

    /**
     * Get the skin by id.
     *
     * @param skinId the skin id
     * @return the skin name
     */
    public String getSkin(int skinId) {
        switch (skinId) {
            case 1:
                return "defaultskin";
            case 2:
                return "speechbubbleskin";
            case 3:
                return "metalskin";
            case 4:
                return "noteitskin";
            case 5:
                return "notepadskin";
            case 6:
                return "goldenskin";
            case 7:
                return "hc_machineskin";
            case 8:
                return "hc_pillowskin";
        }

        return "nakedskin";
    }

    /**
     * Method to get sticker by data.
     *
     * @param data the data of the sticker
     * @return the sticker
     */
    public StickerProduct getStickerByData(String data, StickerType stickerType) {
        return catalogueList.stream().filter(product -> product.getData().toLowerCase().equals(data.toLowerCase()) && product.getType() == stickerType).findFirst().orElse(null);
    }

    /**
     * Get instance of {@link StickerManager}
     *
     * @return the manager instance
     */
    public static StickerManager getInstance() {
        if (instance == null) {
            instance = new StickerManager();
        }

        return instance;
    }
}
