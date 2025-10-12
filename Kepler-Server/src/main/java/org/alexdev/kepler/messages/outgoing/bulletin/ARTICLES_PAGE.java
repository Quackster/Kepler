package org.alexdev.kepler.messages.outgoing.bulletin;

import org.alexdev.kepler.game.bulletin.Article;
import org.alexdev.kepler.game.bulletin.BulletinManager;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ARTICLES_PAGE extends MessageComposer {
    private final int pageId;
    private final int totalPages;
    private final List<Article> pageArticles;

    public ARTICLES_PAGE(int pageId) {
        // TODO: Use a database AND don't hold all articles in memory
        final HashMap<Integer, Article> articles = BulletinManager.getInstance().getArticles();

        this.pageId = pageId;
        this.totalPages = (int) Math.ceil(articles.size() / 3.0);
        this.pageArticles = articles.values().stream().sorted(Comparator.comparingInt(Article::getId).reversed()).skip((pageId - 1) * 3L).limit(3).toList();
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.pageId); // pageId
        response.writeInt(this.totalPages); // totalPages
        response.writeInt(this.pageArticles.size()); // totalArticles

        // Get paginated articles, 3 per page.
        for (Article article : this.pageArticles) {
            response.writeInt(article.getId());
            response.writeString(article.getIcon());
            response.writeString(article.getTitle());
            response.writeString(article.getColor());
            response.writeString(article.getDate());
            response.writeString(article.getDescription());
        }
    }

    @Override
    public short getHeader() {
        return 681;
    }
}
