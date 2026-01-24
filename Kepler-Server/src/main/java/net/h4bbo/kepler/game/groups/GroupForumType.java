package net.h4bbo.kepler.game.groups;

public enum GroupForumType {
    PUBLIC(0),
    PRIVATE(1);

    private final int id;

    GroupForumType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static GroupForumType getById(int id) {
        for (var forumType : values()) {
            if (forumType.getId() == id) {
                return forumType;
            }
        }

        return null;
    }
}
