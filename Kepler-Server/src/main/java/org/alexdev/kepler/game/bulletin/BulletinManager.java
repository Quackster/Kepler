package org.alexdev.kepler.game.bulletin;

import org.alexdev.kepler.game.infostand.InfoStandManager;

import java.util.Arrays;
import java.util.HashMap;

public class BulletinManager {
    private static BulletinManager instance;

    private final HashMap<Integer, Article> articles;

    private BulletinManager() {
        this.articles = new HashMap<>();

        this.articles.put(1002, new Article(
                1002,
                "Welcome to Kepler: Origin",
                "This is a fork of Kepler, modified to work with the Habbo Hotel: Origin client. Created by Mikee (Unfamiliar @ Ragezone).",
                "generic7",
                "#00000",
                "2025-10-10",
                Arrays.asList(
                        new ArticleChunk("", "", ArticleChunkAlignment.LEFT, "[size=18][color=AC7000]Welcome to Kepler: Origin![/color][/size][br][br]This is a small example article.[br]You can use [b]bb code[/b] here.[br]:)"),
                        new ArticleChunk("", "", ArticleChunkAlignment.RIGHT, "This is another chunk"),
                        new ArticleChunk("", "", ArticleChunkAlignment.CENTER, "This is yet another chunk")
                ))
        );

        this.articles.put(1001, new Article(
                1001,
                "Placeholder article #1001",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                "generic1",
                "#00000",
                "2025-10-9",
                Arrays.asList(
                        new ArticleChunk("", "", ArticleChunkAlignment.LEFT, "[size=18][color=AC7000]Placeholder article #1001[/color][/size][br][br]This is a small example article.[br]You can use [b]bb code[/b] here.[br]:)"),
                        new ArticleChunk("", "", ArticleChunkAlignment.RIGHT, "This is another chunk"),
                        new ArticleChunk("", "", ArticleChunkAlignment.CENTER, "This is yet another chunk")
                ))
        );

        this.articles.put(1000, new Article(
                1000,
                "Placeholder article #1000",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                "generic14",
                "#00000",
                "2025-10-8",
                Arrays.asList(
                        new ArticleChunk("", "", ArticleChunkAlignment.LEFT, "[size=18][color=AC7000]Placeholder article #1000[/color][/size][br][br]This is a small example article.[br]You can use [b]bb code[/b] here.[br]:)"),
                        new ArticleChunk("", "", ArticleChunkAlignment.RIGHT, "This is another chunk"),
                        new ArticleChunk("", "", ArticleChunkAlignment.CENTER, "This is yet another chunk")
                ))
        );

        this.articles.put(999, new Article(
                999,
                "Placeholder article #999",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                "generic14",
                "#00000",
                "2025-10-7",
                Arrays.asList(
                        new ArticleChunk("", "", ArticleChunkAlignment.LEFT, "[size=18][color=AC7000]Placeholder article #999[/color][/size][br][br]This is a small example article. You can use [b]bb code[/b] here.[br]:)"),
                        new ArticleChunk("", "", ArticleChunkAlignment.RIGHT, "This is another chunk"),
                        new ArticleChunk("", "", ArticleChunkAlignment.CENTER, "This is yet another chunk")
                ))
        );
    }

    public HashMap<Integer, Article> getArticles() {
        return articles;
    }

    public Article getArticle(int articleId) {
        return this.articles.get(articleId);
    }

    /**
     * Get the {@link InfoStandManager} instance
     *
     * @return the infostand shop manager instance
     */
    public static BulletinManager getInstance() {
        if (instance == null) {
            instance = new BulletinManager();
        }

        return instance;
    }
}
