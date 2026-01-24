package net.h4bbo.http.kepler.game.stickers;

public class StickerCategory {
    public static final int BACKGROUND_CATEGORY_TYPE = 2;
    public static final int STICKER_BACKGROUND_TYPE = 1;

    private final int id;
    private final String name;
    private final int minRank;
    private final int categoryType;

    public StickerCategory(int id, String name, int minRank, int categoryType) {
        this.id = id;
        this.name = name;
        this.minRank = minRank;
        this.categoryType = categoryType;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMinRank() {
        return minRank;
    }

    public int getCategoryType() {
        return categoryType;
    }
}
