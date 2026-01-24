package net.h4bbo.http.kepler.util.piechart;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

public class PieChart {

    private BufferedImage image;
    private Graphics2D g2d;
    private List<Slice> slices;

    public PieChart(BufferedImage image, List<Slice> slices) {
        this.image = image;
        this.g2d = (Graphics2D)image.getGraphics();
        this.slices = slices;


        this.slices.sort(Comparator.comparingDouble(Slice::getValue));

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        drawPie(this.g2d, new Rectangle(image.getWidth() / 2, image.getHeight()));
        drawLegend();

        this.g2d.dispose();
    }

    private void drawPie(Graphics2D g, Rectangle area) {
        double total = 0.0D;

        for (Slice slice : this.slices) {
            total += slice.getValue();
        }

        double curValue = 0;
        int startAngle;

        for (Slice slice : this.slices) {
            startAngle = (int) (curValue * 361 / total);
            int arcAngle = (int) (slice.getValue() * 361 / total);

            g.setColor(slice.getColor());
            g.fillArc((int)area.getX(), (int)area.getY(), (int)area.getWidth(), (int)area.getHeight(), startAngle, arcAngle);

            curValue += slice.getValue();
        }
    }

    private void drawLegend() {
        int heightBetweenText = 20;

        for (Slice slice : slices) {
            FontMetrics fm = g2d.getFontMetrics();

            int x = (this.image.getWidth() / 2) + 10;
            int y = fm.getHeight() + heightBetweenText;

            g2d.setPaint(Color.black);
            g2d.setFont(new Font("Arial", Font.PLAIN, 15));
            g2d.drawString(slice.getLabel(), x + 10, y + 6);

            for (int sqX = x; sqX < x + 5; sqX++) {
                for (int sqY = y; sqY < y + 5; sqY++) {
                    image.setRGB(sqX, sqY, slice.getColor().getRGB());
                }
            }

            heightBetweenText += 20;
        }
    }
}