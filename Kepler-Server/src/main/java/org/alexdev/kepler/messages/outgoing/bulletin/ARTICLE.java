package org.alexdev.kepler.messages.outgoing.bulletin;

import org.alexdev.kepler.game.bulletin.Article;
import org.alexdev.kepler.game.bulletin.ArticleChunk;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class ARTICLE extends MessageComposer {

    private final Article article;

    public ARTICLE(Article article) {
        this.article = article;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.article.getId());
        response.writeInt(this.article.getChunks().size());

        for (ArticleChunk chunk : this.article.getChunks()) {
            response.writeString(chunk.getMemberName());
            response.writeString(chunk.getExternalImageAddress());
            response.writeInt(chunk.getAlignment().getValue());
            response.writeString(chunk.getText());
        }
    }

    @Override
    public short getHeader() {
        return 682;
    }
}
