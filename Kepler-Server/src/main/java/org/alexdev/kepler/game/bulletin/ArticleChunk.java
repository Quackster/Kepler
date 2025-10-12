package org.alexdev.kepler.game.bulletin;

public class ArticleChunk {

    private final String memberName;
    private final String externalImageAddress;
    private final ArticleChunkAlignment alignment;
    private final String text;

    public ArticleChunk(String memberName, String externalImageAddress, ArticleChunkAlignment alignment, String text) {
        this.memberName = memberName;
        this.externalImageAddress = externalImageAddress;
        this.alignment = alignment;
        this.text = text;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getExternalImageAddress() {
        return externalImageAddress;
    }

    public ArticleChunkAlignment getAlignment() {
        return alignment;
    }

    public String getText() {
        return text;
    }
}
