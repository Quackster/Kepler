package org.alexdev.kepler.game.messenger;

public enum MessengerErrorReason {
    FRIENDLIST_FULL_PENDING_FRIEND(1),
    SENDER_FRIENDLIST_FULL(2),
    CONCURRENCY(42); // Requests refresh user console in client

    private final int reasonCode;

    MessengerErrorReason(int reason) {
        this.reasonCode = reason;
    }

    public int getReasonCode() {
        return this.reasonCode;
    }
}
