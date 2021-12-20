package org.alexdev.kepler.game.player.register;

public class RegisterValue {
    private int id;
    private String label;
    private RegisterDataType dataType;
    private String value;
    private boolean flag;

    public RegisterValue(String label, int id, RegisterDataType dataType) {
        this.label = label;
        this.id = id;
        this.dataType = dataType;
    }

    public RegisterDataType getDataType() {
        return dataType;
    }

    public int getId() {
        return id;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }
}
