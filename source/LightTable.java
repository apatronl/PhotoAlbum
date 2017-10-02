/**
 * LightTable
 * CS 4470 - User Interface Software
 * apl7@gatech.edu
 *
 * @author Alejandrina Patron Lopez
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.awt.event.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.Timer;

public class LightTable extends JComponent {

    // Constants
    private final String CENTER = BorderLayout.CENTER;
    private final String SOUTH = BorderLayout.SOUTH;
    private final int ANIMATION_TIME = 125;

    private PhotoAlbum photoAlbum;
    private ViewMode viewMode;
    private PhotoComponent currentPhoto;
    private int currentImgIndex = -1;
    private List<ThumbnailComponent> thumbnails;
    private List<PhotoComponent> photoComponents;
    private JPanel photoPanel;
    private JPanel thumbnailPanel;
    private JScrollPane thumbnailScrollPane;
    private Color inkColor;
    private AnnotationMode annotationMode;
    private Border border;
    private double currZoom = 1.0;
    private boolean magnetMode = false;
    private List<Magnet> magnets;
    private int thumbnailOffset = 0;
    private Timer timer;
    private int numOfIterations = 0;


    /**
     * Creates a LightTable object.
     *
     * @param viewMode
     */
    public LightTable(ViewMode viewMode, PhotoAlbum photoAlbum) {
        super();
        this.setPreferredSize(new Dimension(1000, 800));
        this.setLayout(new BorderLayout());
        this.setBackground(Color.white);
        this.viewMode = viewMode;
        this.photoAlbum = photoAlbum;
        thumbnails = new ArrayList<ThumbnailComponent>();
        photoComponents = new ArrayList<PhotoComponent>();
        magnets = new ArrayList<Magnet>();
        border = BorderFactory.createMatteBorder(3, 3, 3, 3, Color.yellow);
        updatePanel();
    }

    /**
     * Adds an image to the LightTable. Method is called when the user imports
     * a photo
     *
     * @param img photo to be added to the LightTable
     */
    public void addImage(Image img) {
        if (img != null) {
            PhotoComponent photoComponent = new PhotoComponent(img, this);
            photoComponent.setAnnotationMode(annotationMode);
            photoComponent.setInkColor(inkColor);
            currentPhoto = photoComponent;
            photoComponents.add(photoComponent);
            ThumbnailComponent thumbnail = new ThumbnailComponent(photoComponent);
            thumbnail.updateScaleFactor(currZoom);
            thumbnails.add(thumbnail);
            if (currentImgIndex >= 0) thumbnails.get(currentImgIndex).setBorder(BorderFactory.createEmptyBorder());
            currentImgIndex = photoComponents.size() - 1;
        }
        photoAlbum.updateSelectedTags(null);
        updatePanel();
    }

    /**
     * Updates the LightTable when an image is added and/or when
     * the view mode changes
     */
    private void updatePanel() {
        if (viewMode == ViewMode.PHOTO) {
            createPhotoPanel();
        } else if (viewMode == ViewMode.GRID) {
            removePhotoPanel();
            createThumbnailPanel(ViewMode.GRID);
        } else if (viewMode == ViewMode.SPLIT) {
            createPhotoPanel();
            createThumbnailPanel(ViewMode.SPLIT);
        }
        revalidate();
        repaint();
    }

    /**
     * Creates photo panel, where a single photo is shown when
     * viewMode == ViewMode.PHOTO
     */
    private void createPhotoPanel() {
        if (photoPanel != null) this.remove(photoPanel);
        if (thumbnailScrollPane != null) this.remove(thumbnailScrollPane);
        if (thumbnailPanel != null) this.remove(thumbnailPanel);
        photoPanel = new JPanel();
        photoPanel.setLayout(new BorderLayout());
        if (currentPhoto != null) {
            photoPanel.add(currentPhoto.getPhotoComponentScrollPane(), CENTER);
        } else {
            photoPanel.add((new PhotoComponent(null, this)).getPhotoComponentScrollPane(), CENTER);
        }
        this.add(photoPanel, CENTER);
    }

    /**
     * Creates thumbnail panel, where a grid of photos is shown when
     * viewMode == ViewMode.SPLIT or viewMode == ViewMode.GRID
     */
    private void createThumbnailPanel(ViewMode viewMode) {
        if (thumbnailScrollPane != null) this.remove(thumbnailScrollPane);
        if (thumbnailPanel != null) this.remove(thumbnailPanel);
        thumbnailPanel = new JPanel();
        thumbnailPanel.setBackground(Color.gray);
        if (viewMode == ViewMode.SPLIT || (viewMode == ViewMode.GRID && !magnetMode)) {
            if (timer != null) timer.stop();
            thumbnailOffset = 0;
            JPanel extraPanel = new JPanel();
            extraPanel.setLayout(new FlowLayout());
            extraPanel.add(thumbnailPanel);
            thumbnailScrollPane = new JScrollPane(extraPanel);
            thumbnailScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            thumbnailScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            extraPanel.setBackground(Color.gray);
        }
        if (magnetMode && viewMode == ViewMode.GRID) {
            thumbnailPanel.setLayout(null);
            for (Magnet m : magnets) {
                thumbnailPanel.add(m.getMagnet());
            }
            for (ThumbnailComponent thumbnail : thumbnails) {
                setThumbnailStartCoordinates();
                moveThumbnailsWithMagnets();
                thumbnail.setSizeForMagnets();
                thumbnailPanel.add(thumbnail);
            }
            this.add(thumbnailPanel);
        } else if (viewMode == ViewMode.SPLIT) {
            thumbnailPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5,5,5,5);
            this.add(thumbnailScrollPane, SOUTH);
            for (ThumbnailComponent thumbnail : thumbnails) {
                thumbnailPanel.add(thumbnail, c);
            }
        } else if (viewMode == ViewMode.GRID) {
            thumbnailPanel.setLayout(new GridLayout(0, 4, 10, 10));
            this.add(thumbnailScrollPane, CENTER);
            for (ThumbnailComponent thumbnail : thumbnails) {
                thumbnail.setLocation(0, 0);
                thumbnailPanel.add(thumbnail);
            }
        }

        if (currentImgIndex >= 0) thumbnails.get(currentImgIndex).setBorder(border);
        addThumbnailListeners();
    }

    /**
     * Removes the photo panel from the LightTable.
     */
    private void removePhotoPanel() {
        if (photoPanel != null) this.remove(photoPanel);
    }

    /**
     * Adds necessary listeners to the thumbnail panel.
     */
    private void addThumbnailListeners() {
        thumbnailPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int i = 0;
                    for (ThumbnailComponent thumbnail : thumbnails) {
                        if (thumbnailWasClicked(thumbnail, e.getPoint())) {
                            thumbnails.get(currentImgIndex).setBorder(BorderFactory.createEmptyBorder());
                            currentImgIndex = i;
                            viewMode = ViewMode.PHOTO;
                            photoAlbum.updateViewMenu(viewMode);
                            updateCurrentPhoto();
                        } else {
                            i++;
                        }
                    }
                } else {
                    int i = 0;
                    for (ThumbnailComponent thumbnail : thumbnails) {
                        if (thumbnailWasClicked(thumbnail, e.getPoint())) {
                            thumbnails.get(currentImgIndex).setBorder(BorderFactory.createEmptyBorder());
                            currentImgIndex = i;
                            updateCurrentPhoto();
                            updateStatus("Thumbnail");
                        } else {
                            i++;
                        }
                    }
                }
            }
        });
    }

    /**
     * Checks if a specific thumbnail was clicked.
     *
     * @return true if the thumbnail was clicked, false otherwise.
     */
    private boolean thumbnailWasClicked(ThumbnailComponent thumbnail, Point point) {
        return thumbnail.getBounds().contains(point);
    }

    /**
     * Get LightTable's current view mode
     *
     * @return current view mode
     */
    public ViewMode getViewMode() {
        return viewMode;
    }

    /**
     * Set the LightTable's current view mode
     *
     * @param viewMode mode to set the light table to
     */
    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
        updatePanel();
    }

    /**
     * Sets color to be used when drawing a line on a photo.
     *
     * @param Color
     */
    public void setInkColor(Color color) {
        this.inkColor = color;
        if (currentPhoto != null) currentPhoto.setInkColor(inkColor);
    }

    public void setAnnotationMode(AnnotationMode annotationMode) {
        this.annotationMode = annotationMode;
        if (currentPhoto != null) currentPhoto.setAnnotationMode(annotationMode);
    }

    /**
     * Show previous photo, if one is available.
     */
    public void previous() {
        if (currentImgIndex - 1 >= 0) {
            thumbnails.get(currentImgIndex).setBorder(BorderFactory.createEmptyBorder());
            currentImgIndex--;
            updateCurrentPhoto();
        }
        updateStatus("<");
    }

    /**
     * Show next photo, if one is available.
     */
    public void next() {
        if (currentImgIndex + 1 < photoComponents.size()) {
            thumbnails.get(currentImgIndex).setBorder(BorderFactory.createEmptyBorder());
            currentImgIndex++;
            updateCurrentPhoto();
        }
        updateStatus(">");
    }

    /**
     * Updates the current photo shown on the LightTable.
     */
    private void updateCurrentPhoto() {
        if (currentImgIndex < 0) {
            currentPhoto = null;
        } else {
            currentPhoto = photoComponents.get(currentImgIndex);
            currentPhoto.setInkColor(inkColor);
            currentPhoto.setAnnotationMode(annotationMode);
            photoAlbum.updateSelectedTags(currentPhoto.getTags());
        }
        updatePanel();
    }

    /**
     * Deletes the current photo shown on the LightTable and updates it.
     */
    public void deleteCurrentPhoto() {
        if (currentImgIndex >= 0) {
            photoComponents.remove(currentImgIndex);
            thumbnails.remove(currentImgIndex);
        }
        if (photoComponents.size() == 0) {
            currentImgIndex = -1;
            currentPhoto = null;
        } else if (currentImgIndex >= 1) {
            currentImgIndex--;
        }
        updateCurrentPhoto();
    }

    /**
     * Add selected tag to current photo.
     *
     * @param tag
     */
    public void addTag(Tag tag) {
        if (currentPhoto != null) {
            currentPhoto.addTag(tag);
        }
    }

    /**
     * Remove deselected tag from current photo.
     *
     * @param tag
     */
    public void removeTag(Tag tag) {
        if (currentPhoto != null) {
            currentPhoto.removeTag(tag);
        }
    }

    /**
     * Updates the message displayed in the status bar of the PhotoAlbum.
     */
    public void updateStatus(String status) {
        photoAlbum.updateStatusBar(status);
    }

    /**
     * Updates the state of the tag checkboxes of the PhotoAlbum.
     */
    public void updateTags(Set<String> tags) {
        photoAlbum.updateSelectedTags(tags);
    }

    /**
     * Update the scale factor of the thumbnails.
     *
     * @param zoomFactor
     */
    public void updateScaleFactor(double zoomFactor) {
        currZoom = zoomFactor;
        for (ThumbnailComponent thumbnail : thumbnails) {
            thumbnail.updateScaleFactor(zoomFactor);
        }
        updatePanel();
    }

    /**
     * Adds or removes the given magnet.
     *
     * @param tag
     */
    public void updateMagnets(Tag tag) {
        if (magnetExists(tag)) removeMagnet(tag);
        else magnets.add(0, new Magnet(tag, this));
        updatePanel();
    }

    /**
     * Updates the application's magnet mode state.
     *
     * @param magnetMode
     */
    public void setMagnetMode(boolean magnetMode) {
        this.magnetMode = magnetMode;
        updatePanel();
    }

    /**
     * Adds the given magnet to the magnet list.
     *
     * @param tag
     */
    public void addMagnet(Tag tag) {
        magnets.add(new Magnet(tag, this));
    }

    /**
     * Removes the given magnet.
     *
     * @param tag
     */
    private void removeMagnet(Tag tag) {
        Magnet curr;
        for (int i = 0; i < magnets.size(); i++) {
            curr = magnets.get(i);
            if (curr.getTag().equals(tag.getTagName())) {
                magnets.remove(i);
                break;
            }
        }
    }

    /**
     * Check if given magnet exists.
     *
     * @param tag
     */
    private boolean magnetExists(Tag tag) {
        for (Magnet m: magnets) {
            if (m.getTag().equals(tag.getTagName())) return true;
        }
        return false;
    }

    /**
     * Calculates each thumbnail's position in relation to its tags and magnets
     * currently present in the application.
     */
    public void positionsInRelationToMagnets() {
        Set<String> tags;
        int magnetsX = 0;
        int magnetsY = 0;;
        int thumbnailX;
        int thumbnailY;
        int numOfMagnets = 0;
        for (ThumbnailComponent thumbnail : thumbnails) {
            tags = thumbnail.getTags();
            if (tags.size() > 0) { // No need to calculate location if photo isn't tagged
                for (Magnet magnet : magnets) {
                    if (tags.contains(magnet.getTag())) {
                        numOfMagnets++;
                        magnetsX += magnet.getX();
                        magnetsY += magnet.getY();
                    }
                }
                if (numOfMagnets > 0) {
                    thumbnailX = magnetsX / numOfMagnets;
                    thumbnailY = magnetsY / numOfMagnets;
                    thumbnail.setMagnetModeCoordinates(thumbnailX, thumbnailY);
                }
            }
            magnetsX = 0;
            magnetsY = 0;
            numOfMagnets = 0;
        }
    }

    /**
     * Moves each thumbnail in relation to its tags and magnets currently
     * present in the application.
     */
    public void moveThumbnailsWithMagnets() {
        if (timer != null ) {
            timer.stop();
            numOfIterations = 0;
        }
        timer = new Timer(ANIMATION_TIME, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (numOfIterations++ >= 7) {
                    timer.stop();
                    numOfIterations = 0;
                } else {
                    positionsInRelationToMagnets();
                    for (ThumbnailComponent thumbnail : thumbnails) {
                        thumbnail.setLocation(
                        thumbnail.getX() +
                        ((thumbnail.getMagnetModeX() - thumbnail.getStartX()) / 7),
                        thumbnail.getY() +
                        ((thumbnail.getMagnetModeY() - thumbnail.getStartY()) / 7));
                    }
                }
            }
        });
        timer.start();
    }

    /**
     * Saves each thumbnail's starting position to be used for animation.
     */
    public void setThumbnailStartCoordinates() {
        for (ThumbnailComponent thumbnail : thumbnails) {
            thumbnail.setStartCoordinates();
        }
    }
}
