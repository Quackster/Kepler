package org.alexdev.kepler.messages.incoming.infostand;

import org.alexdev.kepler.dao.mysql.CurrencyDao;
import org.alexdev.kepler.dao.mysql.InfoStandDao;
import org.alexdev.kepler.game.infostand.InfoStandManager;
import org.alexdev.kepler.game.infostand.InfoStandShopEntry;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.infostand.AVAILABLE_INFO_PROPS;
import org.alexdev.kepler.messages.outgoing.user.currencies.STAMP_BALANCE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class PURCHASE_INFOSTAND_CUSTOMIZATION implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        final String productCode = reader.readString();

        final InfoStandShopEntry entry = InfoStandManager.getInstance().getShopProduct(productCode);
        if (entry == null) {
            return;
        }

        // Check if they already own it
        if (player.getInfoStand().ownsProp(entry.getType(), entry.getProductId())) {
            return;
        }

        // Charge the user
        switch (entry.getCurrency()) {
            case 0 -> {
                if (player.getDetails().getStamps() < entry.getPrice()) {
                    return;
                }

                CurrencyDao.decreaseStamps(player.getDetails(), entry.getPrice());
                player.send(new STAMP_BALANCE(player.getDetails().getStamps()));
            }
            default -> {
                Log.getErrorLogger().error("Unknown currency type: {} for infostand product: {}", entry.getCurrency(), entry.getProductCode());
                return;
            }
        }

        // Give the prop
        InfoStandDao.addUserProp(player.getInfoStand(), entry.getProductId(), entry.getType());

        // Sync props
        player.send(new AVAILABLE_INFO_PROPS(player.getInfoStand()));
    }
}
