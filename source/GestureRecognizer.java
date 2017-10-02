/**
 * GestureRecognizer
 * CS 4470 - User Interface Software
 * apl7@gatech.edu
 *
 * @author Alejandrina Patron Lopez
 * @version 1.0
 */

import java.awt.Point;
import java.util.List;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class GestureRecognizer {

    public static final String N = "N";
    public static final String S = "S";
    public static final String E = "E";
    public static final String W = "W";
    public static final String NE = "B";
    public static final String NW = "A";
    public static final String SE = "C";
    public static final String SW = "D";

    private Pattern rightAngle, leftAngle, lowercasePhi, upArrow, downArrow, s, w, circle;


    /**
     * Creates a new GestureRecognizer.
     */
    public GestureRecognizer() {
        rightAngle = Pattern.compile(Gestures.RIGHT_ANGLE);
        leftAngle = Pattern.compile(Gestures.LEFT_ANGLE);
        lowercasePhi = Pattern.compile(Gestures.LOWERCASE_PHI);
        upArrow = Pattern.compile(Gestures.UP_ARROW);
        downArrow = Pattern.compile(Gestures.DOWN_ARROW);
        s = Pattern.compile(Gestures.S);
        w = Pattern.compile(Gestures.W);
        circle = Pattern.compile(Gestures.CIRCLE);
    }

    /**
     * Builds a direction vecture of the gesture drawn.
     *
     * @param gesture
     * @return directionVector
     */
    public String buildDirectionVector(PolyLine gesture) {
        ArrayList<Integer> xCoords = gesture.getXList();
        ArrayList<Integer> yCoords = gesture.getYList();
        String directionVector = "";
        if (xCoords.size() <= 1) return directionVector;
        int prevX, prevY, currX, currY, diffX, diffY;
        for (int i = 1; i < xCoords.size(); i++) {
            prevX = xCoords.get(i - 1);
            prevY = yCoords.get(i - 1);
            currX = xCoords.get(i);
            currY = yCoords.get(i);
            diffX = prevX - currX;
            diffY = prevY - currY;
            if (diffX == 0) {
                if (diffY < 0) directionVector += S;
                else if (diffY > 0) directionVector += N;
            }
            if (prevY == 0) {
                if (diffX < 0) directionVector += E;
                else if (diffX > 0) directionVector += W;
            }
            if (diffX > 0 && diffY > 0) directionVector += NW;
            if (diffX > 0 && diffY < 0) directionVector += SW;
            if (diffX < 0 && diffY > 0) directionVector += NE;
            if (diffX < 0 && diffY < 0) directionVector += SE;
        }
        return directionVector;
    }

    /**
     * Matches a direction vecture to a gesture.
     *
     * @param directionVector
     * @param flipped
     * @return matched gesture, or "" if none were matched
     */
    public String matchGesture(String directionVector, boolean flipped) {
        if (flipped && circle.matcher(directionVector).find()) {
            return Gestures.CIRCLE;
        }
        if (lowercasePhi.matcher(directionVector).find()) {
            return Gestures.LOWERCASE_PHI;
        }
        if (!flipped) {
            if (rightAngle.matcher(directionVector).find()) {
                return Gestures.RIGHT_ANGLE;
            } else if (leftAngle.matcher(directionVector).find()) {
                 return Gestures.LEFT_ANGLE;
            } else if (upArrow.matcher(directionVector).find()) {
                return Gestures.UP_ARROW;
            } else if (downArrow.matcher(directionVector).find()) {
                return Gestures.DOWN_ARROW;
            } else if (w.matcher(directionVector).find()) {
                return Gestures.W;
            } else if (s.matcher(directionVector).find()) {
                return Gestures.S;
            }
        }
        return "";
    }
}
