/**
 * PostIt
 * CS 4470 - User Interface Software
 * apl7@gatech.edu
 *
 * @author Alejandrina Patron Lopez
 * @version 1.0
 */

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.FontMetrics;
import java.util.ArrayList;

public class PostIt implements Annotation {

    private int x, y;
    private int prevX, prevY;
    private int width, height;
    private ArrayList<String> text;
    private boolean selected = false;
    private Color inkColor = Color.yellow;

    /**
     * Creates a new PostIt object.
     */
    public PostIt() {
        text = new ArrayList<String>();
    }

    /**
     * Sets the starting coordinates of the PostIt.
     *
     * @param x
     * @param y
     */
    public void setStartCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the starting x coordinate.
     *
     * @param x
     */
    public void setStartX(int x) {
        this.x = x;
    }

    /**
     * Sets the starting y coordinate.
     *
     * @param y
     */
    public void setStartY(int y) {
        this.y = y;
    }

    /**
     * Get PostIt's x coordinate
     *
     * @return x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Get PostIt's y coordinate
     *
     * @return y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the width of the PostIt.
     *
     * @param width width of the PostIt
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Sets the height of the PostIt.
     *
     * @param height height of the PostIt
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Get PostIt's width
     *
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get PostIt's height
     *
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets PostIt's color.
     *
     * @param inkColor
     */
    public void setInkColor(Color inkColor) {
        this.inkColor = inkColor;
    }

    /**
     * Adds a string to the list of strings which are drawn on the PostIt.
     *
     * @param s String to be added
     */
    public void addText(String s) {
        text.add(s);
    }

    /**
     * Remove the last element added to text -- backspace functionality.
     */
    public void removeLastCharacter() {
        if (!text.isEmpty()) {
            text.remove(text.size() - 1);
        }
    }

    /**
     * Set PostIt's selected state.
     *
     * @param selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Check if PostIt is selected.
     *
     * @return true if PostIt is selected, false otherwise
     */
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void savePrevCoordinates() {
        this.prevX = x;
        this.prevY = y;
    }

    /**
     * Get post-it's previous y coordinate.
     *
     * @return prevY
     */
    public int getPrevX() {
        return prevX;
    }

    /**
     * Get post-it's previous y coordinate.
     *
     * @return prevY
     */
    public int getPrevY() {
        return prevY;
    }

    @Override
    public void draw(Graphics2D g2, int xOffset, int yOffset) {
        g2.setColor(inkColor);
        g2.fillRect(x + xOffset, y + yOffset, width, height);
        g2.setColor(Color.black);
        drawText(g2, xOffset, yOffset);
    }

    /**
     * Draws text on the PostIt.
     *
     * @param g2 Graphics context
     * @param xOffset
     * @param yOffset
     */
    private void drawText(Graphics2D g2, int xOffset, int yOffset) {
        FontMetrics fm = g2.getFontMetrics();
        int xBase = 5;
        int yBase = 15;
        int currX;
        int currY;
        int sWidth;
        int sHeight;
        for (String s : text) {
            currX = x + xOffset + xBase;
            currY = y + yOffset + yBase;
            sWidth = fm.stringWidth(s);
            sHeight = fm.getHeight();
            if (sWidth + 5 > width) {
                width += 2 * sWidth + 5;
            }
            if (currX > x + xOffset + width - sWidth) {
                xBase = 5;
                currX = x + xOffset + xBase;
                yBase += sHeight;
                currY = y + yOffset + yBase;
            }
            if (currY > y + yOffset + height - sHeight) {
                height += sHeight;
            }
            xBase += sWidth;
            g2.drawString(s, currX, currY);
        }
    }
}
