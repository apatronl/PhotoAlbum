/**
 * ViewMode
 * CS 4470 - User Interface Software
 * apl7@gatech.edu
 *
 * @author Alejandrina Patron Lopez
 * @version 1.0
 */

public enum ViewMode {
    PHOTO ("Photo View"),
    GRID ("Grid View"),
    SPLIT ("Split View");

    private String modeName;

    ViewMode(String modeName) {
        this.modeName = modeName;
    }

    /**
     * Get ViewMode's name
     *
     * @return name of ViewMode
     */
    public String getModeName() {
        return modeName;
    }
}
