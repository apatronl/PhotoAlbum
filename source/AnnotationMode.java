/**
 * AnnotationMode
 * CS 4470 - User Interface Software
 * apl7@gatech.edu
 *
 * @author Alejandrina Patron Lopez
 * @version 1.0
 */

public enum AnnotationMode {
    DRAWING ("Drawing"),
    TEXT ("Text");

    private String modeName;

    AnnotationMode(String modeName) {
        this.modeName = modeName;
    }

    /**
     * Get AnnotationMode's name
     *
     * @return name of AnnotationMode
     */
    public String getModeName() {
        return modeName;
    }
}
