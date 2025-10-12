package org.alexdev.kepler.game.bulletin;

import java.util.List;

public class Article {

    private final int id;
    private final String title;
    private final String description;
    private final String icon;
    private final String color;
    private final String date;
    private final List<ArticleChunk> chunks;

    public Article(int id, String title, String description, String icon, String color, String date, List<ArticleChunk> chunks) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.color = color;
        this.date = date;
        this.chunks = chunks;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }

    public String getDate() {
        return date;
    }

    public List<ArticleChunk> getChunks() {
        return chunks;
    }
}
