/**
 * PhotoComponent
 * CS 4470 - User Interface Software
 * apl7@gatech.edu
 *
 * @author Alejandrina Patron Lopez
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class PhotoComponent extends JComponent {

    private Image img;
    private LightTable lightTable;
    private boolean flipped = false;
    private AnnotationMode annotationMode;
    private Set<String> tags;
    private JScrollPane scrollPane;
    private int windowWidth, windowHeight;
    private int imgWidth, imgHeight;
    private Graphics2D g2;
    private Color inkColor;
    // All annotations drawn, i.e., lines and post-its
    private List<Annotation> annotations = new ArrayList<Annotation>();
    private PolyLine currentLine; // Line drawn when AnnotationMode == DRAWING
    private PostIt currentPostIt; // Post-It drawn when AnnotationMode == TEXT
    private int rectX, rectY, originX, originY, width, height; // Post-It data
    private String s = ""; // Text drawn on post-it
    private GestureRecognizer gestureRecognizer;
    private boolean rightClick = false;
    private PolyLine currentGestureLine;
    private boolean annotationsSelected = false;
    private boolean dragging = false;
    private int dragOriginX, dragOriginY;

    /**
     * Creates a new PhotoComponent.
     *
     * @param img image to be drawn on the PhotoComponent
     */
    public PhotoComponent(Image img, LightTable lightTable) {
        super();
        this.img = img;
        this.lightTable = lightTable;
        inkColor = Color.black;
        tags = new HashSet<String>();
        if (img != null) {
            this.setSize(new Dimension(img.getWidth(null), img.getHeight(null)));
            this.setPreferredSize(new Dimension(img.getWidth(null), img.getHeight(null)));
        } else {
            this.setSize(new Dimension(640, 480));
            this.setPreferredSize(new Dimension(640, 480));
        }
        scrollPane = new JScrollPane(this);
        scrollPane.getViewport().setBackground(Color.gray);
        scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            gestureRecognizer = new GestureRecognizer();
        if (img != null) addListeners();
    }

    public JScrollPane getPhotoComponentScrollPane() {
        return scrollPane;
    }

    public void hidePhotoComponent() {
        scrollPane.setVisible(false);
    }

    public Image getImage() {
        return img;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHints(rh);
        super.paintComponent(g2);

        // Get focus for key events
        this.setFocusable(true);
        this.requestFocusInWindow();

        g2.setColor(Color.white);

        // Update width and height variables on repaint
        windowWidth = scrollPane.getWidth();
        windowHeight = scrollPane.getHeight();
        if (img != null) {
            imgWidth = img.getWidth(null);
            imgHeight = img.getHeight(null);
            if (!flipped) {
                g2.drawImage(img, getImageX(), getImageY(), null);
            } else {
                g2.fillRect(getImageX(), getImageY(), imgWidth, imgHeight);
                g2.setColor(inkColor);
                // Draw lines first
                for (Annotation a : annotations) {
                    if (a instanceof PolyLine) a.draw(g2, getImageX(), getImageY());
                }
                // Then draw postits on top
                for (Annotation a : annotations) {
                    if (a instanceof PostIt) a.draw(g2, getImageX(), getImageY());
                }
            }
            if (currentGestureLine != null) currentGestureLine.draw(g2, 0, 0);
        }
    }

    /**
     * Sets the annotation mode of the PhotoComponent.
     *
     * @param mode annotation mode to be used
     */
    public void setAnnotationMode(AnnotationMode mode) {
        annotationMode = mode;
    }

    /**
     * Adds a single tag to the PhotoComponent.
     *
     * @param tag
     */
    public void addTag(Tag tag) {
        tags.add(tag.getTagName());
    }

    /**
     * Adds multiple tags to the PhotoComponent.
     *
     * @param tags
     */
    public void addMultipleTags(List<Tag> tags) {
        for (Tag tag : tags) {
            this.tags.add(tag.getTagName());
        }
    }

    /**
     * Removes a single tag from the PhotoComponent.
     *
     * @param tag
     */
    public void removeTag(Tag tag) {
        tags.remove(tag.getTagName());
    }

    /**
     * Removes all tags from the PhotoComponent.
     */
    public void removeAllTags() {
        tags.clear();
    }

    /**
     * Returns a set containing the PhotoComponent's tags.
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * Sets color to be used when drawing a line.
     *
     * @param Color
     */
    public void setInkColor(Color color) {
        this.inkColor = color;
    }

    /**
     * Adds all necessary listeners to the PhotoComponent.
     */
    private void addListeners() {
        this.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if (e.getClickCount() == 1) {
                    if (annotationsSelected) {
                        deselectAnnotations();
                        annotationsSelected = false;
                        dragging = false;
                    }
                } else if (e.getClickCount() == 2) {
                    Point p = e.getPoint();
                    if (imageWasClicked(p)) {
                        flipped = !flipped;
                        repaint();
                    }
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) rightClick = true;
                else rightClick = false;
                Point p = e.getPoint();
                if (rightClick) { // Gestures
                    int x = e.getX();
                    int y = e.getY();
                    currentGestureLine = new PolyLine();
                    currentGestureLine.setInkColor(Color.RED);
                    currentGestureLine.addPoint(x, y);
                } else if (flipped && !rightClick) {
                    int x = e.getX();
                    int y = e.getY();
                    if (annotationsSelected && annotationWasClicked(x - getImageX(), y - getImageY())) {
                        dragOriginX = e.getX();
                        dragOriginY = e.getY();
                        dragging = true;
                        savePrevCoordinates();
                    } else if (imageWasClicked(p)) {
                        if (annotationsSelected) {
                            deselectAnnotations();
                            annotationsSelected = false;
                            dragging = false;
                        }
                        x = e.getX() - getImageX();
                        y = e.getY() - getImageY();
                        if (drawing()) {
                            currentLine = new PolyLine();
                            currentLine.setInkColor(inkColor);
                            annotations.add(currentLine);
                            currentLine.addPoint(x, y);
                        } else {
                            currentPostIt = new PostIt();
                            originX = e.getX() - getImageX();
                            originY = e.getY() - getImageY();
                            currentPostIt.setStartCoordinates(originX, originY);
                            annotations.add(currentPostIt);
                        }
                    }
                }
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = e.getPoint();
                if (rightClick) { // Gestures
                    int x = e.getX();
                    int y = e.getY();
                    currentGestureLine.addPoint(x, y);
                } else if (dragging) {
                    // change coordinates of annotations
                    int x = e.getX();
                    int y = e.getY();
                    updateAnnotationCoordinates(x - dragOriginX, y - dragOriginY);
                } else if (flipped && imageWasClicked(p) && !rightClick) { // Annotations
                    int x = e.getX() - getImageX();
                    int y = e.getY() - getImageY();
                    if (drawing()) {
                        if (currentLine == null) {
                            currentLine = new PolyLine();
                            currentLine.setInkColor(inkColor);
                            annotations.add(currentLine);
                        }
                        currentLine.addPoint(x, y);
                    } else {
                        if (currentPostIt != null) {
                            // Update coordinates if dragging in negative direction
                            if (x < originX) {
                                rectX = x;
                                currentPostIt.setStartX(rectX);
                                width = originX - rectX;
                            } else {
                                width = x - originX;
                            }
                            currentPostIt.setWidth(width);
                            if (y < originY) {
                                rectY = y;
                                currentPostIt.setStartY(rectY);
                                height = originY - rectY;
                            } else {
                                height = y - originY;
                            }
                            currentPostIt.setHeight(height);
                        }
                    }
                }
                repaint();
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!flipped && rightClick) { // Gestures for UNFLIPPED photo
                    String directionVector = gestureRecognizer.buildDirectionVector(currentGestureLine);
                    String matchedGesture = gestureRecognizer.matchGesture(directionVector, false);
                    switch (matchedGesture) {
                        case Gestures.RIGHT_ANGLE:
                            lightTable.updateStatus(">");
                            lightTable.next();
                            break;
                        case Gestures.LEFT_ANGLE:
                            lightTable.updateStatus("<");
                            lightTable.previous();
                            break;
                        case Gestures.UP_ARROW:
                            if (tags.contains(Tag.TRAVEL.getTagName())) removeTag(Tag.TRAVEL);
                            else addTag(Tag.TRAVEL);
                            lightTable.updateStatus(Tag.TRAVEL.getTagName());
                            lightTable.updateTags(tags);
                            break;
                        case Gestures.DOWN_ARROW:
                            if (tags.contains(Tag.FAMILY.getTagName())) removeTag(Tag.FAMILY);
                            else addTag(Tag.FAMILY);
                            lightTable.updateStatus(Tag.FAMILY.getTagName());
                            lightTable.updateTags(tags);
                            break;
                        case Gestures.W:
                            if (tags.contains(Tag.WORK.getTagName())) removeTag(Tag.WORK);
                            else addTag(Tag.WORK);
                            lightTable.updateStatus(Tag.WORK.getTagName());
                            lightTable.updateTags(tags);
                            break;
                        case Gestures.S:
                            if (tags.contains(Tag.SCHOOL.getTagName())) removeTag(Tag.SCHOOL);
                            else addTag(Tag.SCHOOL);
                            lightTable.updateStatus(Tag.SCHOOL.getTagName());
                            lightTable.updateTags(tags);
                            break;
                        case Gestures.LOWERCASE_PHI:
                            lightTable.updateStatus("Delete");
                            lightTable.deleteCurrentPhoto();
                            break;
                        default:
                            lightTable.updateStatus("Unrecognized gesture");
                            break;
                    }
                    currentGestureLine = null;
                } else if (flipped && !rightClick) {
                    // Annotation done
                    if (drawing()) currentLine = null;
                } else if (flipped && rightClick) { // Gestures for FLIPPED photo
                    String directionVector = gestureRecognizer.buildDirectionVector(currentGestureLine);
                    String matchedGesture = gestureRecognizer.matchGesture(directionVector, true);
                    if (matchedGesture.equals(Gestures.CIRCLE)) {
                        int count = 0;
                        for (Annotation a : annotations) {
                            if (annotationWasSelected(a)) count++;
                        }
                        if (count > 0) annotationsSelected = true;
                        lightTable.updateStatus("Selection");
                    } else if (matchedGesture.equals(Gestures.LOWERCASE_PHI)) {
                        if (annotationsSelected) { // Delete annotations selected (if any)
                            deleteSelectedAnnotations();
                        }
                        annotationsSelected = false;
                        lightTable.updateStatus("Delete");
                    } else {
                        lightTable.updateStatus("Unrecognized gesture");
                    }
                    currentGestureLine = null;
                }
                dragging = false;
                repaint();
            }
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (flipped && text() && currentPostIt != null) {
                    char typedText = e.getKeyChar();
                    // Backspace functionality
                    if (typedText == KeyEvent.VK_BACK_SPACE || e.getKeyChar() == KeyEvent.VK_DELETE) {
                        currentPostIt.removeLastCharacter();
                    } else {
                        s += typedText;
                        currentPostIt.addText(s);
                        s = "";
                    }
                    repaint();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    /**
     * Check if a point on the image was clicked.
     *
     * @return true if the image was clicked
     */
    private boolean imageWasClicked(Point p) {
        Rectangle imageBounds;
        if (widthAndHeightTooSmall(windowWidth, windowHeight, imgWidth, imgHeight)) {
            imageBounds = new Rectangle(0, 0, imgWidth, imgHeight);
        } else if (heightTooSmall(windowHeight, imgHeight)) {
            imageBounds = new Rectangle(windowWidth / 2 - imgWidth / 2, 0, imgWidth, imgHeight);
        } else if (widthTooSmall(windowWidth, imgWidth)) {
            imageBounds = new Rectangle(0, windowHeight / 2 - imgHeight / 2,imgWidth, imgHeight);
        } else {
            imageBounds = new Rectangle(windowWidth / 2 - imgWidth / 2, windowHeight / 2 - imgHeight / 2, imgWidth, imgHeight);
        }
        return imageBounds.contains(p);
    }

    /**
     * Check if an annotation was selected, and update the annotation's
     * selected state.
     *
     * @return true if the image was clicked
     */
    private boolean annotationWasSelected(Annotation annotation) {
        int circleMinX, circleMinY, circleMaxX, circleMaxY;
        ArrayList<Integer> circleXCoords = currentGestureLine.getXList();
        ArrayList<Integer> circleYCoords = currentGestureLine.getYList();
        circleMinX = Collections.min(circleXCoords);
        circleMinY = Collections.min(circleYCoords);
        circleMaxX = Collections.max(circleXCoords);
        circleMaxY = Collections.max(circleYCoords);
        int x, y;
        if (annotation instanceof PolyLine) {
            ArrayList<Integer> xCoords = ((PolyLine)annotation).getXList();
            ArrayList<Integer> yCoords = ((PolyLine)annotation).getYList();
            boolean xInCircle, yInCircle;
            for (int i = 0; i < xCoords.size(); i++) {
                x = xCoords.get(i) + getImageX();
                y = yCoords.get(i) + getImageY();
                xInCircle = (x >= circleMinX) && (x <= circleMaxX);
                yInCircle = (y >= circleMinY) && (y <= circleMaxY);
                if (!(xInCircle && yInCircle)) return false;
            }
        } else {
            x = ((PostIt)annotation).getX() + getImageX();
            y = ((PostIt)annotation).getY() + getImageY();
            int width = ((PostIt)annotation).getWidth();
            int height = ((PostIt) annotation).getHeight();
            if (!((x >= circleMinX) && (x + width <= circleMaxX)
                && (y >= circleMinY) && (y + height <= circleMaxY))) {
                return false;
            }
        }
        annotation.setSelected(true);
        annotation.setInkColor(Color.RED);
        return true;
    }

    /**
     * Check if a selected annotation was clicked for dragging.
     */
    private boolean annotationWasClicked(int x, int y) {
        for (Annotation a : annotations) {
            if (a.isSelected()) {
                if (a instanceof PolyLine) {
                    ArrayList<Integer> xCoords = ((PolyLine)a).getXList();
                    ArrayList<Integer> yCoords = ((PolyLine)a).getYList();
                    int currX, currY, diffX, diffY;
                    for (int i = 0; i < xCoords.size(); i++) {
                        currX = xCoords.get(i);
                        currY = yCoords.get(i);
                        diffX = x - currX;
                        diffY = y - currY;
                        // Account for a click not happening exactly on the line
                        if ((diffX > -5) && (diffX < 5) && (diffY > -5) && (diffY < 5)) return true;
                    }
                } else {
                    int postItX = ((PostIt)a).getX();
                    int postItY = ((PostIt)a).getY();
                    int width = ((PostIt)a).getWidth();
                    int height = ((PostIt)a).getHeight();
                    if ((x >= postItX) && (x <= postItX + width) && (y >= postItY) && (y <= postItY + height)) return true;
                }
            }
        }
        return false;
    }

    /**
     * Update selected annotation's x and y coordinates.
     */
    private void updateAnnotationCoordinates(int x, int y) {
        for (Annotation a : annotations) {
            if (a instanceof PostIt && a.isSelected()) { // PostIt
                int postItX = ((PostIt)a).getPrevX();
                int postItY = ((PostIt)a).getPrevY();
                ((PostIt)a).setStartCoordinates(postItX + x,  postItY + y);
            } else if (a instanceof PolyLine && a.isSelected()) {
                // PolyLine
                ArrayList<Integer> prevXList = ((PolyLine)a).getPrevX();
                ArrayList<Integer> prevYList = ((PolyLine)a).getPrevY();
                ArrayList<Integer> newXList = new ArrayList<Integer>();
                ArrayList<Integer> newYList = new ArrayList<Integer>();
                int currX, currY;
                for (int i = 0; i < prevXList.size(); i++) {
                    currX = prevXList.get(i);
                    currY = prevYList.get(i);
                    newXList.add(currX + x);
                    newYList.add(currY + y);
                }
                ((PolyLine)a).setCoordinates(newXList, newYList);
            }
        }
    }

    /**
     * Save an annotations previous coordinates for dragging reference.
     */
    private void savePrevCoordinates() {
        for (Annotation a : annotations) {
            if (a.isSelected()) {
                a.savePrevCoordinates();
            }
        }
    }

    /**
     * Remove annotations selected from annotations list.
     */
    private void deleteSelectedAnnotations() {
        Annotation curr;
        for (int i = 0; i < annotations.size(); i++) {
            curr = annotations.get(i);
            if (curr.isSelected()) {
                annotations.remove(i);
                i--;
            }
        }
    }

    /**
     * Deselect all currently selected annotations.
     */
    private void deselectAnnotations() {
        for (Annotation a : annotations) {
            if (a.isSelected()) {
                a.setSelected(false);
                if (a instanceof PostIt) a.setInkColor(Color.yellow);
                else a.setInkColor(inkColor);
            }
        }
    }

    /**
     * Get image's current x coordinate. This coordinate changes on window
     * resize so that the image is always centered.
     *
     * @return image's x coordinate
     */
    private int getImageX() {
        if (widthAndHeightTooSmall(windowWidth, windowHeight, imgWidth, imgHeight)) {
            return 0;
        } else if (heightTooSmall(windowHeight, imgHeight)) {
            return windowWidth / 2 - imgWidth / 2;
        } else if (widthTooSmall(windowWidth, imgWidth)) {
            return 0;
        } else {
            return windowWidth / 2 - imgWidth / 2;
        }
    }

    /**
     * Get image's current y coordinate. This coordinate changes on window
     * resize so that the image is always centered.
     *
     * @return image's y coordinate
     */
    private int getImageY() {
        if (widthAndHeightTooSmall(windowWidth, windowHeight, imgWidth, imgHeight)) {
            return 0;
        } else if (heightTooSmall(windowHeight, imgHeight)) {
            return 0;
        } else if (widthTooSmall(windowWidth, imgWidth)) {
            return windowHeight / 2 - imgHeight / 2;
        } else {
            return windowHeight / 2 - imgHeight / 2;
        }
    }

    /**
     * Check if both the current width and height of the window are too small
     * to fit the image.
     *
     * @return true if both the width and height of the window are too small to
     * fit the image
     */
    private boolean widthAndHeightTooSmall(int windowWidth, int windowHeight, int imgWidth, int imgHeight) {
        return windowHeight < imgHeight && windowWidth < imgWidth;
    }

    /**
     * Check if the current width of the window is too small to fit the image.
     *
     * @return true if the width of the window is too small to fit the image
     */
    private boolean widthTooSmall(int windowWidth, int imgWidth) {
        return windowWidth < imgWidth;
    }

    /**
     * Check if the current height of the window is too small to fit the image.
     *
     * @return true if the height of the window is too small to fit the image
     */
    private boolean heightTooSmall(int windowHeight, int imgHeight) {
        return windowHeight < imgHeight;
    }

    /**
     * Check if current annotation mode is drawing.
     *
     * @return true if annotationMode == DRAWING
     */
    private boolean drawing() {
        return this.annotationMode == AnnotationMode.DRAWING;
    }

    /**
     * Check if current annotation mode is text.
     *
     * @return true if annotationMode == TEXT
     */
    private boolean text() {
        return this.annotationMode == AnnotationMode.TEXT;
    }
}
