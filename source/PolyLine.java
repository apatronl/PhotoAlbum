/**
 * PolyLine
 * CS 4470 - User Interface Software
 * apl7@gatech.edu
 *
 * @author Alejandrina Patron Lopez
 * @version 1.0
 */

import java.awt.Graphics2D;
import java.awt.Color;
import java.util.*;
import java.awt.BasicStroke;

public class PolyLine implements Annotation {
    private ArrayList<Integer> xList;
    private ArrayList<Integer> yList;
    private ArrayList<Integer> prevXList;
    private ArrayList<Integer> prevYList;
    private Color inkColor;
    private boolean selected = false;

    /**
     * Creates a new PolyLine object.
     */
    public PolyLine() {
        xList = new ArrayList<Integer>();
        yList = new ArrayList<Integer>();
    }

    /**
     * Adds a point to the line at the given x and y.
     *
     * @param x
     * @param y
     */
    public void addPoint(int x, int y) {
        xList.add(x);
        yList.add(y);
    }

    /**
     * Get list of x coordinates.
     *
     * @return x coordinates
     */
    public ArrayList<Integer> getXList() {
        return xList;
    }

    /**
     * Get list of y coordinates.
     *
     * @return y coordinates
     */
    public ArrayList<Integer> getYList() {
        return yList;
    }

    /**
     * Sets line's color.
     *
     * @param inkColor
     */
    public void setInkColor(Color inkColor) {
        this.inkColor = inkColor;
    }

    /**
     * Set PolyLine's selected state.
     *
     * @param selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Check if PolyLine is selected.
     *
     * @return true if PolyLine is selected, false otherwise
     */
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void savePrevCoordinates() {
        this.prevXList = this.xList;
        this.prevYList = this.yList;
    }

    /**
     * Get line's previous x coordinates.
     *
     * @return prevXList
     */
    public ArrayList<Integer> getPrevX() {
        return prevXList;
    }

    /**
     * Get line's previous y coordinates.
     *
     * @return prevYList
     */
    public ArrayList<Integer> getPrevY() {
        return prevYList;
    }

    /**
     * Set line's new x and y coordinates.
     *
     * @param xList
     * @param yList
     */
    public void setCoordinates(ArrayList<Integer> xList, ArrayList<Integer> yList) {
        this.xList = xList;
        this.yList = yList;
    }

    @Override
    public void draw(Graphics2D g2, int xOffset, int yOffset) {
        for (int i = 0; i < xList.size() - 1; ++i) {
            g2.setColor(inkColor);
            g2.setStroke(new BasicStroke(3));
            g2.drawLine((int)xList.get(i) + xOffset,
                (int)yList.get(i) + yOffset, (int)xList.get(i + 1) + xOffset,
                (int)yList.get(i + 1) + yOffset);
        }
        g2.setColor(Color.BLACK);
    }
}
