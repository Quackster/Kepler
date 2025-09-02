package net.h4bbo.http.kepler.controllers.homes.store;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.config.Settings;
import net.h4bbo.kepler.dao.mysql.CurrencyDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import net.h4bbo.http.kepler.dao.WidgetDao;
import net.h4bbo.http.kepler.game.stickers.StickerCategory;
import net.h4bbo.http.kepler.game.stickers.StickerManager;
import net.h4bbo.http.kepler.game.stickers.StickerProduct;
import net.h4bbo.http.kepler.game.stickers.StickerType;
import net.h4bbo.http.kepler.util.RconUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class StoreController {
    public static void main(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var tpl = webConnection.template("homes/store/main");
        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (playerDetails == null) {
            webConnection.session().delete("user.id");
            webConnection.session().delete("authenticated");
            webConnection.redirect("/");
            return;
        }

        List<StickerCategory> categories = StickerManager.getInstance().getCategories(playerDetails.getRank().getRankId());
        var stickerCategories = categories.stream().filter(category -> category.getCategoryType() == StickerCategory.STICKER_BACKGROUND_TYPE).sorted(Comparator.comparing(StickerCategory::getName)).collect(Collectors.toList());
        var backgroundCategories = categories.stream().filter(category -> category.getCategoryType() == StickerCategory.BACKGROUND_CATEGORY_TYPE).sorted(Comparator.comparing(StickerCategory::getName)).collect(Collectors.toList());

        int stickerCategory = -1;
        List<StickerProduct> products = new ArrayList<>();

        if (stickerCategories.size() > 0) {
            stickerCategory = stickerCategories.get(0).getId();
        } else if (backgroundCategories.size() > 0) {
            stickerCategory = backgroundCategories.get(0).getId();
        }

        if (stickerCategory != -1) {
            int finalStickerCategory = stickerCategory;
            products = StickerManager.getInstance().getCatalogueList().stream().filter(product -> product.getCategoryId() == finalStickerCategory).collect(Collectors.toList());
        }

        int emptyBoxes = 0;

        if (products.size() > 20) {
            emptyBoxes = (int) (Math.ceil(products.size()/5.0) * 5);
        } else {
            emptyBoxes = 20 - products.size();
        }

        StickerProduct product = null;

        if (products.size() > 0) {
            product = products.get(0);
            webConnection.headers().put("X-JSON", "[[\"Inventory\",\"Web Store\"],[{\"itemCount\":" + product.getAmount() + ",\"previewCssClass\":\"" + product.getCssClass() + "\",\"titleKey\":\"\"}]]");
        } else {
            webConnection.headers().put("X-JSON", "[[\"Inventory\",\"Web Store\"],[{\"itemCount\":0,\"titleKey\":\"\"}]]");
        }

        List<Object> emptyBox = new ArrayList<>();

        if (emptyBoxes > 0) {
            for (int i = 0; i < emptyBoxes; i++) {
                emptyBox.add(null);
            }
        }

        tpl.set("stickerCategories", stickerCategories);
        tpl.set("backgroundCategories", backgroundCategories);
        tpl.set("products", products);
        tpl.set("product", product);
        tpl.set("emptyBoxes", emptyBox);
        tpl.render();
    }

    public static void items(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var tpl = webConnection.template("homes/store/items");
        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (playerDetails == null) {
            webConnection.session().delete("user.id");
            webConnection.session().delete("authenticated");
            webConnection.redirect("/");
            return;
        }

        int subCategory = webConnection.post().getInt("subCategoryId");
        StickerCategory category = StickerManager.getInstance().getCategory(subCategory);

        if (category == null) {
            webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            return;
        }

        List<StickerProduct> products = StickerManager.getInstance().getCatalogueList().stream().filter(product -> product.getCategoryId() == category.getId()).collect(Collectors.toList());

        int emptyBoxes = 0;

        if (products.size() > 20) {
            emptyBoxes = (int) (Math.ceil(products.size()/5.0) * 5);
        } else {
            emptyBoxes = 20 - products.size();
        }

        tpl.set("products", products);
        tpl.set("emptyProducts", emptyBoxes);
        tpl.render();
    }

    public static void preview(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int productId = webConnection.post().getInt("productId");
        StickerProduct stickerProduct = StickerManager.getInstance().getCatalogueList().stream().filter(product -> product.getId() == productId).findFirst().orElse(null);

        if (stickerProduct == null) {
            webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            return;
        }
        
        if (stickerProduct.getType() == StickerType.STICKER || stickerProduct.getType() == StickerType.NOTE) {
            webConnection.headers().put("X-JSON", "[{\"itemCount\":" + stickerProduct.getAmount() + ",\"previewCssClass\":\"" + stickerProduct.getCssClass() + "\",\"titleKey\":\"" + stickerProduct.getName() + "\"}]");
        } else if (stickerProduct.getType() == StickerType.BACKGROUND){
            webConnection.headers().put("X-JSON", "[{\"bgCssClass\":\"b_" + stickerProduct.getData() + "\",\"itemCount\":" + stickerProduct.getAmount() + ",\"previewCssClass\":\"" + stickerProduct.getCssClass() + "\",\"titleKey\":\"" + stickerProduct.getName() + "\"}]");
        }

        var tpl = webConnection.template("homes/store/preview");
        tpl.set("product", stickerProduct);
        tpl.render();
    }

    public static void purchaseConfirm(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int productId = webConnection.post().getInt("productId");
        StickerProduct stickerProduct = StickerManager.getInstance().getCatalogueList().stream().filter(product -> product.getId() == productId).findFirst().orElse(null);

        if (stickerProduct == null) {
            return;
        }


        var tpl = webConnection.template("homes/store/purchase_confirm");
        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        tpl.set("product", stickerProduct);

        if (playerDetails.getCredits() < stickerProduct.getPrice()) {
            tpl.set("noCredits", true);
        } else {
            tpl.set("noCredits", false);
        }

        tpl.render();
    }

    public static void backgroundWarning(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var tpl = webConnection.template("homes/store/background_warning");
        tpl.render();

    }

    public static void purchaseBackgrounds(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        int widgetId = webConnection.post().getInt("selectedId");

        /*

        <p>
You already own this item.<br />
</p>

<p>
<a href="#" class="new-button" id="webstore-confirm-cancel"><b>Cancel</b><i></i></a>
</p>

<div class="clear"></div>

         */

        StickerProduct stickerProduct = StickerManager.getInstance().getCatalogueList().stream().filter(product -> product.getId() == widgetId).findFirst().orElse(null);

        if (stickerProduct == null) {
            webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            return;
        }

        if (stickerProduct.getType() != StickerType.BACKGROUND) {
            webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            return;
        }


        PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));

        if (playerDetails.getCredits() < stickerProduct.getPrice()) {
            webConnection.send("");
            return;
        }

        for (int i = 0; i < stickerProduct.getAmount(); i++) {
            WidgetDao.purchaseWidget(userId, 0, 0, 0, 0, stickerProduct.getId(), "", 0, false);
        }

        CurrencyDao.decreaseCredits(playerDetails, stickerProduct.getPrice());

        RconUtil.sendCommand(RconHeader.REFRESH_CREDITS, new HashMap<>() {{
            put("userId", playerDetails.getId());
        }});

        webConnection.send("OK");
    }

    public static void purchaseStickers(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        int widgetId = webConnection.post().getInt("selectedId");

        StickerProduct stickerProduct = StickerManager.getInstance().getCatalogueList().stream().filter(product -> product.getId() == widgetId).findFirst().orElse(null);

        if (stickerProduct == null) {
            webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            return;
        }

        if (stickerProduct.getType() != StickerType.STICKER) {
            webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            return;
        }

        PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));

        if (playerDetails.getCredits() < stickerProduct.getPrice()) {
            webConnection.send("");
            return;
        }

        for (int i = 0; i < stickerProduct.getAmount(); i++) {
            WidgetDao.purchaseWidget(userId, 0, 0, 0, 0, stickerProduct.getId(), "", 0, false);
        }

        //         if (type.equalsIgnoreCase("stickers")) {
        //            typeId = 1;
        //        }
        //
        //        if (type.equalsIgnoreCase("backgrounds")) {
        //            typeId = 4;
        //        }
        //
        //        if (type.equalsIgnoreCase("notes")) {
        //            typeId = 3;
        //        }

        CurrencyDao.decreaseCredits(playerDetails, stickerProduct.getPrice());

        RconUtil.sendCommand(RconHeader.REFRESH_CREDITS, new HashMap<>() {{
            put("userId", playerDetails.getId());
        }});

        webConnection.send("OK");
    }

    public static void purchaseStickieNotes(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        int widgetId = webConnection.post().getInt("selectedId");

        StickerProduct stickerProduct = StickerManager.getInstance().getCatalogueList().stream().filter(product -> product.getId() == widgetId).findFirst().orElse(null);

        if (stickerProduct == null) {
            webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            return;
        }

        if (stickerProduct.getType() != StickerType.NOTE) {
            webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            return;
        }

        PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));

        if (playerDetails.getCredits() < stickerProduct.getPrice()) {
            webConnection.send("");
            return;
        }


        for (int i = 0; i < stickerProduct.getAmount(); i++) {
            WidgetDao.purchaseWidget(userId, 0, 0, 0, 0, stickerProduct.getId(), "", 0, false);
        }

        CurrencyDao.decreaseCredits(playerDetails, stickerProduct.getPrice());

        RconUtil.sendCommand(RconHeader.REFRESH_CREDITS, new HashMap<>() {{
            put("userId", playerDetails.getId());
        }});

        webConnection.send("OK");
    }

}
