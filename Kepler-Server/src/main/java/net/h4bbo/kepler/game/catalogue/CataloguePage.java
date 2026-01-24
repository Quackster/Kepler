package net.h4bbo.kepler.game.catalogue;

import net.h4bbo.kepler.game.player.PlayerRank;
import net.h4bbo.kepler.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class CataloguePage {
    private int id;
    private PlayerRank minRole;
    private boolean indexVisible;
    private boolean isClubOnly;
    private String nameIndex;
    private String linkList;
    private String name;
    private String layout;
    private String imageHeadline;
    private String imageTeasers;
    private String body;
    private String labelPick;
    private String labelExtra_s;
    private Map<String, String> labelExtra;

    public CataloguePage(int id, PlayerRank minRole, boolean indexVisible, boolean isClubOnly, String nameIndex, String linkedList, String name, String layout, String imageHeadline, String imageTeasers, String body, String labelPick, String labelExtra_s, String labelExtra_t) {
        this.id = id;
        this.minRole = minRole;
        this.indexVisible = indexVisible;
        this.isClubOnly = isClubOnly;
        this.nameIndex = nameIndex;
        this.linkList = linkedList;
        this.name = name;
        this.layout = layout;
        this.imageHeadline = imageHeadline;
        this.imageTeasers = imageTeasers;
        this.body = body;
        this.labelPick = labelPick;
        this.labelExtra_s = labelExtra_s;
        this.labelExtra = new HashMap<>();

        if (!StringUtil.isNullOrEmpty(labelExtra_t)) {
            String[] extraTypedData = labelExtra_t.split("\r\n");

            for (String dbExtraData : extraTypedData) {
                String extraData = StringUtil.filterInput(dbExtraData, true);

                String extraId = extraData.substring(0, extraData.indexOf(':'));
                String data = extraData.substring(extraId.length() + 1);

                this.labelExtra.put("label_extra_t_" + extraId, data);
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PlayerRank getMinRole() {
        return minRole;
    }

    public boolean isIndexVisible() {
        return indexVisible;
    }

    public String getNameIndex() {
        return nameIndex;
    }

    public String getLinkList() {
        return linkList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLayout() {
        return layout;
    }

    public String getImageHeadline() {
        return imageHeadline;
    }

    public String getImageTeasers() {
        return imageTeasers == null ? "" : imageTeasers;
    }

    public String getBody() {
        return body == null ? "" : body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLabelPick() {
        return labelPick;
    }

    public String getLabelExtra_s() {
        return labelExtra_s;
    }

    public Map<String, String> getLabelExtra() {
        return labelExtra;
    }

    public boolean isClubOnly() {
        return isClubOnly;
    }
}
