package org.coode.pattern.valuepartition.ui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 7, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public class PieChart extends JComponent {

    private static final int BORDER_SIZE = 5;
    private static final int SHADOW_SIZE = 6;
    private static final Color SHADOW_COLOR = Color.lightGray;

    private static Color[] colours = null;

    private int colourIndex = 0;

    private List<PieValue> slices = new ArrayList<PieValue>();

    public PieChart() {
        setBackground(Color.WHITE);
        setOpaque(true);
        if (colours == null){
            colours = new Color[200];
            List<Integer> elements = new ArrayList<Integer>();
            elements.add(0);
            elements.add(0);
            elements.add(0);
            for (int i=0; i<100; i++){
                elements.set(0, (int)(Math.random()*255));
                elements.set(1, (int)(Math.random()*255));
                elements.set(2, elements.get(0) + elements.get(1) < 255 ? 125 + (int)(Math.random()*130) : 0);
                Collections.shuffle(elements);
                colours[i] = new Color(elements.get(0), elements.get(1), elements.get(2));
            }
        }
    }

    public void addValue(double value, Color color, String label) {
        slices.add(new PieValue(value, color, label));
    }

    public void addValue(double value, String label) {
        slices.add(new PieValue(value, nextColour(), label));
    }

    public void clearValues() {
        slices.clear();
        colourIndex = 0;
    }

    private Color nextColour() {
        Color c = colours[colourIndex++];
        if (colourIndex >= colours.length) {
            colourIndex = 0;
        }
        return c;
    }

    // This method is called whenever the contents needs to be painted
    public void paint(Graphics g) {
        super.paint(g);
        // Draw the pie
        Rectangle bounds = getBounds();

        g.setColor(getBackground());
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        // turn on antialiasing
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                         RenderingHints.VALUE_ANTIALIAS_ON);

        drawPie((Graphics2D) g, bounds, slices);
    }

    // slices is an array of values that represent the size of each slice.
    public void drawPie(Graphics2D g, Rectangle area, List<PieValue> slices) {

        int diameter = Math.min(area.width, area.height) - (2*BORDER_SIZE) - SHADOW_SIZE;
        area = new Rectangle(area.x + BORDER_SIZE,
                             area.y + BORDER_SIZE,
                             diameter,
                             diameter);

        Map<PieValue, Point> labelPoints = new HashMap<PieValue, Point>();
        Point middle = new Point(area.x + (area.width / 2), area.y + (area.height / 2));
        FontMetrics fMetrics = g.getFontMetrics();
        int distFromCentre = (area.width / 2) - fMetrics.getHeight();

        g.setColor(SHADOW_COLOR);
        g.fillOval(area.x + SHADOW_SIZE,  area.y + SHADOW_SIZE, area.width, area.height);

        // Get total value of all slices
        double total = 0.0D;
        for (PieValue slice : slices) {
            total += slice.value;
        }

        // Draw each pie slice
        double curValue = 0.0D;
        int startAngle = 0;
        for (PieValue slice : slices) {
            // Compute the start and stop angles
            startAngle = (int) (curValue * 360 / total);
            int arcAngle = (int) (slice.value * 360 / total);

            // Ensure that rounding errors do not leave a gap between the first and last slice
            if (slices.get(slices.size() - 1) == slice) {
                arcAngle = 360 - startAngle;
            }

            // Set the color and draw a filled arc
            g.setColor(slice.color);
            g.fillArc(area.x, area.y, area.width, area.height, startAngle, arcAngle);

            // calculate label positions
            double halfAngle = Math.toRadians(startAngle + (arcAngle / 2.0));
            int x = middle.x + (int) (distFromCentre * Math.cos(halfAngle));
            int y = middle.y + (int) (distFromCentre * Math.sin(halfAngle));
            labelPoints.put(slice,  new Point(x, y));

            curValue += slice.value;
        }

        // draw the labels
        g.setColor(Color.BLACK);
        for (PieValue slice : slices){
            Point p = labelPoints.get(slice);
//            int halfLabelWidth = (int)fMetrics.getStringBounds(slice.label, g).getWidth() / 2; // centre text
            g.drawString(slice.label, p.x /*- halfLabelWidth*/, p.y);
        }

        g.setStroke(new BasicStroke(2));
        g.setColor(Color.BLACK);
        g.drawOval(area.x, area.y, area.width, area.height);
    }

    // Class to hold a value for a slice
    class PieValue {
        double value;
        Color color;
        String label;

        public PieValue(double value, Color color, String label) {
            this.value = value;
            this.color = color;
            this.label = label;
        }
    }
}


