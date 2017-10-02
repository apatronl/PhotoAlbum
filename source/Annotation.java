/**
 * Annotation
 * An annotation could be either a PolyLine or a PostIt
 * CS 4470 - User Interface Software
 * apl7@gatech.edu
 *
 * @author Alejandrina Patron Lopez
 * @version 1.0
 */

import java.awt.Graphics2D;
import java.awt.Color;

public interface Annotation {

    /**
     * Draws the annotation on the screen, as decided by classes implementing
     * Annotation.
     *
     * @param g2 Graphics context
     * @param xOffset
     * @param yOffset
     */
    public void draw(Graphics2D g2, int xOffset, int yOffset);

    /**
     * Set Annotations's selected state.
     *
     * @param selected
     */
    public void setSelected(boolean selected);

    /**
     * Check if Annotation is selected.
     */
    public boolean isSelected();

    /**
     * Sets Annotation's color.
     *
     * @param inkColor
     */
    public void setInkColor(Color color);

    /**
     * Save an annotation's previous coordinates for dragging reference.
     */
    public void savePrevCoordinates();
}
