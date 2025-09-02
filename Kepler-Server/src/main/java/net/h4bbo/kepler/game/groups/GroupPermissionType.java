package net.h4bbo.kepler.game.groups;

public enum GroupPermissionType {
    ADMIN_ONLY(2),
    MEMBER_ONLY(1),
    EVERYONE(0);

    private final int id;

    GroupPermissionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static GroupPermissionType getById(int id) {
        for (var forumType : values()) {
            if (forumType.getId() == id) {
                return forumType;
            }
        }

        return null;
    }
}
