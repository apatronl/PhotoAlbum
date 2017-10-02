/**
 * Magnet.java
 * CS 4470 - User Interface Software
 * apl7@gatech.edu
 *
 * @author Alejandrina Patron Lopez
 * @version 1.0
 */

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.event.*;

public class Magnet implements MouseListener, MouseMotionListener {

    private final int CENTER = javax.swing.SwingConstants.CENTER;
    private Tag tag;
    private JLabel label;
    private LightTable lightTable;
    int x, y;
    int clickX, clickY;
    int dragX, dragY;

    /**
     * Creates a Magnet object.
     *
     * @param tag
     */
    public Magnet(Tag tag, LightTable lightTable) {
        this.tag = tag;
        this.label = new JLabel(tag.getTagName(), CENTER);
        this.x = 50;
        this.y = 50;
        this.lightTable = lightTable;
        label.setOpaque(true);
        label.setBackground(Color.cyan);
        label.setSize(60, 60);
        label.setLocation(this.x, this.y);
        label.addMouseListener(this);
        label.addMouseMotionListener(this);
        lightTable.moveThumbnailsWithMagnets();
        lightTable.setThumbnailStartCoordinates();
    }

    /**
     * Gets the Magnet's label.
     *
     * @return label of the Megnet object
     */
    public JLabel getMagnet() {
        return label;
    }

    /**
     * Gets x position of the Magnet.
     *
     * @return x
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets y position of the Magnet.
     *
     * @return y
     */
    public int getY() {
        return this.y;
    }

    /**
     * Sets x and y position of the Magnet.
     *
     * @param x
     * @param y
     */
    public void setCoordinates(int x, int y) {
        label.setLocation(x, y);
        this.x = x;
        this.y = y;
    }

    /**
     * Sets x position of the Magnet.
     *
     * @param x
     */
    public void setX(int x) {
        label.setLocation(x, this.y);
        this.x = x;
    }

    /**
     * Sets y position of the Magnet.
     *
     * @param y
     */
    public void setY(int y) {
        label.setLocation(this.x, y);
        this.y = y;
    }

    /**
     * Returns the tag name of this Magnet.
     *
     * @return tag name
     */
    public String getTag() {
        return tag.getTagName();
    }

    /**
     * Updates the location of the magnet.
     *
     * @param newX
     * @param newY
     */
    private void updateLocation(int newX, int newY) {
        label.setLocation(this.x + newX, this.y + newY);
        this.x = this.x + newX;
        this.y = this.y + newY;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.clickX = e.getX();
        this.clickY = e.getY();
        lightTable.setThumbnailStartCoordinates();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        lightTable.moveThumbnailsWithMagnets();
        lightTable.setThumbnailStartCoordinates();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.dragX = e.getX();
        this.dragY = e.getY();
        updateLocation(dragX - clickX, dragY - clickY);
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}

}
