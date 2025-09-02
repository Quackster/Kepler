package net.h4bbo.http.kepler.util;

import net.h4bbo.kepler.util.config.GameConfiguration;

public class BBCode {
    public static String format(String message, boolean allowImages) {
        message = parse(message, allowImages);
        String siteContentPath = GameConfiguration.getInstance().getString("static.content.path");
        /*message = message.replace(":)", " <img src='" + siteContentPath + "/web-gallery/smilies/smile.gif' alt='Smiley' title='Smiley' border='0'> ");
        message = message.replace(";)", " <img src='" + siteContentPath + "/web-gallery/smilies/wink.gif' alt='Smiley' title='Smiley' border='0'> ");
        message = message.replace(":P", " <img src='" + siteContentPath + "/web-gallery/smilies/tongue.gif' alt='Smiley' title='Smiley' border='0'> ");
        message = message.replace(";P", " <img src='" + siteContentPath + "/web-gallery/smilies/winktongue.gif' alt='Smiley' title='Smiley' border='0'> ");
        message = message.replace(":p", " <img src='" + siteContentPath + "/web-gallery/smilies/tongue.gif' alt='Smiley' title='Smiley' border='0'> ");
        message = message.replace(";p", " <img src='" + siteContentPath + "/web-gallery/smilies/winktonue.gif' alt='Smiley' title='Smiley' border='0'> ");
        message = message.replace("(L)", " <img src='" + siteContentPath + "/web-gallery/smilies/heart.gif' alt='Smiley' title='Smiley' border='0'> ");
        message = message.replace("(l)", " <img src='" + siteContentPath + "/web-gallery/smilies/heart.gif' alt='Smiley' title='Smiley' border='0'> ");
        message = message.replace(":o", " <img src='" + siteContentPath + "/web-gallery/smilies/shocked.gif' alt='Smiley' title='Smiley' border='0'> ");
        message = message.replace(":O", " <img src='" + siteContentPath + "/web-gallery/smilies/shocked.gif' alt='Smiley' title='Smiley' border='0'> ");*/
        return message;
    }

    public static String normalise(String message) {
        message = message.replaceAll("\r", "\n");
        message = message.replaceAll("\\[/quote]\n\n", "[/quote]");
        message = message.replaceAll("\\[/quote]\n", "[/quote]");
        /*message = message.replaceAll("\n\n", "\n");
        message = message.replaceAll("\n\n", "\n");*/
        message = message.replaceAll("\n", "[br]");
        return message;
    }

    private static String parse(String message, boolean allowImages) {
        String sitePath = GameConfiguration.getInstance().getString("site.path");

        if (message.contains("javascript:") ||
            message.contains("document.write")) {
            message = message.replace("javascript:", "");
            message = message.replace("document.write", "");
        }

        message = message.replaceAll("\\[b](.*?)\\[/b]", "<b>$1</b>");
        message = message.replaceAll("\\[i](.*?)\\[/i]", "<i>$1</i>");
        message = message.replaceAll("\\[u](.*?)\\[/u]", "<u>$1</u>");
        message = message.replaceAll("\\[s](.*?)\\[/s]", "<s>$1</s>");
        message = message.replaceAll("\\[strike](.*?)\\[/strike]", "<strike>$1</strike>");
        message = message.replaceAll("\\[link=(.*?)](.*?)\\[/link]", "<a href=\"$1\">$2</a>");
        message = message.replaceAll("\\[url=(.*?)](.*?)\\[/url]", "<a href=\"$1\">$2</a>");
        message = message.replaceAll("\\[color=(orange|red|yellow|green|cyan|blue|gray|black|white)](.*?)\\[/color]", "<font color=\"$1\">$2</font>");
        message = message.replaceAll("\\[color=(#[0-9a-fA-F]{6})](.*?)\\[/color]", "<font color=\"$1\">$2</font>");
        message = message.replaceAll("\\[size=small](.*?)\\[/size]", "<span style=\"font-size: 9px;\">$1</span>");
        message = message.replaceAll("\\[size=large](.*?)\\[/size]", "<span style=\"font-size: 14px;\">$1</span>");
        message = message.replaceAll("\\[code](.*?)\\[/code]", "<pre>$1</pre>");
        message = message.replaceAll("\\[habbo=(.*?)](.*?)\\[/habbo]", "<a href=\"" + sitePath + "/home/$1/id\">$2</a>");
        message = message.replaceAll("\\[room=(.*?)](.*?)\\[/room]", "<a onclick=\"roomForward(this, '$1', 'private'); return false;\" target=\"client\" href=\"" + sitePath + "/client?forwardId=2&roomId=$1\">$2</a>");
        message = message.replaceAll("\\[group=(.*?)](.*?)\\[/group]", "<a href=\"" + sitePath + "/groups/$1/id\">$2</a>");

        for (int i = 0; i < 10; i++) {
            message = message.replaceAll("\\[quote](.*?)\\[/quote]", "<div class=\"bbcode-quote\">$1</div>");
        }

        message = message.replaceAll("\\[br]", "<br>");

        if (allowImages) {
            message = message.replaceAll("\\[img=(.*?)](.*?)\\[/img]", "<img alt=\"$1\" src=\"$2\"/>");
            message = message.replaceAll("\\[img](.*?)\\[/img]", "<img src=\"$1\"/>");

            message = message.replaceAll("\\[img height='(.*?)' width='(.*?)'](.*?)\\[/img]", "<img height=\"$1\" width=\"$2\" src=\"$3\"/>");
            message = message.replaceAll("\\[img](.*?)\\[/img]", "<img src=\"$1\"/>");

            message = message.replaceAll("\\[article_images](.*?)\\[/article_images]", "<div class=\"article-images clearfix\">$1</div>");
            message = message.replaceAll("\\[article_image](.*?)\\[/article_image]", "<a href=\"$1\" style=\"background-image: url($1); background-position: -0px -0px\"></a>");
            message = message.replaceAll("\\[article_image x=(.*?) y=(.*?)](.*?)\\[/article_image]", "<a href=\"$3\" style=\"background-image: url($3); background-position: $1 $2\"></a>");
            message = message.replaceAll("\\[center](.*?)\\[/center]", "<center>$1</center>");
            message = message.replace("<br><br>", "<br>");
        }

        return message;
    }
}
