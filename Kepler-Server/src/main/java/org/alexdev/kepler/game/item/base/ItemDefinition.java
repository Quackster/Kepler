package org.alexdev.kepler.game.item.base;

import org.alexdev.kepler.game.texts.TextsManager;

import java.util.ArrayList;
import java.util.List;

public class ItemDefinition {
    public static final double DEFAULT_TOP_HEIGHT = 0.001;

    private int id;
    private String sprite;
    private String behaviourData;
    private double topHeight;
    private int length;
    private int width;
    private String colour;
    private List<ItemBehaviour> behaviourList;

    public ItemDefinition() {
        this.sprite = "";
        this.behaviourData = "";
        this.colour = "";
        this.topHeight = DEFAULT_TOP_HEIGHT;
        this.length = 1;
        this.width = 1;
        this.behaviourList = new ArrayList<>();
    }

    public ItemDefinition(int id, String sprite, String behaviourData, double topHeight, int length, int width, String colour) {
        this.id = id;
        this.sprite = sprite;
        this.behaviourData = behaviourData;
        this.topHeight = topHeight;
        this.length = length;
        this.width = width;
        this.colour = colour;
        this.behaviourList = parseBehaviour(this.behaviourData);

        // If the item is a gate (checked below) then the top height is set to 0 so the item can be walked in
        if (!this.behaviourList.contains(ItemBehaviour.CAN_SIT_ON_TOP)
                && !this.behaviourList.contains(ItemBehaviour.CAN_LAY_ON_TOP)
                && !this.behaviourList.contains(ItemBehaviour.CAN_STACK_ON_TOP)) {
            this.topHeight = 0;
        }

        // If the top height 0, then make it 0.001 to make it taller than the default room tile, that the
        // furni collision map can be generated.
        if (this.topHeight == 0) {
            this.topHeight = DEFAULT_TOP_HEIGHT;
        }
    }

    /**
     * Parse the behaviour list seperated by comma.
     *
     * @param behaviourData the behaviourData to parse
     * @return the behaviour list
     */
    private static List<ItemBehaviour> parseBehaviour(String behaviourData) {
        List<ItemBehaviour> behaviourList = new ArrayList<>();

        if (behaviourData.length() > 0) {
            for (String behaviourEnum : behaviourData.split(",")) {
                behaviourList.add(ItemBehaviour.valueOf(behaviourEnum.toUpperCase()));
            }
        }

        return behaviourList;
    }

    /**
     * Get if the item has a type of behaviour.
     *
     * @param behaviour the behaviour to check
     * @return true, if successful
     */
    public boolean hasBehaviour(ItemBehaviour behaviour) {
        return this.behaviourList.contains(behaviour);
    }

    /**
     * Add a behaviour to the list.
     *
     * @param behaviour the behaviour to add
     */
    public void addBehaviour(ItemBehaviour behaviour) {
        if (this.behaviourList.contains(behaviour)) {
            return;
        }

        this.behaviourList.add(behaviour);
    }

    /**
     * Remove a behaviour from the list.
     *
     * @param behaviour the behaviour to remove
     */
    public void removeBehaviour(ItemBehaviour behaviour) {
        this.behaviourList.remove(behaviour);
    }

    /**
     * Get the item name by creating an external text key and reading external text entries.
     *
     * @param specialSpriteId the special sprite id
     * @return the name
     */
    public String getName(int specialSpriteId) {
        if (this.hasBehaviour(ItemBehaviour.DECORATION)) {
            return this.sprite;
        }

        String etxernalTextKey = this.getExternalTextKey(specialSpriteId);
        String name = etxernalTextKey + "_name";

        String value = TextsManager.getInstance().getValue(etxernalTextKey);

        if (value == null) {
            return "null";
        }

        return name;
    }

    /**
     * Get the item description by creating an external text key and reading external text entries.
     *
     * @param specialSpriteId the special sprite id
     * @return the description
     */
    public String getDescription(int specialSpriteId) {
        if (this.hasBehaviour(ItemBehaviour.CAN_STACK_ON_TOP)) {
            return this.sprite;
        }

        String etxernalTextKey = this.getExternalTextKey(specialSpriteId);
        String name = etxernalTextKey + "_desc";

        String value = TextsManager.getInstance().getValue(etxernalTextKey);

        if (value == null) {
            return "null";
        }

        return name;
    }

    /**
     * Create the catalogue icon through using the special sprite id.
     *
     * @param specialSpriteId the special sprite id
     * @return the catalogue icon
     */
    public String getIcon(int specialSpriteId) {
        String icon = "";

        icon += this.sprite;

        if (specialSpriteId > 0) {
            icon += " " + specialSpriteId;
        }

        return icon;
    }

    /**
     * Get external text key by definition.
     *
     * @param specialSpriteId the special sprite id
     * @return the external text key
     */
    private String getExternalTextKey(int specialSpriteId) {
        String key = "";

        if (specialSpriteId == 0) {
            if (this.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
                key = "wallitem";
            } else {
                key = "furni";
            }

            key += "_";
        }

        key += this.sprite;

        if (specialSpriteId > 0) {
            key += ("_" + specialSpriteId);
        }

        return key;
    }

    public int getId() {
        return id;
    }

    public String getSprite() {
        return sprite;
    }

    public void setSprite(String sprite) {
        this.sprite = sprite;
    }

    public double getTopHeight() {
        return topHeight;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public String getColour() {
        return colour;
    }

    public void setTopHeight(double topHeight) {
        this.topHeight = topHeight;
    }

    public String getBehaviourData() {
        return behaviourData;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public List<ItemBehaviour> getBehaviourList() {
        return behaviourList;
    }
}
