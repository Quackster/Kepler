package net.h4bbo.http.kepler.game.stickers;

public enum StickerType {
    STICKER(1),
    BACKGROUND(4),
    NOTE(3),
    HOME_WIDGET(2),
    GROUP_WIDGET(5);

    private final int typeId;

    StickerType(int typeId) {
        this.typeId = typeId;
    }

    public static StickerType getByType(int type) {
        for (StickerType stickerType : values()) {
            if (stickerType.getTypeId() == type) {
                return stickerType;
            }
        }

        return null;
    }

    public int getTypeId() {
        return typeId;
    }
}
