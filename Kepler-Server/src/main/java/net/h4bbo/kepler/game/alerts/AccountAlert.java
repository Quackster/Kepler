package net.h4bbo.kepler.game.alerts;

public class AccountAlert {
    private final int id;
    private final int userId;
    private final AlertType alertType;
    private final String message;
    private final boolean isDisabled;
    private final long createdAt;

    public AccountAlert(int id, int userId, AlertType alertType, String message, boolean isDisabled, long createdAt) {
        this.id = id;
        this.userId = userId;
        this.alertType = alertType;
        this.message = message;
        this.isDisabled = isDisabled;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public String getMessage() {
        return message;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
