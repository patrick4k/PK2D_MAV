package com.PK2D.PK2D_Mav.Util;

import com.PK2D.PK2D_Mav.Phys.Position.LinePosition;

public class GameMath {
    public static boolean approximatelyEqual(double desiredValue, double actualValue, double tolerancePercentage) {
        return (Math.abs(desiredValue - actualValue) < tolerancePercentage * desiredValue);
    }
    public static boolean approximatelyEqual(double desiredValue, double actualValue) {
        return approximatelyEqual(desiredValue, actualValue,0.0001);
    }
    public static boolean closeByOffset(double value1, double value2, double offset) {
        return (Math.abs(value1 - value2) <= offset);
    }

    // Code credit to GFG
    // https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
    static class Point
    {
        double x;
        double y;
        public Point(double x, double y)
        {
            this.x = x;
            this.y = y;
        }
    }
    // Given three collinear points p, q, r, the function checks if
// point q lies on line segment 'pr'
    static boolean onSegment(Point p, Point q, Point r)
    {
        if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
                q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y))
            return true;
        return false;
    }
    // To find orientation of ordered triplet (p, q, r).
// The function returns following values
// 0 --> p, q and r are collinear
// 1 --> Clockwise
// 2 --> Counterclockwise
    static int orientation(Point p, Point q, Point r)
    {
        // See https://www.geeksforgeeks.org/orientation-3-ordered-points/
        // for details of below formula.
        double val = (q.y - p.y) * (r.x - q.x) -
                (q.x - p.x) * (r.y - q.y);
        if (val == 0) return 0; // collinear
        return (val > 0)? 1: 2; // clock or counterclock wise
    }

    // The main function that returns true if line segment 'p1q1'
// and 'p2q2' intersect.
    static boolean doIntersect(Point p1, Point q1, Point p2, Point q2)
    {
        // Find the four orientations needed for general and
        // special cases
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);
        // General case
        if (o1 != o2 && o3 != o4)
            return true;
        // Special Cases
        // p1, q1 and p2 are collinear and p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;
        // p1, q1 and q2 are collinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;
        // p2, q2 and p1 are collinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;
        // p2, q2 and q1 are collinear and q1 lies on segment p2q2
        if (o4 == 0 && onSegment(p2, q1, q2)) return true;
        return false; // Doesn't fall in any of the above cases
    }

    public static boolean doLinesIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        Point p1 = new Point(x1, y1);
        Point q1 = new Point(x2, y2);
        Point p2 = new Point(x3,  y3);
        Point q2 = new Point(x4, y4);
        return doIntersect(p1, q1, p2, q2);
    }

    public static boolean doLinesIntersect(LinePosition linePosition1, LinePosition linePosition2) {
        Point p1 = new Point(linePosition1.getStartPos().getX(), linePosition1.getStartPos().getY());
        Point q1 = new Point(linePosition1.getEndPos().getX(), linePosition1.getEndPos().getY());
        Point p2 = new Point(linePosition2.getStartPos().getX(), linePosition2.getStartPos().getY());
        Point q2 = new Point(linePosition2.getEndPos().getX(), linePosition2.getEndPos().getY());
        return doIntersect(p1, q1, p2, q2);
    }

    public static double randomBetween(double min, double max) {
        return (Math.random()*(max-min)+min);
    }

    public static boolean randomBool(double odds) {
        return randomBetween(0, 1) < odds;
    }

    public static boolean randomBool() {
        return randomBool(0.5);
    }

    public static double sind(double theta) {
        return Math.sin(Math.toRadians(theta));
    }

    public static double cosd(double theta) {
        return Math.cos(Math.toRadians(theta));
    }

    public static double tand(double theta) {
        return Math.tan(Math.toRadians(theta));
    }

    public static double asind(double num) {
        return Math.toDegrees(Math.asin(num));
    }

    public static double acosd(double num) {
        return Math.toDegrees(Math.acos(num));
    }

    public static double atand(double num) {
        return Math.toDegrees(Math.atan(num));
    }

    public static double average(double... values) {
        double sum = 0;
        for (double num: values) {
            sum += num;
        }
        return sum/values.length;
    }

}
