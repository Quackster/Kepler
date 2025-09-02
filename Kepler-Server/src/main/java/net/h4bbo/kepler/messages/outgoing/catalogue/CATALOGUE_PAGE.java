package net.h4bbo.kepler.messages.outgoing.catalogue;

import net.h4bbo.kepler.game.catalogue.CatalogueItem;
import net.h4bbo.kepler.game.catalogue.CataloguePackage;
import net.h4bbo.kepler.game.catalogue.CataloguePage;
import net.h4bbo.kepler.game.item.base.ItemBehaviour;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;
import net.h4bbo.kepler.util.StringUtil;

import java.util.List;

public class CATALOGUE_PAGE extends MessageComposer {
    private final List<CatalogueItem> catalogueItems;
    private final CataloguePage page;

    public CATALOGUE_PAGE(CataloguePage cataloguePage, List<CatalogueItem> cataloguePageItems) {
        this.page = cataloguePage;
        this.catalogueItems = cataloguePageItems;
    }

    @Override
    public void compose(NettyResponse response) {
        //Ai:Recycler{13}n:Recycler{13}l:ctlg_recycler{13}g:catalog_recycler_headline1{13}
        response.writeKeyValue("i", this.page.getNameIndex());
        response.writeKeyValue("n", this.page.getName());
        response.writeKeyValue("l", this.page.getLayout());
        response.writeKeyValue("g", this.page.getImageHeadline());

        if (this.page.getImageTeasers() != null && this.page.getImageTeasers().length() > 0) {
            response.writeKeyValue("e", this.page.getImageTeasers());
        }

        if (!StringUtil.isNullOrEmpty(this.page.getBody())) {
            response.writeKeyValue("h", this.page.getBody());
        }

        if (this.page.getLinkList().length() > 0) {
            response.writeKeyValue("u", this.page.getLinkList());
        }

        if (!StringUtil.isNullOrEmpty(this.page.getLabelPick())) {
            response.writeKeyValue("w", this.page.getLabelPick());
        }

        if (!StringUtil.isNullOrEmpty(this.page.getLabelExtra_s())) {
            response.writeKeyValue("s", this.page.getLabelExtra_s());
        }

        for (int labelDataId = 1; labelDataId < 11; labelDataId++) {
            String extraDataId = "label_extra_t_" + labelDataId;

            if (!this.page.getLabelExtra().containsKey(extraDataId)) {
                continue;
            }

            response.writeKeyValue("t" + labelDataId, this.page.getLabelExtra().get(extraDataId));
        }

        for (CatalogueItem item : this.catalogueItems) {
            response.write("p:");
            response.writeDelimeter(item.getName(), (char) 9);
            response.writeDelimeter(item.getDescription(), (char) 9);
            response.writeDelimeter(item.getPrice(), (char) 9);
            response.writeDelimeter("", (char) 9);
            response.writeDelimeter(item.getType(), (char) 9);
            response.writeDelimeter(item.getIcon(), (char) 9);
            response.writeDelimeter(item.getSize(), (char) 9);
            response.writeDelimeter(item.getDimensions(), (char) 9);
            response.writeDelimeter(item.getSaleCode(), (char) 9);

            if (item.isPackage() || item.getDefinition().getSprite().equals("poster")) {
                response.writeDelimeter("", (char) 9);
            }

            if (item.isPackage()) {
                response.writeDelimeter(item.getPackages().size(), (char) 9);

                for (CataloguePackage cataloguePackage : item.getPackages()) {
                    response.writeDelimeter(cataloguePackage.getDefinition().getIcon(cataloguePackage.getSpecialSpriteId()), (char) 9);
                    response.writeDelimeter(cataloguePackage.getAmount(), (char) 9);
                    response.writeDelimeter(cataloguePackage.getDefinition().getColour(), (char) 9);
                }

            } else if (!item.getDefinition().hasBehaviour(ItemBehaviour.WALL_ITEM)) {
                response.writeDelimeter(item.getDefinition().getColour(), (char) 9);
            }

            response.write(Character.toString((char) 13));
        }
    }

    @Override
    public short getHeader() {
        return 127; // "A"
    }
}
