package net.h4bbo.kepler.game.item.base;

import net.h4bbo.kepler.game.item.interactors.InteractionType;

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
    private String name;
    private String description;
    private int[] drinkIds;
    private boolean isRecyclable;
    private InteractionType interactionType;

    public ItemDefinition() {
        this.sprite = "";
        this.behaviourData = "";
        this.colour = "";
        this.topHeight = DEFAULT_TOP_HEIGHT;
        this.length = 1;
        this.width = 1;
        this.behaviourList = new ArrayList<>();
        this.interactionType = null;
        this.drinkIds = new int[0];
    }

    public ItemDefinition(int id, String sprite, String name, String description, String behaviourData, String interactor, double topHeight, int length, int width, String colour, String drinkIdData, boolean isRecyclable) {
        this.id = id;
        this.sprite = sprite;
        this.name = name;
        this.description = description;
        this.behaviourData = behaviourData;
        this.interactionType = InteractionType.valueOf(interactor.toUpperCase());
        this.topHeight = topHeight;
        this.length = length;
        this.width = width;
        this.colour = colour;
        this.isRecyclable = isRecyclable;
        this.behaviourList = parseBehaviour(this.behaviourData);
        this.drinkIds = new int[drinkIdData.split(",").length];

        if (drinkIdData.length() > 0) {
            int i = 0;
            for (String data : drinkIdData.split(",")) {
                this.drinkIds[i++] = Integer.parseInt(data);
            }
        }

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

    public double getPositiveTopHeight() {
        if (this.topHeight < 0) {
            return DEFAULT_TOP_HEIGHT;
        }

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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    public int[] getDrinkIds() {
        return drinkIds;
    }

    public InteractionType getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(InteractionType interactionType) {
        this.interactionType = interactionType;
    }

    public boolean isRecyclable() {
        return isRecyclable;
    }

    public void setRecyclable(boolean recyclable) {
        isRecyclable = recyclable;
    }
}

