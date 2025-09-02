package net.h4bbo.http.kepler.game.news;

public class NewsCategory {
    private int id;
    private String label;
    private String index;

    public NewsCategory(int id, String label, String index) {
        this.id = id;
        this.label = label;
        this.index = index;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getIndex() {
        return index;
    }
}
