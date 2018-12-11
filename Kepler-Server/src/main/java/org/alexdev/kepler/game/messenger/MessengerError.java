package org.alexdev.kepler.game.messenger;

public class MessengerError {
    private MessengerUser causer;
    private final MessengerErrorType error;
    private final MessengerErrorReason reason;

    public MessengerError(MessengerErrorType error) {
        this.error = error;
        this.reason = null;
    }

    public MessengerError(MessengerErrorType error, MessengerErrorReason reason) {
        this.error = error;
        this.reason = reason;
    }

    public void setCauser(MessengerUser causer) {
        this.causer = causer;
    }

    public MessengerUser getCauser() {
        return this.causer;
    }

    public MessengerErrorType getErrorType() {
        return error;
    }

    public MessengerErrorReason getErrorReason() {
        return reason;
    }
}