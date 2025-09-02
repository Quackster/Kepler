package net.h4bbo.http.kepler.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

import net.h4bbo.kepler.util.config.GameConfiguration;

public class HtmlUtil {
    /**
     * Strip HTML tags.
     *
     * @param str the string to strip
     * @return the new string
     */
    public static String removeHtmlTags(String str) {
        return str.replaceAll("\\<[^>]*>","");
    }

    /**
     * Conver text to HTML.
     *
     * @param s the string to convert
     * @return the converted text
     */
    public static String escape(String s) {
        StringBuilder builder = new StringBuilder();
        boolean previousWasASpace = false;

        for (char c : s.toCharArray()) {
            if (c == ' ') {
                if (previousWasASpace) {
                    builder.append("&nbsp;");
                    previousWasASpace = false;
                    continue;
                }
                previousWasASpace = true;
            } else {
                previousWasASpace = false;
            }

            switch (c) {
                case '<':
                    builder.append("&lt;");
                    break;
                case '>':
                    builder.append("&gt;");
                    break;
                case '&':
                    builder.append("&amp;");
                    break;
                case '"':
                    builder.append("&quot;");
                    break;
                case '\n':
                    builder.append("<br>");
                    break;
                // We need Tab support here, because we print StackTraces as HTML
                case '\t':
                    builder.append("&nbsp; &nbsp; &nbsp;");
                    break;
                default:
                    if (c < 128) {
                        builder.append(c);
                    } else {
                        builder.append("&#").append((int) c).append(";");
                    }
            }
        }
        return builder.toString();
    }

    public static String createFigureLink(String figure, String sex) {
        return GameConfiguration.getInstance().getString("site.path") + "/habbo-imaging/avatarimage?figure=" + figure + "&size=s&direction=4&head_direction=4&crr=0&gesture=sml&frame=1";
    }

    public static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            imageString =  new String(Base64.encodeBase64(imageBytes));

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }

    public static String getResponse(String url) {
        InputStream in = null;
        String val = null;

        try {
            in = new URL(url).openStream();
            val = IOUtils.toString( in, Charset.defaultCharset());
        } catch (Exception ignored) {
            IOUtils.closeQuietly(in);
        }

        return val;
    }
}
