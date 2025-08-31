package org.alexdev.kepler.game.wordfilter;

public class WordfilterWord {
    private final String word;
    private final boolean isBannable;
    private final boolean isFilterable;

    public WordfilterWord(String word, boolean isBannable, boolean isFilterable) {
        this.word = word;
        this.isBannable = isBannable;
        this.isFilterable = isFilterable;
    }

    public String getWord() {
        return word;
    }

    public boolean isBannable() {
        return isBannable;
    }

    public boolean isFilterable() {
        return isFilterable;
    }
}
