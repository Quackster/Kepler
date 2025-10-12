package org.alexdev.kepler.messages.incoming.bulletin;

import org.alexdev.kepler.game.bulletin.Article;
import org.alexdev.kepler.game.bulletin.BulletinManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.bulletin.ARTICLE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_ARTICLE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        final int articleId = reader.readInt();
        final Article article = BulletinManager.getInstance().getArticle(articleId);
        if (article == null) {
            return;
        }

        player.send(new ARTICLE(article));
    }
}
