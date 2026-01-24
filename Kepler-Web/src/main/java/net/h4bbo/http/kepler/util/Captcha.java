/*
Copyright 2009-2016 Igor Polevoy

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
*/
package net.h4bbo.http.kepler.util;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.apache.commons.codec.binary.Hex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This is a simple captcha class, use it to generate a random string and then to create an image of it.
 *
 * @author Igor Polevoy
 */
public class Captcha {
    private static boolean showGrid = false;
    private static int width = 200;
    private static int height = 50;
    private static int gridSize = 11;
    private static int fontSize = 45;
    private static int rotationAmplitude = 15;
    private static int scaleAmplitude = 15;

    public Captcha(){}

    public String createHash() {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        String str = generateText(32);

        messageDigest.reset();
        messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
        final byte[] resultByte = messageDigest.digest();
        final String result = new String(Hex.encodeHex(resultByte));
        return result;
    }

    /**
     * Generates a random alpha-numeric string of eight characters.
     *
     * @return random alpha-numeric string of eight characters.
     */
    public static String generateText(int length) {

        char[] data = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
                'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6',
                '7', '8', '9'};

        char[] index = new char[length - 1];

        Random r = new Random();
        int i = 0;

        for (i = 0; i < (index.length); i++) {
            int ran = r.nextInt(data.length);
            index[i] = data[ran];
        }
        return new String(index).toLowerCase();
    }

    /**
     * Generates a PNG image of text 180 pixels wide, 40 pixels high with white background.
     *
     * @param text expects string size eight (8) characters.
     * @return byte array that is a PNG image generated with text displayed.
     */
    public static byte[] generateImage(String text) {
        if (text == null || text.length() == 0) {
            throw new IllegalArgumentException("No captcha text given");
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setBackground(Color.WHITE);
        g2d.setColor(Color.BLACK);

        clearCanvas(g2d);

        if (showGrid) {
            drawGrid(g2d);
        }

        for(int i = 0; i < 8; i++) {
            int oldX = ThreadLocalRandom.current().nextInt(0, width);
            int oldY = ThreadLocalRandom.current().nextInt(0, height);

            int newX = ThreadLocalRandom.current().nextInt(0, height);
            int newY = ThreadLocalRandom.current().nextInt(0, height);

            g2d.drawLine(oldX, oldY, newX, newY);
        }

        int xPos = 20;

        for (char ch : text.toCharArray()) {
            int charMaxWidth = (width / text.length()) - ThreadLocalRandom.current().nextInt(0, 20);
            drawCharacter(g2d, ch, xPos, charMaxWidth);
            xPos += charMaxWidth;
        }

        g2d.dispose();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", bout);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bout.toByteArray();
    }

    private static void drawCharacter(Graphics2D g2d, char ch, int x, int boxWidth) {
        Font normalFont = new Font("Arial", Font.PLAIN, fontSize);
        Font boldFont = new Font("Arial", Font.BOLD, fontSize);

        double degree = (ThreadLocalRandom.current().nextDouble() * rotationAmplitude * 2) - rotationAmplitude;
        double scale = 1 - (ThreadLocalRandom.current().nextDouble() * scaleAmplitude / 100);

        Graphics2D cg2d = (Graphics2D) g2d.create();

        if (ThreadLocalRandom.current().nextBoolean()) {
            cg2d.setFont(normalFont);//fontSize));
        } else {
            cg2d.setFont(boldFont);
        }

        cg2d.translate(x + (boxWidth / 2), height / 2);
        cg2d.rotate(degree * Math.PI / 90);
        cg2d.scale(scale, scale);

        FontMetrics fm = cg2d.getFontMetrics();
        int charWidth = fm.charWidth(ch);
        int charHeight = fm.getAscent() + fm.getDescent();

        cg2d.drawString(String.valueOf(ch), -(charWidth / 2), fm.getAscent() - (charHeight / 2));

        cg2d.dispose();
    }

    /**
     * Clears the canvas.
     */
    private static void clearCanvas(Graphics2D g2d) {
        g2d.clearRect(0, 0, width, height);
    }

    /**
     * Draws the background grid.
     */
    private static void drawGrid(Graphics2D g2d) {
        for (int y = 2; y < height; y += gridSize) {
            g2d.drawLine(0, y, width - 1, y);
        }

        for (int x = 2; x < width; x += gridSize) {
            g2d.drawLine(x, 0, x, height -1);
        }
    }

    public static boolean matches(WebConnection webConnection, String captcha) {
        String captchaGenerated = webConnection.session().getString("captcha-text");

        if (captchaGenerated == null || captchaGenerated.isBlank())
            return false;

        return captchaGenerated.equals(captcha);
    }
}