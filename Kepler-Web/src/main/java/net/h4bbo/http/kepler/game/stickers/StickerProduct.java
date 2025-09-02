package net.h4bbo.http.kepler.game.stickers;

public class StickerProduct {
    private final int id;
    private final String name;
    private final String description;
    private final int minRank;
    private final String data;
    private final int price;
    private final int amount;
    private final int categoryId;
    private final int widgetType;
    private final int type;

    public StickerProduct(int id, String name, String description, int minRank, String data, int price, int amount, int categoryId, int widgetType, int type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.minRank = minRank;
        this.data = data;
        this.price = price;
        this.amount = amount;
        this.categoryId = categoryId;
        this.type = type;
        this.widgetType = widgetType;
    }

    public String getCssClass() {
        if (this.type == 1) {
            return "s_" + this.data + "_pre";
        }

        if (this.type == 4) {
            return "b_" + this.data + "_pre";
        }

        if (this.type == 3) {
            return "commodity_" + this.data + "_pre";
        }

        if (this.type == StickerType.GROUP_WIDGET.getTypeId() || this.type == StickerType.HOME_WIDGET.getTypeId()) {
            return "w_" + this.data + "_pre";
        }

        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMinRank() {
        return minRank;
    }

    public String getData() {
        return data;
    }

    public int getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public StickerType getType() {
        return StickerType.getByType(this.type);
    }

    public boolean isProduct() {
        if (this.widgetType == 0) {
            return true;
        }

        return false;
    }

    public boolean isGroupWidget() {
        if (this.widgetType == -1) {
            return true;
        }

        return false;
    }

    public boolean isHomeWidget() {
        if (this.widgetType == 1) {
            return true;
        }

        return false;
    }
}
