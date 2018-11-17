package org.alexdev.kepler.game.messenger;

public enum MessengerErrorType {
    TARGET_FRIEND_LIST_FULL(2),
    TARGET_DOES_NOT_ACCEPT(3),
    FRIEND_REQUEST_NOT_FOUND(4),
    BUDDYREMOVE_ERROR(37),
    FRIENDLIST_FULL(39),
    CONCURRENCY_ERROR(42);

    private final int errorCode;

    MessengerErrorType(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}