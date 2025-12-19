package src;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GraphPanel extends JPanel {
    private String function;
    private ExpressionParser parser = new ExpressionParser();
    private double xMin = -10, xMax = 10, yMin = -10, yMax = 10;
    private boolean grid = true;

    public void setFunction(String f) { function = f; repaint(); }
    public void zoom(double f) { xMin*=f; xMax*=f; yMin*=f; yMax*=f; repaint(); }
    public void pan(double dx, double dy) { xMin+=dx; xMax+=dx; yMin+=dy; yMax+=dy; repaint(); }
    public void toggleGrid() { grid = !grid; repaint(); }

    public double getYIntercept() { return parser.evaluate(function, 0); }

    public ArrayList<Double> getXIntercepts() {
        ArrayList<Double> xs = new ArrayList<>();
        double step = (xMax - xMin) / 1000;
        double prevX = xMin;
        double prevY = parser.evaluate(function, prevX);
        for (double x = xMin; x <= xMax; x += step) {
            double y = parser.evaluate(function, x);
            if (prevY * y <= 0) xs.add(x);
            prevY = y;
        }
        return xs;
    }

    public double[] getRange() {
        double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
        for (double x = xMin; x <= xMax; x += 0.05) {
            double y = parser.evaluate(function, x);
            min = Math.min(min, y);
            max = Math.max(max, y);
        }
        return new double[]{min, max};
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (grid) drawGrid(g);
        drawAxes(g);
        if (function == null) return;
        g.setColor(Color.RED);
        int w = getWidth(), h = getHeight();
        double step = (xMax - xMin) / w;
        double prevX = xMin;
        double prevY = parser.evaluate(function, prevX);
        for (double x = xMin; x <= xMax; x += step) {
            double y = parser.evaluate(function, x);
            int x1 = (int)((prevX - xMin) / (xMax - xMin) * w);
            int y1 = (int)(h - (prevY - yMin) / (yMax - yMin) * h);
            int x2 = (int)((x - xMin) / (xMax - xMin) * w);
            int y2 = (int)(h - (y - yMin) / (yMax - yMin) * h);
            g.drawLine(x1, y1, x2, y2);
            prevX = x; prevY = y;
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        for (int i=0;i<getWidth();i+=50) g.drawLine(i,0,i,getHeight());
        for (int i=0;i<getHeight();i+=50) g.drawLine(0,i,getWidth(),i);
    }

    private void drawAxes(Graphics g) {
        g.setColor(Color.BLACK);
        int x0 = (int)((0 - xMin)/(xMax - xMin)*getWidth());
        int y0 = (int)(getHeight() - (0 - yMin)/(yMax - yMin)*getHeight());
        g.drawLine(x0,0,x0,getHeight());
        g.drawLine(0,y0,getWidth(),y0);
    }
}
