package net.h4bbo.http.kepler.controllers.habblet;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.game.catalogue.CatalogueItem;
import net.h4bbo.kepler.game.catalogue.voucher.VoucherManager;
import net.h4bbo.kepler.game.catalogue.voucher.VoucherRedeemMode;
import net.h4bbo.kepler.game.catalogue.voucher.VoucherRedeemStatus;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import net.h4bbo.http.kepler.util.RconUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class VoucherController {
    public static void redeemVoucher(WebConnection webConnection) {
        var tpl = webConnection.template("habblet/redeemvoucher");
        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        AtomicInteger redeemedCredits = new AtomicInteger(0);
        List<CatalogueItem> redeemedItems = new ArrayList<>();

        var voucherStatus = VoucherManager.getInstance().redeem(playerDetails, VoucherRedeemMode.IN_GAME, webConnection.post().getString("voucherCode"), redeemedItems, redeemedCredits);

        if (voucherStatus == VoucherRedeemStatus.FAILURE) {
            tpl.set("voucherResult", "error");
        }

        if (voucherStatus == VoucherRedeemStatus.FAILURE_NEW_ACCOUNT) {
            tpl.set("voucherResult", "too_new");
        }

        if (voucherStatus == VoucherRedeemStatus.SUCCESS) {
            tpl.set("voucherResult", "success");
        }

        if (redeemedItems.size() > 0) {
            RconUtil.sendCommand(RconHeader.REFRESH_HAND, new HashMap<>() {{
                put("userId", playerDetails.getId());
            }});
        }

        if (redeemedCredits.get() > 0) {
            RconUtil.sendCommand(RconHeader.REFRESH_CREDITS, new HashMap<>() {{
                put("userId", playerDetails.getId());
            }});
        }

        tpl.render();
    }
}
