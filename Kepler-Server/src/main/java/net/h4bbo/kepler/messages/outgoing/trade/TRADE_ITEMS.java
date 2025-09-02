package net.h4bbo.kepler.messages.outgoing.trade;

import net.h4bbo.kepler.game.inventory.Inventory;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class TRADE_ITEMS extends MessageComposer {
    private final Player player;
    private final List<Item> ownItems;
    private final boolean playerAcceptedTrade;
    private final Player tradePartner;
    private final List<Item> partnerItems;
    private final boolean partnerAcceptedTrade;

    public TRADE_ITEMS(Player player, List<Item> ownItems, boolean playerAcceptedTrade, Player tradePartner, List<Item> partnerItems, boolean partnerAcceptedTrade) {
        this.player = player;
        this.ownItems = ownItems;
        this.playerAcceptedTrade = playerAcceptedTrade;
        this.tradePartner = tradePartner;
        this.partnerItems = partnerItems;
        this.partnerAcceptedTrade = partnerAcceptedTrade;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeDelimeter(this.player.getDetails().getName(), (char) 9);
        response.writeDelimeter(this.playerAcceptedTrade ? "true" : "false", (char) 9);

        for (int i = 0; i < this.ownItems.size(); i++) {
            Item item = this.ownItems.get(i);
            Inventory.serialise(response, item, i);
        }

        response.write((char) 13);

        response.writeDelimeter(this.tradePartner.getDetails().getName(), (char) 9);
        response.writeDelimeter(this.partnerAcceptedTrade ? "true" : "false", (char) 9);

        for (int i = 0; i < this.partnerItems.size(); i++) {
            Item item = this.partnerItems.get(i);
            Inventory.serialise(response, item, i);
        }
    }

    @Override
    public short getHeader() {
        return 108; // "Al"
    }
}
