/**
 * Tag
 * CS 4470 - User Interface Software
 * apl7@gatech.edu
 *
 * @author Alejandrina Patron Lopez
 * @version 1.0
 */

public enum Tag {
    TRAVEL ("Travel"),
    FAMILY ("Family"),
    SCHOOL ("School"),
    WORK ("Work");

    private String tagName;

    Tag(String tagName) {
        this.tagName = tagName;
    }

    /**
     * Get Tag's name
     *
     * @return name of Tag
     */
    public String getTagName() {
        return tagName;
    }
}
