package net.h4bbo.kepler.game.misc.figure;

import java.util.ArrayList;
import java.util.List;

public class FigureSet {
    private String type;
    private String id;
    private String gender;
    private boolean isClub;
    private boolean isColorable;
    private boolean isSelectable;
    private List<FigurePart> figureParts;

    public FigureSet(String type, String id, String gender, boolean isClub, boolean isColorable, boolean isSelectable) {
        this.type = type;
        this.id = id;
        this.gender = gender;
        this.isClub = isClub;
        this.isColorable = isColorable;
        this.isSelectable = isSelectable;
        this.figureParts = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getGender() {
        return gender;
    }

    public boolean isClub() {
        return isClub;
    }

    public boolean isColorable() {
        return isColorable;
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public List<FigurePart> getFigureParts() {
        return figureParts;
    }
}
