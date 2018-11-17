package org.alexdev.kepler.game.catalogue;

import org.alexdev.kepler.game.player.PlayerRank;
import org.alexdev.kepler.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class CataloguePage {
    private int id;
    private PlayerRank minRole;
    private boolean indexVisible;
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

    public CataloguePage(int id, PlayerRank minRole, boolean indexVisible, String nameIndex, String linkedList, String name, String layout, String imageHeadline, String imageTeasers, String body, String labelPick, String labelExtra_s, String labelExtra_t) {
        this.id = id;
        this.minRole = minRole;
        this.indexVisible = indexVisible;
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

    public void setMinRole(PlayerRank minRole) {
        this.minRole = minRole;
    }

    public boolean isIndexVisible() {
        return indexVisible;
    }

    public String getNameIndex() {
        return nameIndex;
    }

    public void setNameIndex(String nameIndex) {
        this.nameIndex = nameIndex;
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

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getImageHeadline() {
        return imageHeadline;
    }

    public void setImageHeadline(String imageHeadline) {
        this.imageHeadline = imageHeadline;
    }

    public String getImageTeasers() {
        return imageTeasers;
    }

    public void setImageTeasers(String imageTeasers) {
        this.imageTeasers = imageTeasers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLabelPick() {
        return labelPick;
    }

    public void setLabelPick(String labelPick) {
        this.labelPick = labelPick;
    }

    public String getLabelExtra_s() {
        return labelExtra_s;
    }

    public void setLabelExtra_s(String labelExtra_s) {
        this.labelExtra_s = labelExtra_s;
    }

    public Map<String, String> getLabelExtra() {
        return labelExtra;
    }
}
