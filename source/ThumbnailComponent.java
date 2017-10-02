/**
 * ThumbnailComponent
 * CS 4470 - User Interface Software
 * apl7@gatech.edu
 *
 * @author Alejandrina Patron Lopez
 * @version 1.0
 */

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Graphics2D;
import java.util.Set;

public class ThumbnailComponent extends JComponent {

    private PhotoComponent photoComponent;
    private Image img;
    private int width;
    private int height;
    private double scaleFactor = 1.0;
    private final int MAX_WIDTH = 206;
    private final int MAX_HEIGHT = 142;
    private int magnetModeX = 50;
    private int magnetModeY = 50;
    private int startX;
    private int startY;

    /**
     * Creates a new ThumbnailComponent.
     *
     * @param photoComponent
     */
    public ThumbnailComponent(PhotoComponent photoComponent) {
        this.photoComponent = photoComponent;
        img = photoComponent.getImage();
        width = img.getWidth(null);
        height = img.getHeight(null);
        scaleFactor = calculateScaleFactor();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.drawImage(img, getImageX(), getImageY(), (int)(scaleFactor * width),
            (int)(scaleFactor * height), null, null);
    }

    @Override
    public Dimension getPreferredSize() {
        if (width > MAX_WIDTH || height > MAX_HEIGHT) return new Dimension(
            (int)(scaleFactor * width), (int)(scaleFactor * height));
        else if (isVertical()) return new Dimension(MAX_HEIGHT, MAX_WIDTH);
        return new Dimension(MAX_WIDTH, MAX_HEIGHT);
    }
    @Override
    public Dimension getMaximumSize() {
        if (width > MAX_WIDTH || height > MAX_HEIGHT) return new Dimension(
            (int)(scaleFactor * width), (int)(scaleFactor * height));
        else if (isVertical()) return new Dimension(MAX_HEIGHT, MAX_WIDTH);
        return new Dimension(MAX_WIDTH, MAX_HEIGHT);
    }

    /**
     * Get image's current x coordinate.
     *
     * @return image's x coordinate
     */
    private int getImageX() {
        if (width > MAX_WIDTH || scaleFactor * width > MAX_WIDTH) return 0;
        else if (isVertical()) return (MAX_HEIGHT / 2) - ((int)(scaleFactor * width) / 2);
        return (MAX_WIDTH / 2) - (width / 2);
    }

    /**
     * Get image's current y coordinate.
     *
     * @return image's x coordinate
     */
    private int getImageY() {
        if (height > MAX_HEIGHT || scaleFactor * height > MAX_HEIGHT) return 0;
        else if (isVertical()) return (MAX_WIDTH / 2) - ((int)(scaleFactor * width) / 2);
        return (MAX_HEIGHT / 2) - (height / 2);
    }

    /**
     * Check if photo is vertical.
     *
     * @return true if photo is vertical, false otherwise
     */
    private boolean isVertical() {
        return height > width;
    }

    /**
     * Calculates a photo's scale factor.
     *
     * @return photo's scale factor
     */
    private double calculateScaleFactor() {
        if (isVertical() && (height > MAX_WIDTH || width > MAX_HEIGHT)) {
            return MAX_HEIGHT / (double)width;
        } else if (width > MAX_WIDTH || height > MAX_HEIGHT) {
            return MAX_WIDTH / (double)width;
        } else {
            return 1.0;
        }
    }

    /**
     * Updates a photo's scale factor by multiplying it by the zoom factor.
     *
     * @param zoomFactor
     */
    public void updateScaleFactor(double zoomFactor) {
        scaleFactor = zoomFactor * calculateScaleFactor();
    }

    /**
     * Sets x and y coordinates of thumbnails in relation to magnets.
     *
     * @param x
     * @param y
     */
    public void setMagnetModeCoordinates(int x, int y) {
        this.magnetModeX = x;
        this.magnetModeY = y;
    }

    /**
     * Gets thumbnail's x coordinate to be used when in magnet mode.
     *
     * @return magnet mode x
     */
    public int getMagnetModeX() {
        return this.magnetModeX;
    }

    /**
     * Gets thumbnail's y coordinate to be used when in magnet mode.
     *
     * @return magnet mode y
     */
    public int getMagnetModeY() {
        return this.magnetModeY;
    }

    /**
     * Saves thumbnail's starting position to be used for animation.
     */
    public void setStartCoordinates() {
        this.startX = this.getX();
        this.startY = this.getY();
    }

    /**
     * Gets thumbnail's starting x coordinate.
     *
     * @return starting x
     */
    public int getStartX() {
        return this.startX;
    }

    /**
     * Gets thumbnail's starting y coordinate.
     *
     * @return starting y
     */
    public int getStartY() {
        return this.startY;
    }

    /**
     * Sets thumbnail's size to be used when in magnet mode.
     */
    public void setSizeForMagnets() {
        this.setSize((int)(scaleFactor * width), (int)(scaleFactor * height));
    }

    /**
     * Gets tags associated with the thumbnail.
     *
     * @return tags
     */
    public Set<String> getTags() {
        return photoComponent.getTags();
    }
}
